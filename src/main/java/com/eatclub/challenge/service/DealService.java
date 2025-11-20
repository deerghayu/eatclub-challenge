package com.eatclub.challenge.service;

import com.eatclub.challenge.client.RestaurantDataClient;
import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.model.domain.Deal;
import com.eatclub.challenge.model.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling deal-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DealService {

    private final RestaurantDataClient dataClient;

    /**
     * Retrieves all deals from all restaurants.
     *
     * @return list of DealDto representing all deals
     */
    public List<DealDto> getAllDeals() {
        List<Restaurant> restaurants = dataClient.fetchRestaurants();

        return restaurants.stream()
                .flatMap(restaurant -> restaurant.getDeals().stream()
                        .map(deal -> mapToDto(restaurant, deal)))
                .collect(Collectors.toList());
    }

    /**
     * Maps a Restaurant and Deal to a DealDto.
     *
     * @param restaurant the restaurant
     * @param deal       the deal
     * @return the mapped DealDto
     */
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