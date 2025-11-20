package com.eatclub.challenge.integration;

import com.eatclub.challenge.dto.DealResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class DealsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void getDeals_shouldReturnsAllDeals() {
        //When
        ResponseEntity<DealResponse> response =
                restTemplate.getForEntity("/api/v1/deals", DealResponse.class);

        //Then
        // Response validation
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getDeals());
        assertFalse(response.getBody().getDeals().isEmpty());

        // Structure validation
        var firstDeal = response.getBody().getDeals().get(0);
        assertNotNull(firstDeal.getRestaurantObjectId());
        assertNotNull(firstDeal.getRestaurantName());
        assertNotNull(firstDeal.getDealObjectId());
        assertNotNull(firstDeal.getDiscount());
    }
}