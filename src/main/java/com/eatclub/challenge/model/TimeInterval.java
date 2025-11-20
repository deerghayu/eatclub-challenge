package com.eatclub.challenge.model;

import lombok.Data;

/**
 * Represents a time interval in minutes since midnight.
 * Used internally for efficient peak time calculations.
 */
@Data
public class TimeInterval {
    private int startMinutes;
    private int endMinutes;
    private int dealCount; // Number of active deals during this interval

    public TimeInterval(int startMinutes, int endMinutes, int dealCount) {
        this.startMinutes = startMinutes;
        this.endMinutes = endMinutes;
        this.dealCount = dealCount;
    }

    /**
     * Calculate the length of this interval in minutes.
     */
    public int length() {
        return endMinutes - startMinutes;
    }

    /**
     * Extend this interval's end time.
     */
    public void extendTo(int newEnd) {
        this.endMinutes = newEnd;
    }
}