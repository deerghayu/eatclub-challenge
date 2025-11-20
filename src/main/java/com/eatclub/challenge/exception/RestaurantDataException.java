package com.eatclub.challenge.exception;

/**
 * Exception thrown when there are issues fetching or processing restaurant data.
 */
public class RestaurantDataException extends RuntimeException {

    public RestaurantDataException(String message) {
        super(message);
    }

    public RestaurantDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
