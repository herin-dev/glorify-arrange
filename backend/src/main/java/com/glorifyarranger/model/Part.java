package com.glorifyarranger.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Choir part for the arrangement.
 */
public enum Part {
    SOPRANO("S"),
    ALTO("A"),
    TENOR("T"),
    BASS("B");

    private final String shortCode;

    Part(String shortCode) {
        this.shortCode = shortCode;
    }

    @JsonValue
    public String getShortCode() {
        return shortCode;
    }

    @JsonCreator
    public static Part fromString(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim().toUpperCase();
        switch (value) {
            case "S":
            case "SOPRANO":
                return SOPRANO;
            case "A":
            case "ALTO":
                return ALTO;
            case "T":
            case "TENOR":
                return TENOR;
            case "B":
            case "BASS":
                return BASS;
            default:
                throw new IllegalArgumentException("Unknown part: " + value);
        }
    }
}
