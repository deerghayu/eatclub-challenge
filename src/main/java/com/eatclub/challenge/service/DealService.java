package com.eatclub.challenge.service;

import com.eatclub.challenge.client.RestaurantDataClient;
import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.exception.InvalidTimeFormatException;
import com.eatclub.challenge.exception.RestaurantDataException;
import com.eatclub.challenge.model.domain.Deal;
import com.eatclub.challenge.model.domain.Restaurant;
import com.eatclub.challenge.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

/**
 * Service for handling deal-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DealService {

    private final RestaurantDataClient dataClient;

    /**
     * Retrieves all active deals from all restaurants at specified time.
     *
     * @param timeOfDay time to query (e.g., "3:00pm", "15:00")
     * @return list of active deals
     * @throws InvalidTimeFormatException if timeOfDay format is invalid
     * @throws RestaurantDataException    if unable to fetch restaurant data
     */
    public List<DealDto> getActiveDeals(String timeOfDay) {
        if (timeOfDay == null || timeOfDay.isBlank()) {
            log.warn("Empty timeOfDay parameter received");
            throw new IllegalArgumentException("timeOfDay parameter is required");
        }

        LocalTime queryTime = TimeParser.parseTime(timeOfDay);
        List<Restaurant> restaurants = dataClient.fetchRestaurants();

        return restaurants.stream()
                .filter(this::hasValidData)
                .filter(restaurant -> isRestaurantOpen(restaurant, queryTime))
                .filter(restaurant -> restaurant.getDeals() != null && !restaurant.getDeals().isEmpty())
                .flatMap(restaurant -> restaurant.getDeals().stream()
                        .map(deal -> mapToDto(restaurant, deal)))
                .toList();
    }

    private boolean hasValidData(Restaurant restaurant) {
        return restaurant != null
                && restaurant.getOpen() != null
                && restaurant.getClose() != null;
    }

    private boolean isRestaurantOpen(Restaurant restaurant, LocalTime queryTime) {
        LocalTime openTime = TimeParser.parseTime(restaurant.getOpen());
        LocalTime closeTime = TimeParser.parseTime(restaurant.getClose());
        return TimeParser.isWithinOperatingHours(queryTime, openTime, closeTime);
    }

    private DealDto mapToDto(Restaurant restaurant, Deal deal) {
        return DealDto.builder()
                .restaurantObjectId(restaurant.getObjectId())
                .restaurantName(restaurant.getName())
                .restaurantAddress1(restaurant.getAddress1())
                .restaurantSuburb(restaurant.getSuburb())
                .restaurantOpen(restaurant.getOpen())
                .restaurantClose(restaurant.getClose())
                .dealObjectId(deal.getObjectId())
                .discount(deal.getDiscount())
                .dineIn(deal.getDineIn())
                .lightning(deal.getLightning())
                .qtyLeft(deal.getQtyLeft())
                .build();
    }
}