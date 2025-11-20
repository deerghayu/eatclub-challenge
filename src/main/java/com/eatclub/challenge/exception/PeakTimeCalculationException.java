package com.eatclub.challenge.exception;

/**
 * Exception thrown when peak time calculation fails.
 */
public class PeakTimeCalculationException extends RuntimeException {

    public PeakTimeCalculationException(String message) {
        super(message);
    }

    public PeakTimeCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}