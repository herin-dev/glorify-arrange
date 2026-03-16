package com.glorifyarranger.service;

import com.glorifyarranger.model.ArrangementResult;
import com.glorifyarranger.model.Member;
import com.glorifyarranger.model.Part;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrangementServiceTest {

    @Test
    void arrange_distributesEvenlyAcrossRowsAndParts() {
        List<Member> members = new ArrayList<>();
        // 3 rows expected: 2 front rows, 1 back row
        // Add 4 sopranos, 4 altos, 3 tenors, 3 basses
        for (int i = 1; i <= 4; i++) {
            members.add(new Member("S" + i, Part.SOPRANO, 160 + i));
            members.add(new Member("A" + i, Part.ALTO, 150 + i));
        }
        for (int i = 1; i <= 3; i++) {
            members.add(new Member("T" + i, Part.TENOR, 170 + i));
            members.add(new Member("B" + i, Part.BASS, 175 + i));
        }

        ArrangementService service = new ArrangementService();
        ArrangementResult result = service.arrange(members, 3);

        assertEquals(3, result.getRows().size());
        // Front rows should contain Sop/Alt, back row contains Ten/Bass
        assertEquals(0, result.getUnplacedMembers().size());

        // verify each row has correct part assignments
        assertEquals(Part.ALTO, result.getRows().get(0).getLeftPart());
        assertEquals(Part.SOPRANO, result.getRows().get(0).getRightPart());
        assertEquals(Part.ALTO, result.getRows().get(1).getLeftPart());
        assertEquals(Part.SOPRANO, result.getRows().get(1).getRightPart());
        assertEquals(Part.BASS, result.getRows().get(2).getLeftPart());
        assertEquals(Part.TENOR, result.getRows().get(2).getRightPart());
    }
}
