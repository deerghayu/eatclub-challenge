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
    void getAllDeals_returnsAllDeals() {
        // Given
        Deal deal1 = Deal.builder()
                .objectId("deal1")
                .discount("50")
                .dineIn("true")
                .lightning("false")
                .qtyLeft("10")
                .build();

        Deal deal2 = Deal.builder()
                .objectId("deal2")
                .discount("30")
                .dineIn("false")
                .lightning("true")
                .qtyLeft("5")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .objectId("rest1")
                .name("Test Restaurant")
                .address1("123 Test St")
                .suburb("Test Suburb")
                .open("9:00am")
                .close("5:00pm")
                .deals(List.of(deal1, deal2))
                .build();

        when(dataClient.fetchRestaurants()).thenReturn(List.of(restaurant));

        // When
        List<DealDto> result = dealService.getAllDeals();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Restaurant", result.get(0).getRestaurantName());
        assertEquals("deal1", result.get(0).getDealObjectId());
        assertEquals("50", result.get(0).getDiscount());
    }

    @Test
    void getAllDeals_emptyRestaurants_returnsEmptyList() {
        // Given
        when(dataClient.fetchRestaurants()).thenReturn(List.of());

        // When
        List<DealDto> result = dealService.getAllDeals();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}