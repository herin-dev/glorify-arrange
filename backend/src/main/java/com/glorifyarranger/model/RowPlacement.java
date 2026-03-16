package com.glorifyarranger.model;

import java.util.List;

/**
 * Represents a single row on the stage and the final seat assignments for that row.
 */
public class RowPlacement {
    private int rowNumber;
    private boolean staggered;
    private Part leftPart;
    private Part rightPart;
    private List<Seat> seats;

    public RowPlacement() {
    }

    public RowPlacement(int rowNumber, boolean staggered, Part leftPart, Part rightPart, List<Seat> seats) {
        this.rowNumber = rowNumber;
        this.staggered = staggered;
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.seats = seats;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public boolean isStaggered() {
        return staggered;
    }

    public void setStaggered(boolean staggered) {
        this.staggered = staggered;
    }

    public Part getLeftPart() {
        return leftPart;
    }

    public void setLeftPart(Part leftPart) {
        this.leftPart = leftPart;
    }

    public Part getRightPart() {
        return rightPart;
    }

    public void setRightPart(Part rightPart) {
        this.rightPart = rightPart;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
