package com.eatclub.challenge.controller;

import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.dto.DealResponse;
import com.eatclub.challenge.dto.PeakTimeResponse;
import com.eatclub.challenge.service.DealService;
import com.eatclub.challenge.service.PeakTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deals")
@Slf4j
@RequiredArgsConstructor
public class DealsController {

    private final DealService dealService;
    private final PeakTimeService peakTimeService;

    @GetMapping
    public DealResponse getActiveDeals(@RequestParam String timeOfDay) {
        log.info("Received request for active deals at time: {}", timeOfDay);

        List<DealDto> deals = dealService.getActiveDeals(timeOfDay);

        log.info("Returning {} active deals for time: {}", deals.size(), timeOfDay);
        return new DealResponse(deals);
    }

    @GetMapping("/peak")
    public PeakTimeResponse getPeakTime() {
        log.info("Received request for peak time calculation");

        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        log.info("Returning peak time: {} - {}", response.getPeakTimeStart(), response.getPeakTimeEnd());
        return response;
    }
}