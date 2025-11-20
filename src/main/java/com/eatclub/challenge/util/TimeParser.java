package com.eatclub.challenge.util;


import com.eatclub.challenge.exception.InvalidTimeFormatException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Utility class for parsing and comparing time strings.
 */
@UtilityClass
@Slf4j
public class TimeParser {

    private static final DateTimeFormatter FORMATTER_12H_NO_SPACE =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("h:mma")
                    .toFormatter();
    private static final DateTimeFormatter FORMATTER_12H_WITH_SPACE =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("h:mm a")
                    .toFormatter();
    private static final DateTimeFormatter FORMATTER_24H_SINGLE =
            DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter FORMATTER_24H_DOUBLE =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            FORMATTER_12H_NO_SPACE,      // "7:00pm" or "7:00PM"
            FORMATTER_12H_WITH_SPACE,    // "7:00 pm" or "7:00 PM"
            FORMATTER_24H_SINGLE,        // "19:00"
            FORMATTER_24H_DOUBLE         // "09:00"
    );

    /**
     * Parse time string to LocalTime, trying multiple formats.
     *
     * @param timeStr time string to parse
     * @return parsed LocalTime
     * @throws InvalidTimeFormatException if unable to parse
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            throw new InvalidTimeFormatException("Time cannot be null or empty");
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalTime.parse(timeStr.trim(), formatter);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }

        throw new InvalidTimeFormatException(
                "Unable to parse time: '" + timeStr +
                        "'. Expected formats: 3:00pm, 3:00 pm, 15:00, etc."
        );
    }

    /**
     * Check if queryTime falls within [openTime, closeTime].
     * Handles midnight wraparound for restaurants operating past midnight.
     *
     * @param queryTime time to check
     * @param openTime  opening time
     * @param closeTime closing time
     * @return true if queryTime is within operating hours
     */
    public static boolean isWithinOperatingHours(
            LocalTime queryTime,
            LocalTime openTime,
            LocalTime closeTime
    ) {
        // Simple case: open < close (e.g., 9:00am - 5:00pm)
        if (openTime.isBefore(closeTime)) {
            return !queryTime.isBefore(openTime) &&
                    !queryTime.isAfter(closeTime);
        }

        // Edge case: wraps midnight (e.g., 10:00pm - 2:00am)
        // Restaurant is open if time >= open OR time <= close
        return !queryTime.isBefore(openTime) ||
                !queryTime.isAfter(closeTime);
    }
}