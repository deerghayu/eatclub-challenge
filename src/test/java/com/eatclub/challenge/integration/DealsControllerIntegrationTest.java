package com.eatclub.challenge.integration;

import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.dto.DealResponse;
import com.eatclub.challenge.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Integration tests for Deals Controller with real data.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class DealsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @ParameterizedTest
    @CsvSource({
            "3:00pm, 8",
            "6:00pm, 9",
            "9:00pm, 9"
    })
    void getActiveDeals_validTime_returnsExpectedDeals(String timeOfDay, int expectedCount) {
        ResponseEntity<DealResponse> response = restTemplate.getForEntity(
                "/api/v1/deals?timeOfDay=" + timeOfDay,
                DealResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<DealDto> deals = response.getBody().getDeals();
        assertThat(deals)
                .isNotNull()
                .hasSize(expectedCount);

        // Validate structure of first deal
        if (!deals.isEmpty()) {
            DealDto firstDeal = deals.get(0);
            assertThat(firstDeal.getRestaurantObjectId()).isNotNull();
            assertThat(firstDeal.getRestaurantName()).isNotNull();
            assertThat(firstDeal.getDealObjectId()).isNotNull();
            assertThat(firstDeal.getDiscount()).isNotNull();
        }
    }

    @Test
    void getActiveDeals_invalidTimeFormat_returnsBadRequest() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/api/v1/deals?timeOfDay=invalid",
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).contains("Unable to parse time");
    }
}