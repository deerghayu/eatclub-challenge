package com.eatclub.challenge.service;

import com.eatclub.challenge.client.RestaurantDataClient;
import com.eatclub.challenge.dto.PeakTimeResponse;
import com.eatclub.challenge.model.domain.Deal;
import com.eatclub.challenge.model.domain.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PeakTimeService.
 * Validates the event-based sweep-line algorithm with event merging.
 */
@ExtendWith(MockitoExtension.class)
class PeakTimeServiceTest {

    @Mock
    private RestaurantDataClient dataClient;

    private PeakTimeService peakTimeService;

    @BeforeEach
    void setUp() {
        peakTimeService = new PeakTimeService(dataClient);
    }

    @Test
    void calculatePeakTime_multiplePeaksWithSameCount_returnsEarliestPeak() {
        // Two periods with same count: 11am-1pm (4 deals) and 5pm-7pm (4 deals)
        Restaurant r1 = createRestaurant("R1", "11:00am", "1:00pm", 4);
        Restaurant r2 = createRestaurant("R2", "5:00pm", "7:00pm", 4);

        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // Should pick earlier time (11am)
        assertThat(response.getPeakTimeStart()).isEqualTo("11:00am");
        assertThat(response.getPeakTimeEnd()).isEqualTo("1:00pm");
    }

    @Test
    void calculatePeakTime_peaksWithSameStartTime_returnsLongestDuration() {
        // Overlapping periods starting at same time
        // Both start at 12pm but R2 extends longer
        Restaurant r1 = createRestaurant("R1", "12:00pm", "2:00pm", 3);
        Restaurant r2 = createRestaurant("R2", "12:00pm", "4:00pm", 3);

        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // Peak should be 12pm-2pm (6 deals total from both)
        assertThat(response.getPeakTimeStart()).isEqualTo("12:00pm");
        assertThat(response.getPeakTimeEnd()).isEqualTo("2:00pm");
    }

