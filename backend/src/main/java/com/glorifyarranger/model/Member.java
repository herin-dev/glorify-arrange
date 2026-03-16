package com.glorifyarranger.model;

/**
 * Represents an individual choir member.
 */
public class Member {
    private String name;
    private Part part;
    private int heightCm;

    public Member() {
    }

    public Member(String name, Part part, int heightCm) {
        this.name = name;
        this.part = part;
        this.heightCm = heightCm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public int getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(int heightCm) {
        this.heightCm = heightCm;
    }
}
