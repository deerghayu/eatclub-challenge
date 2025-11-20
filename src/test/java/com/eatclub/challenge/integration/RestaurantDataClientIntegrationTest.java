package com.eatclub.challenge.integration;

import com.eatclub.challenge.client.RestaurantDataClient;
import com.eatclub.challenge.model.domain.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class RestaurantDataClientIntegrationTest {
    @Autowired
    RestaurantDataClient client;

    @Test
    void fetchRestaurants_shouldReturnData() {
        List<Restaurant> restaurants = client.fetchRestaurants();
        assertFalse(restaurants.isEmpty(), "Expected non-empty list of restaurants");
    }
}