    @Test
    void calculatePeakTime_contiguousPeakIntervals_mergesIntoSinglePeak() {
        // Create a scenario where peak extends across multiple adjacent intervals
        Restaurant r1 = createRestaurant("R1", "10:00am", "2:00pm", 2);
        Restaurant r2 = createRestaurant("R2", "12:00pm", "4:00pm", 2);
        Restaurant r3 = createRestaurant("R3", "2:00pm", "6:00pm", 2);

        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2, r3));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // Peak is 12pm-4pm: 12-2pm has 4 deals (R1+R2), 2-4pm has 4 deals (R2+R3)
        // These are contiguous and merged
        assertThat(response.getPeakTimeStart()).isEqualTo("12:00pm");
        assertThat(response.getPeakTimeEnd()).isEqualTo("4:00pm");
    }

    @Test
    void calculatePeakTime_complexOverlappingHours_findsCorrectPeak() {
        // Complex pattern with multiple overlaps
        Restaurant r1 = createRestaurant("R1", "9:00am", "11:00am", 3);   // 3 deals
        Restaurant r2 = createRestaurant("R2", "10:00am", "2:00pm", 5);   // 5 deals
        Restaurant r3 = createRestaurant("R3", "11:00am", "3:00pm", 4);   // 4 deals
        Restaurant r4 = createRestaurant("R4", "1:00pm", "5:00pm", 2);    // 2 deals

        // Peak should be 1pm-2pm with 11 deals (R2:5 + R3:4 + R4:2)
        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2, r3, r4));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        assertThat(response.getPeakTimeStart()).isEqualTo("1:00pm");
        assertThat(response.getPeakTimeEnd()).isEqualTo("2:00pm");
    }

    @Test
    void calculatePeakTime_midnightWraparound_splitsAndHandlesCorrectly() {
        // R1: 10pm-2am (wraps midnight) - 5 deals
        // Should be split into [10pm-midnight] and [midnight-2am]
        Restaurant r1 = createRestaurant("R1", "10:00pm", "2:00am", 5);
        Restaurant r2 = createRestaurant("R2", "6:00pm", "11:00pm", 3);

        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // Peak should be 10pm-11pm with 8 deals total
        assertThat(response.getPeakTimeStart()).isEqualTo("10:00pm");
        assertThat(response.getPeakTimeEnd()).isEqualTo("11:00pm");
    }

    @Test
    void calculatePeakTime_restaurantsWithNoDeals_ignoresThemInCalculation() {
        Restaurant r1 = createRestaurant("R1", "11:00am", "2:00pm", 0); // No deals
        Restaurant r2 = createRestaurant("R2", "12:00pm", "3:00pm", 5);
        Restaurant r3 = createRestaurant("R3", "1:00pm", "4:00pm", 0);  // No deals

        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2, r3));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // Should only consider R2
        assertThat(response.getPeakTimeStart()).isEqualTo("12:00pm");
        assertThat(response.getPeakTimeEnd()).isEqualTo("3:00pm");
    }

    @Test
    void calculatePeakTime_invalidTimeFormats_skipsInvalidRestaurants() {
        Restaurant r1 = createRestaurant("R1", "invalid", "2:00pm", 3);
        Restaurant r2 = createRestaurant("R2", "12:00pm", "4:00pm", 5);

        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // Should skip R1 and only process R2
        assertThat(response.getPeakTimeStart()).isEqualTo("12:00pm");
        assertThat(response.getPeakTimeEnd()).isEqualTo("4:00pm");
    }

    @Test
    void calculatePeakTime_emptyRestaurantList_returnsNullTimes() {
        when(dataClient.fetchRestaurants()).thenReturn(List.of());

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        assertThat(response.getPeakTimeStart()).isNull();
        assertThat(response.getPeakTimeEnd()).isNull();
    }

    @Test
    void calculatePeakTime_allRestaurantsSameHours_returnsFullOverlapPeriod() {
        Restaurant r1 = createRestaurant("R1", "11:00am", "2:00pm", 2);
        Restaurant r2 = createRestaurant("R2", "11:00am", "2:00pm", 3);
        Restaurant r3 = createRestaurant("R3", "11:00am", "2:00pm", 4);

        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2, r3));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // All overlap completely: 2+3+4 = 9 deals
        assertThat(response.getPeakTimeStart()).isEqualTo("11:00am");
        assertThat(response.getPeakTimeEnd()).isEqualTo("2:00pm");
    }

    @Test
    void calculatePeakTime_lateNightRestaurants_findsPeakInEarlyMorning() {
        Restaurant r1 = createRestaurant("R1", "11:00pm", "3:00am", 6);
        Restaurant r2 = createRestaurant("R2", "1:00am", "4:00am", 3);

        when(dataClient.fetchRestaurants()).thenReturn(List.of(r1, r2));

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // Peak should be 1am-3am with 9 deals
        assertThat(response.getPeakTimeStart()).isEqualTo("1:00am");
        assertThat(response.getPeakTimeEnd()).isEqualTo("3:00am");
    }

    /**
     * Helper method to create a restaurant with specified details.
     */
    private Restaurant createRestaurant(String name, String open, String close, int dealCount) {
        List<Deal> deals = createDeals(dealCount);

        return Restaurant.builder()
                .objectId("id-" + name)
                .name(name)
                .address1("123 Main St")
                .suburb("Suburb")
                .open(open)
                .close(close)
                .deals(deals)
                .build();
    }

    /**
     * Helper method to create a list of dummy deals.
     */
    private List<Deal> createDeals(int count) {
        if (count == 0) {
            return List.of();
        }

        List<Deal> deals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            deals.add(Deal.builder()
                    .objectId("deal-" + i)
                    .discount("20%")
                    .dineIn("true")
                    .lightning("false")
                    .qtyLeft("10")
                    .build());
        }
        return deals;
    }
}