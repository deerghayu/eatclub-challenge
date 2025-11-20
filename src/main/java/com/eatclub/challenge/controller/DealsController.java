package com.eatclub.challenge.controller;

import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.dto.DealResponse;
import com.eatclub.challenge.dto.PeakTimeResponse;
import com.eatclub.challenge.service.DealService;
import com.eatclub.challenge.service.PeakTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API controller for deal-related operations.
 * Delegates all business rules to {@link DealService} and {@link PeakTimeService}.
 */
@RestController
@RequestMapping("/api/v1/deals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deals", description = "APIs for fetching restaurant deals and peak times")
public class DealsController {

    private final DealService dealService;
    private final PeakTimeService peakTimeService;

    @GetMapping
    @Operation(summary = "Get active deals", description = "Fetches all active restaurant deals for the specified time of day.")
    @ApiResponse(responseCode = "200", description = "Active deals retrieved successfully")
    public ResponseEntity<DealResponse> getActiveDeals(@RequestParam String timeOfDay) {
        log.info("Received request for active deals at time: {}", timeOfDay);

        List<DealDto> deals = dealService.getActiveDeals(timeOfDay);

        log.info("Returning {} active deals for time: {}", deals.size(), timeOfDay);
        return ResponseEntity.ok(new DealResponse(deals));
    }

    @GetMapping("/peak")
    @Operation(summary = "Get peak time", description = "Calculates the peak time window when the maximum number of deals are simultaneously available.")
    @ApiResponse(responseCode = "200", description = "Peak time calculated successfully")
    public ResponseEntity<PeakTimeResponse> getPeakTime() {
        log.info("Received request for peak time calculation");

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        log.info("Returning peak time: {} - {}", response.getPeakTimeStart(), response.getPeakTimeEnd());
        return ResponseEntity.ok(response);
    }
}