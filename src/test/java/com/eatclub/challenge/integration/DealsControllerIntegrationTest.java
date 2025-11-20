package com.eatclub.challenge.integration;

import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.dto.DealResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class DealsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getActiveDeals_with3pm_returnsActiveDeals() {
        // When
        ResponseEntity<DealResponse> response = restTemplate.getForEntity(
                "/api/v1/deals?timeOfDay=3:00pm",
                DealResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<DealDto> deals = response.getBody().getDeals();
        assertNotNull(deals);
        assertFalse(deals.isEmpty());

        assertEquals(8, deals.size(), "Expected 8 active deals at 3:00pm");

        var firstDeal = deals.get(0);
        assertNotNull(firstDeal.getRestaurantObjectId());
        assertNotNull(firstDeal.getRestaurantName());
        assertNotNull(firstDeal.getDealObjectId());
        assertNotNull(firstDeal.getDiscount());
    }

    @Test
    void getActiveDeals_with6pm_returnsActiveDeals() {
        // When
        ResponseEntity<DealResponse> response = restTemplate.getForEntity(
                "/api/v1/deals?timeOfDay=6:00pm",
                DealResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<DealDto> deals = response.getBody().getDeals();
        assertNotNull(deals);
        assertEquals(9, deals.size(), "Expected 9 active deals at 6:00pm");
    }

    @Test
    void getActiveDeals_with9pm_returnsActiveDeals() {
        // When
        ResponseEntity<DealResponse> response = restTemplate.getForEntity(
                "/api/v1/deals?timeOfDay=9:00pm",
                DealResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<DealDto> deals = response.getBody().getDeals();
        assertNotNull(deals);
        assertEquals(9, deals.size(), "Expected 9 active deals at 9:00pm");
    }
}