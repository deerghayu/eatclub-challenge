package com.eatclub.challenge.service;

import com.eatclub.challenge.client.RestaurantDataClient;
import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.model.domain.Deal;
import com.eatclub.challenge.model.domain.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private RestaurantDataClient dataClient;

    @InjectMocks
    private DealService dealService;

    @Test
    void getActiveDeals_restaurantOpen_returnsDeals() {
        // Given
        Deal deal = Deal.builder()
                .objectId("deal1")
                .discount("50")
                .dineIn("true")
                .lightning("false")
                .qtyLeft("10")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .objectId("rest1")
                .name("Test Restaurant")
                .address1("123 Test St")
                .suburb("Test Suburb")
                .open("9:00am")
                .close("5:00pm")
                .deals(List.of(deal))
                .build();

        when(dataClient.fetchRestaurants()).thenReturn(List.of(restaurant));

        // When
        List<DealDto> result = dealService.getActiveDeals("3:00pm");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Restaurant", result.get(0).getRestaurantName());
        assertEquals("deal1", result.get(0).getDealObjectId());
    }

    @Test
    void getActiveDeals_restaurantClosed_returnsEmpty() {
        // Given
        Deal deal = Deal.builder()
                .objectId("deal1")
                .discount("50")
                .dineIn("true")
                .lightning("false")
                .qtyLeft("10")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .objectId("rest1")
                .name("Test Restaurant")
                .address1("123 Test St")
                .suburb("Test Suburb")
                .open("9:00am")
                .close("5:00pm")
                .deals(List.of(deal))
                .build();

        when(dataClient.fetchRestaurants()).thenReturn(List.of(restaurant));

        // When
        List<DealDto> result = dealService.getActiveDeals("8:00pm");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveDeals_midnightWraparound_returnsDeals() {
        // Given
        Deal deal = Deal.builder()
                .objectId("deal1")
                .discount("50")
                .dineIn("true")
                .lightning("false")
                .qtyLeft("10")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .objectId("rest1")
                .name("Late Night Restaurant")
                .address1("123 Test St")
                .suburb("Test Suburb")
                .open("10:00pm")
                .close("2:00am")
                .deals(List.of(deal))
                .build();

        when(dataClient.fetchRestaurants()).thenReturn(List.of(restaurant));

        // When
        List<DealDto> result = dealService.getActiveDeals("1:00am");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Late Night Restaurant", result.get(0).getRestaurantName());
    }
}