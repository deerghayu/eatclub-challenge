package com.eatclub.challenge.exception;

/**
 * Exception thrown when time format is invalid or cannot be parsed.
 */
public class InvalidTimeFormatException extends RuntimeException {

    public InvalidTimeFormatException(String message) {
        super(message);
    }
}