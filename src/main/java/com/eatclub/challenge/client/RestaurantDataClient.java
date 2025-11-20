package com.eatclub.challenge.client;

import com.eatclub.challenge.model.domain.Restaurant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Client for fetching restaurant data from external API.
 */
@Component
@Slf4j
public class RestaurantDataClient {

    private static final String RESTAURANTS_API_URL = "https://eccdn.com.au/misc/challengedata.json";

    private final WebClient webClient;

    public RestaurantDataClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(RESTAURANTS_API_URL).build();
    }

    /**
     * Fetches all restaurants with deals from external API.
     *
     * @return list of restaurants, empty list if error occurs
     */
    public List<Restaurant> fetchRestaurants() {
        log.info("Fetching restaurant data from: {}", RESTAURANTS_API_URL);

        try {
            RestaurantApiResponse response = webClient.get()
                    .retrieve()
                    .bodyToMono(RestaurantApiResponse.class)
                    .block();

            int count = response != null && response.getRestaurants() != null
                    ? response.getRestaurants().size() : 0;
            log.info("Successfully fetched {} restaurants", count);

            return response != null ? response.getRestaurants() : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch restaurant data", e);
            throw new RuntimeException("Unable to fetch restaurant data", e);
        }
    }

    /**
     * Response wrapper matching external API structure.
     */
    @Data
    private static class RestaurantApiResponse {
        private List<Restaurant> restaurants;
    }
}