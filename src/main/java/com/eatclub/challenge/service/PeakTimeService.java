package com.eatclub.challenge.service;

import com.eatclub.challenge.client.RestaurantDataClient;
import com.eatclub.challenge.dto.PeakTimeResponse;
import com.eatclub.challenge.model.TimeInterval;
import com.eatclub.challenge.model.domain.Restaurant;
import com.eatclub.challenge.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Service for calculating peak time windows when most restaurant deals are available.
 * Uses an event-based sweep line algorithm: O(n log n) time, O(n) space.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PeakTimeService {

    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("h:mma");
    private static final int MINUTES_PER_DAY = 1440;
    private static final int MIDNIGHT = 0;

    private final RestaurantDataClient dataClient;

    /**
     * Calculates when the maximum number of deals are simultaneously available.
     */
    public PeakTimeResponse calculatePeakTime() {
        log.info("Calculating peak time window");

        List<Restaurant> restaurants = dataClient.fetchRestaurants();

        if (restaurants.isEmpty()) {
            log.warn("No restaurants found");
            return new PeakTimeResponse(null, null);
        }

        return findPeakInterval(restaurants)
                .map(this::toResponse)
                .orElseGet(() -> {
                    log.warn("No valid peak found");
                    return new PeakTimeResponse(null, null);
                });
    }

    /**
     * Finds the time interval with maximum concurrent deals.
     */
    private Optional<TimeInterval> findPeakInterval(List<Restaurant> restaurants) {
        Map<Integer, Integer> timeEvents = createTimeEvents(restaurants);

        if (timeEvents.isEmpty()) {
            return Optional.empty();
        }

        //Produces all maximal intervals where activeDeals == globalMaxDeals
        List<TimeInterval> peaks = findPeakWindows(timeEvents);
        return selectOptimalPeak(peaks);
    }

    /**
     * Creates time-based events from restaurant operating hours.
     * Each event represents a change in the number of active deals.
     */
    private Map<Integer, Integer> createTimeEvents(List<Restaurant> restaurants) {
        Map<Integer, Integer> events = new HashMap<>();

        restaurants.stream()
                .filter(this::hasValidDeals)
                .forEach(restaurant -> addRestaurantEvents(events, restaurant));

        log.debug("Created {} time events from {} restaurants", events.size(), restaurants.size());
        return events;
    }

    /**
     * Adds opening and closing events for a restaurant.
     */
    private void addRestaurantEvents(Map<Integer, Integer> events, Restaurant restaurant) {
        try {
            TimeWindow window = parseTimeWindow(restaurant);
            int dealCount = restaurant.getDeals().size();

            if (window.spansMidnight()) {
                // Split midnight-spanning window into two segments
                addEvent(events, window.openMinutes, dealCount);
                addEvent(events, MINUTES_PER_DAY, -dealCount);
                addEvent(events, MIDNIGHT, dealCount);
                addEvent(events, window.closeMinutes, -dealCount);
            } else {
                // Simple same-day window
                addEvent(events, window.openMinutes, dealCount);
                addEvent(events, window.closeMinutes, -dealCount);
            }
        } catch (Exception e) {
            log.debug("Skipping restaurant {}: {}", restaurant.getName(), e.getMessage());
        }
    }

    /**
     * Finds all time windows with the maximum number of concurrent deals.
     */
    private List<TimeInterval> findPeakWindows(Map<Integer, Integer> timeEvents) {
        List<Integer> sortedTimes = timeEvents.keySet().stream()
                .sorted()
                .toList();

        PeakTracker tracker = new PeakTracker();

        IntStream.range(0, sortedTimes.size() - 1)
                .forEach(i -> {
                    int currentTime = sortedTimes.get(i);
                    int nextTime = sortedTimes.get(i + 1);

                    tracker.applyDelta(timeEvents.get(currentTime));
                    tracker.recordInterval(currentTime, nextTime);
                });

        return tracker.getPeaks();
    }

    /**
     * Selects the best peak using tie-breaking rules:
     * 1. Earliest start time
     * 2. Longest duration if starts are equal
     */
    private Optional<TimeInterval> selectOptimalPeak(List<TimeInterval> peaks) {
        return peaks.stream()
                .min(Comparator.comparingInt(TimeInterval::getStartMinutes)
                        .thenComparing(Comparator.comparingInt(TimeInterval::length).reversed()));
    }

    /**
     * Formats a time interval for the response.
     */
    private PeakTimeResponse toResponse(TimeInterval interval) {
        String start = formatMinutes(interval.getStartMinutes());
        String end = formatMinutes(interval.getEndMinutes());

        log.info("Peak time: {} - {} ({} minutes)", start, end, interval.length());
        return new PeakTimeResponse(start, end);
    }

    /**
     * Checks if a restaurant has valid deals.
     */
    private boolean hasValidDeals(Restaurant restaurant) {
        return restaurant.getDeals() != null && !restaurant.getDeals().isEmpty();
    }

    private TimeWindow parseTimeWindow(Restaurant restaurant) {
        LocalTime open = TimeParser.parseTime(restaurant.getOpen());
        LocalTime close = TimeParser.parseTime(restaurant.getClose());
        return new TimeWindow(toMinutes(open), toMinutes(close));
    }

    private void addEvent(Map<Integer, Integer> events, int time, int delta) {
        events.merge(time, delta, Integer::sum);
    }

    private int toMinutes(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    private String formatMinutes(int minutes) {
        int boundedMinutes = Math.min(minutes, MINUTES_PER_DAY - 1);
        LocalTime time = LocalTime.of(boundedMinutes / 60, boundedMinutes % 60);
        return time.format(OUTPUT_FORMAT).toLowerCase();
    }

    /**
     * Inner class to track peak intervals during the sweep.
     */
    private static class PeakTracker {
        private int currentCount = 0;
        private int maxCount = 0;
        private final List<TimeInterval> peaks = new ArrayList<>();

        void applyDelta(int delta) {
            currentCount += delta;
        }

        void recordInterval(int start, int end) {
            // Skip if no active deals
            if (currentCount <= 0) return;

            if (currentCount > maxCount) {
                // New maximum found
                maxCount = currentCount;
                peaks.clear();
                peaks.add(new TimeInterval(start, end, currentCount));
            } else if (currentCount == maxCount) {
                // Equal to max - merge if contiguous, otherwise add new
                if (!peaks.isEmpty() && peaks.get(peaks.size() - 1).getEndMinutes() == start) {
                    peaks.get(peaks.size() - 1).extendTo(end);
                } else {
                    peaks.add(new TimeInterval(start, end, currentCount));
                }
            }
        }

        List<TimeInterval> getPeaks() {
            return new ArrayList<>(peaks);
        }
    }

    /**
     * Represents a restaurant's operating time window in minutes since midnight.
     */
    private record TimeWindow(int openMinutes, int closeMinutes) {
        boolean spansMidnight() {
            return openMinutes > closeMinutes;
        }
    }
}