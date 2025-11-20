package com.eatclub.challenge.controller;

import com.eatclub.challenge.dto.DealResponse;
import com.eatclub.challenge.dto.PeakTimeResponse;
import com.eatclub.challenge.service.DealService;
import com.eatclub.challenge.service.PeakTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(summary = "Get active deals", description = "Fetches all active restaurant deals for the specified time of day with pagination support")
    @ApiResponse(responseCode = "200", description = "Active deals retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid time format")
    @ApiResponse(responseCode = "503", description = "Unable to fetch restaurant data")
    public ResponseEntity<DealResponse> getActiveDeals(
            @Parameter(description = "Time of day (e.g., 3:00pm, 15:00)", example = "3:00pm")
            @RequestParam String timeOfDay,
            @PageableDefault(size = 20, page = 0) Pageable pageable) {

        log.info("Received request for active deals at time: {} with pagination: page={}, size={}",
                timeOfDay, pageable.getPageNumber(), pageable.getPageSize());

        DealResponse response = dealService.getActiveDeals(timeOfDay, pageable);

        log.info("Returning page {} of {} ({} total deals, {} on this page)",
                response.getCurrentPage(), response.getTotalPages(),
                response.getTotalElements(), response.getDeals().size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/peak-time")
    @Operation(summary = "Get peak time window", description = "Calculates when the maximum number of deals are simultaneously available")
    @ApiResponse(responseCode = "200", description = "Peak time calculated successfully")
    @ApiResponse(responseCode = "500", description = "Calculation error")
    @ApiResponse(responseCode = "503", description = "Unable to fetch restaurant data")
    public ResponseEntity<PeakTimeResponse> getPeakTime() {

        log.info("Received request for peak time calculation");

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        log.info("Returning peak time: {} - {}", response.getPeakTimeStart(), response.getPeakTimeEnd());

        return ResponseEntity.ok(response);
    }
}