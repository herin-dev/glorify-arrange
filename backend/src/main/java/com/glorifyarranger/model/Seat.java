package com.glorifyarranger.model;

/**
 * A single position on the stage grid.
 */
public class Seat {
    private int row;
    private double column;
    private Member member;

    public Seat() {
    }

    public Seat(int row, double column, Member member) {
        this.row = row;
        this.column = column;
        this.member = member;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public double getColumn() {
        return column;
    }

    public void setColumn(double column) {
        this.column = column;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
