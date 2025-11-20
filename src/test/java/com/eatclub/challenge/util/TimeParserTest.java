package com.eatclub.challenge.util;

import com.eatclub.challenge.exception.InvalidTimeFormatException;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeParserTest {

    @Test
    void parseTime_12HourFormat_success() {
        assertEquals(LocalTime.of(15, 0), TimeParser.parseTime("3:00pm"));
        assertEquals(LocalTime.of(15, 0), TimeParser.parseTime("3:00 PM"));
        assertEquals(LocalTime.of(9, 0), TimeParser.parseTime("9:00am"));
    }

    @Test
    void parseTime_24HourFormat_success() {
        assertEquals(LocalTime.of(15, 0), TimeParser.parseTime("15:00"));
        assertEquals(LocalTime.of(9, 0), TimeParser.parseTime("09:00"));
    }

    @Test
    void parseTime_invalidFormat_throwsException() {
        assertThrows(InvalidTimeFormatException.class,
                () -> TimeParser.parseTime("invalid"));
        assertThrows(InvalidTimeFormatException.class,
                () -> TimeParser.parseTime("25:00"));
    }

    @Test
    void parseTime_nullOrEmpty_throwsException() {
        assertThrows(InvalidTimeFormatException.class,
                () -> TimeParser.parseTime(null));
        assertThrows(InvalidTimeFormatException.class,
                () -> TimeParser.parseTime(""));
        assertThrows(InvalidTimeFormatException.class,
                () -> TimeParser.parseTime("   "));
    }

    @Test
    void isWithinOperatingHours_normalHours_true() {
        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(17, 0);
        LocalTime query = LocalTime.of(12, 0);

        assertTrue(TimeParser.isWithinOperatingHours(query, open, close));
    }

    @Test
    void isWithinOperatingHours_normalHours_false() {
        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(17, 0);
        LocalTime query = LocalTime.of(20, 0);

        assertFalse(TimeParser.isWithinOperatingHours(query, open, close));
    }

    @Test
    void isWithinOperatingHours_midnightWraparound_true() {
        LocalTime open = LocalTime.of(22, 0);   // 10pm
        LocalTime close = LocalTime.of(2, 0);   // 2am
        LocalTime query = LocalTime.of(1, 0);   // 1am

        assertTrue(TimeParser.isWithinOperatingHours(query, open, close));
    }

    @Test
    void isWithinOperatingHours_midnightWraparound_lateNight_true() {
        LocalTime open = LocalTime.of(22, 0);   // 10pm
        LocalTime close = LocalTime.of(2, 0);   // 2am
        LocalTime query = LocalTime.of(23, 0);  // 11pm

        assertTrue(TimeParser.isWithinOperatingHours(query, open, close));
    }

    @Test
    void isWithinOperatingHours_midnightWraparound_false() {
        LocalTime open = LocalTime.of(22, 0);   // 10pm
        LocalTime close = LocalTime.of(2, 0);   // 2am
        LocalTime query = LocalTime.of(12, 0);  // 12pm

        assertFalse(TimeParser.isWithinOperatingHours(query, open, close));
    }

    @Test
    void isWithinOperatingHours_exactOpenTime_true() {
        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(17, 0);
        LocalTime query = LocalTime.of(9, 0);

        assertTrue(TimeParser.isWithinOperatingHours(query, open, close));
    }

    @Test
    void isWithinOperatingHours_exactCloseTime_true() {
        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(17, 0);
        LocalTime query = LocalTime.of(17, 0);

        assertTrue(TimeParser.isWithinOperatingHours(query, open, close));
    }
}