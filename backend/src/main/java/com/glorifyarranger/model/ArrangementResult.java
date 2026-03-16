package com.glorifyarranger.model;

import java.util.List;

/**
 * Result of the arrangement algorithm.
 */
public class ArrangementResult {
    private int targetRows;
    private List<RowPlacement> rows;
    private List<Member> unplacedMembers;

    public ArrangementResult() {
    }

    public ArrangementResult(int targetRows, List<RowPlacement> rows, List<Member> unplacedMembers) {
        this.targetRows = targetRows;
        this.rows = rows;
        this.unplacedMembers = unplacedMembers;
    }

    public int getTargetRows() {
        return targetRows;
    }

    public void setTargetRows(int targetRows) {
        this.targetRows = targetRows;
    }

    public List<RowPlacement> getRows() {
        return rows;
    }

    public void setRows(List<RowPlacement> rows) {
        this.rows = rows;
    }

    public List<Member> getUnplacedMembers() {
        return unplacedMembers;
    }

    public void setUnplacedMembers(List<Member> unplacedMembers) {
        this.unplacedMembers = unplacedMembers;
    }
}
