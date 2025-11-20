package com.eatclub.challenge.controller;

import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.dto.DealResponse;
import com.eatclub.challenge.dto.PeakTimeResponse;
import com.eatclub.challenge.exception.InvalidTimeFormatException;
import com.eatclub.challenge.exception.RestaurantDataException;
import com.eatclub.challenge.service.DealService;
import com.eatclub.challenge.service.PeakTimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealsController.class)
class DealsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    @MockBean
    private PeakTimeService peakTimeService;

    @Test
    void getActiveDeals_returnsOkWithPaginatedDeals() throws Exception {
        DealResponse response = DealResponse.builder()
                .deals(List.of(DealDto.builder().restaurantName("Test").build()))
                .totalElements(1)
                .currentPage(0)
                .pageSize(20)
                .build();

        when(dealService.getActiveDeals(eq("3:00pm"), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/deals").param("timeOfDay", "3:00pm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getActiveDeals_withInvalidTime_returnsBadRequest() throws Exception {
        when(dealService.getActiveDeals(eq("invalid"), any(Pageable.class)))
                .thenThrow(new InvalidTimeFormatException("Unable to parse time"));

        mockMvc.perform(get("/api/v1/deals").param("timeOfDay", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getActiveDeals_withServiceError_returnsServiceUnavailable() throws Exception {
        when(dealService.getActiveDeals(eq("3:00pm"), any(Pageable.class)))
                .thenThrow(new RestaurantDataException("Service unavailable"));

        mockMvc.perform(get("/api/v1/deals").param("timeOfDay", "3:00pm"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void getPeakTime_returnsOkWithPeakWindow() throws Exception {
        when(peakTimeService.calculatePeakTime())
                .thenReturn(new PeakTimeResponse("6:00pm", "9:00pm"));

        mockMvc.perform(get("/api/v1/deals/peak-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peakTimeStart").value("6:00pm"))
                .andExpect(jsonPath("$.peakTimeEnd").value("9:00pm"));
    }

    @Test
    void getPeakTime_withServiceError_returnsServiceUnavailable() throws Exception {
        when(peakTimeService.calculatePeakTime())
                .thenThrow(new RestaurantDataException("Service unavailable"));

        mockMvc.perform(get("/api/v1/deals/peak-time"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }
}
