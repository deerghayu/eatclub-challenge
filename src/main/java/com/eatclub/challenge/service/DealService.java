package com.eatclub.challenge.service;

import com.eatclub.challenge.client.RestaurantDataClient;
import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.dto.DealResponse;
import com.eatclub.challenge.exception.InvalidTimeFormatException;
import com.eatclub.challenge.exception.RestaurantDataException;
import com.eatclub.challenge.model.domain.Deal;
import com.eatclub.challenge.model.domain.Restaurant;
import com.eatclub.challenge.util.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    /**
     * Retrieves active deals with pagination support.
     *
     * @param timeOfDay time to query (e.g., "3:00pm", "15:00")
     * @param pageable  pagination parameters
     * @return paginated response with deals and metadata
     * @throws InvalidTimeFormatException if timeOfDay format is invalid
     * @throws RestaurantDataException    if unable to fetch restaurant data
     */
    public DealResponse getActiveDeals(String timeOfDay, Pageable pageable) {
        List<DealDto> allDeals = getActiveDeals(timeOfDay);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allDeals.size());

        Page<DealDto> page;
        if (start > allDeals.size()) {
            page = new PageImpl<>(List.of(), pageable, allDeals.size());
        } else {
            List<DealDto> pageContent = allDeals.subList(start, end);
            page = new PageImpl<>(pageContent, pageable, allDeals.size());
        }

        return DealResponse.builder()
                .deals(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
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