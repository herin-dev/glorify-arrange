package com.glorifyarranger.service;

import com.glorifyarranger.model.ArrangementResult;
import com.glorifyarranger.model.Member;
import com.glorifyarranger.model.Part;
import com.glorifyarranger.model.RowPlacement;
import com.glorifyarranger.model.Seat;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core placement logic for the Glorify-Arranger stage layout.
 *
 * <p>Highlights:
 * - Supports dynamic targetRows with front/back half splitting (lower parts in front, upper parts in back)
 * - Maintains strict part zoning (4 quadrants) and avoids mixing parts
 * - Provides mirror-key sorting within each part zone (taller toward outer edge)
 * - Applies staggered row placement (even rows shift and carry one fewer seat)
 */
@Service
public class ArrangementService {

    private static final double CENTER_RIGHT = 11.0;

    /**
     * Arrange members into a stage grid.
     *
     * @param members    all members to place
     * @param targetRows total number of rows to use (1..N)
     * @return placement result including rows and any unplaced members
     */
    public ArrangementResult arrange(List<Member> members, int targetRows) {
        if (targetRows < 1) {
            throw new IllegalArgumentException("targetRows must be >= 1");
        }

        Map<Part, List<Member>> membersByPart = splitByPart(members);
        membersByPart.values().forEach(list -> list.sort(Comparator.comparingInt(Member::getHeightCm).reversed()));

        int frontRows = (targetRows + 1) / 2; // 앞쪽 절반(소수일때 올림)

        // 각 줄별 양쪽 용량 (짝수 행은 한 칸 적게 배치)
        List<Integer> leftCapacities = new ArrayList<>(targetRows);
        List<Integer> rightCapacities = new ArrayList<>(targetRows);
        for (int row = 1; row <= targetRows; row++) {
            boolean staggered = (row % 2) == 0;
            leftCapacities.add(10);
            rightCapacities.add(staggered ? 9 : 10);
        }

        List<List<Member>> altoRows = distributeToRows(membersByPart.get(Part.ALTO), leftCapacities.subList(0, frontRows));
        List<List<Member>> sopranoRows = distributeToRows(membersByPart.get(Part.SOPRANO), rightCapacities.subList(0, frontRows));
        List<List<Member>> bassRows = distributeToRows(membersByPart.get(Part.BASS), leftCapacities.subList(frontRows, targetRows));
        List<List<Member>> tenorRows = distributeToRows(membersByPart.get(Part.TENOR), rightCapacities.subList(frontRows, targetRows));

        // Collect unplaced members
        List<Member> unplaced = new ArrayList<>();
        unplaced.addAll(getOverflow(membersByPart.get(Part.ALTO), leftCapacities.subList(0, frontRows)));
        unplaced.addAll(getOverflow(membersByPart.get(Part.SOPRANO), rightCapacities.subList(0, frontRows)));
        unplaced.addAll(getOverflow(membersByPart.get(Part.BASS), leftCapacities.subList(frontRows, targetRows)));
        unplaced.addAll(getOverflow(membersByPart.get(Part.TENOR), rightCapacities.subList(frontRows, targetRows)));

        List<RowPlacement> rows = new ArrayList<>();
        for (int row = 1; row <= targetRows; row++) {
            boolean isFront = row <= frontRows;
            Part leftPart = isFront ? Part.ALTO : Part.BASS;
            Part rightPart = isFront ? Part.SOPRANO : Part.TENOR;
            boolean staggered = (row % 2) == 0;

            List<Member> leftMembers = isFront ? altoRows.get(row - 1) : bassRows.get(row - frontRows - 1);
            List<Member> rightMembers = isFront ? sopranoRows.get(row - 1) : tenorRows.get(row - frontRows - 1);

            List<Seat> seats = buildSeatMapping(row, staggered, leftMembers, rightMembers);
            rows.add(new RowPlacement(row, staggered, leftPart, rightPart, seats));
        }

        return new ArrangementResult(targetRows, rows, unplaced);
    }

    private static List<List<Member>> distributeToRows(List<Member> sortedMembers, List<Integer> capacities) {
        List<List<Member>> result = new ArrayList<>(capacities.size());
        int total = sortedMembers.size();
        int index = 0;
        for (int i = 0; i < capacities.size(); i++) {
            int capacity = capacities.get(i);
            int remainingRows = capacities.size() - i;
            int remainingMembers = total - index;
            int desired = (int) Math.ceil((double) remainingMembers / Math.max(1, remainingRows));
            int take = Math.min(capacity, desired);
            List<Member> rowMembers = new ArrayList<>(take);
            for (int j = 0; j < take && index < total; j++) {
                rowMembers.add(sortedMembers.get(index++));
            }
            result.add(rowMembers);
        }
        return result;
    }

    private static List<Member> getOverflow(List<Member> sortedMembers, List<Integer> capacities) {
        int totalCapacity = capacities.stream().mapToInt(Integer::intValue).sum();
        if (sortedMembers.size() <= totalCapacity) {
            return List.of();
        }
        return new ArrayList<>(sortedMembers.subList(totalCapacity, sortedMembers.size()));
    }

    private static Map<Part, List<Member>> splitByPart(List<Member> members) {
        Map<Part, List<Member>> map = new HashMap<>();
        for (Part part : Part.values()) {
            map.put(part, new ArrayList<>());
        }
        if (members != null) {
            for (Member m : members) {
                if (m != null && m.getPart() != null) {
                    map.get(m.getPart()).add(m);
                }
            }
        }
        return map;
    }

    private List<Seat> buildSeatMapping(int row, boolean staggered, List<Member> leftMembers, List<Member> rightMembers) {
        List<Seat> seats = new ArrayList<>();

        // LEFT side: columns 1..10 (or 1.5..10.5 if staggered).
        // Taller singers should be further from the center (toward the left edge), so we place in descending height.
        List<Member> sortedLeft = new ArrayList<>(leftMembers);
        sortedLeft.sort(Comparator.comparingInt(Member::getHeightCm).reversed());

        for (int i = 0; i < sortedLeft.size(); i++) {
            double col = 1 + i;
            if (staggered) {
                col += 0.5; // shift inward for staggered rows
            }
            Seat seat = new Seat(row, col, sortedLeft.get(i));
            seats.add(seat);
        }

        // RIGHT side: columns 11..20 (or 11.5..19.5 if staggered). Tallest on the right.
        List<Member> sortedRight = new ArrayList<>(rightMembers);
        sortedRight.sort(Comparator.comparingInt(Member::getHeightCm).reversed());

        int rightCount = sortedRight.size();
        for (int i = 0; i < rightCount; i++) {
            // Place tallest at the far right.
            int reverseIndex = rightCount - 1 - i;
            Member member = sortedRight.get(i);

            double col;
            if (staggered) {
                // shift half a step toward center and allow one fewer seat (columns 11.5..19.5)
                col = CENTER_RIGHT + 0.5 + reverseIndex;
            } else {
                col = CENTER_RIGHT + reverseIndex;
            }

            Seat seat = new Seat(row, col, member);
            seats.add(seat);
        }

        return seats;
    }
}
