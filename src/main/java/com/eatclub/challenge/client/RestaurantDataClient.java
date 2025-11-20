package com.eatclub.challenge.client;

import com.eatclub.challenge.exception.RestaurantDataException;
import com.eatclub.challenge.model.domain.Restaurant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

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
     * @return list of restaurants
     * @throws RestaurantDataException if unable to fetch data
     */
    public List<Restaurant> fetchRestaurants() {
        log.info("Fetching restaurant data from: {}", RESTAURANTS_API_URL);

        try {
            RestaurantApiResponse response = webClient.get()
                    .retrieve()
                    .bodyToMono(RestaurantApiResponse.class)
                    .block();

            if (response == null || response.getRestaurants() == null) {
                log.warn("Received empty response from restaurant API");
                throw new RestaurantDataException("Restaurant API returned empty response");
            }

            int count = response.getRestaurants().size();
            log.info("Successfully fetched {} restaurants", count);

            return response.getRestaurants();
        } catch (WebClientException e) {
            log.error("Failed to fetch restaurant data: {}", e.getMessage());
            throw new RestaurantDataException("Unable to connect to restaurant data service", e);
        } catch (Exception e) {
            log.error("Unexpected error fetching restaurant data", e);
            throw new RestaurantDataException("Failed to fetch restaurant data", e);
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