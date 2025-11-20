package com.eatclub.challenge.controller;

import com.eatclub.challenge.dto.DealDto;
import com.eatclub.challenge.dto.DealResponse;
import com.eatclub.challenge.service.DealService;
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

    @GetMapping
    public DealResponse getActiveDeals(@RequestParam String timeOfDay) {
        log.info("Received request for active deals at time: {}", timeOfDay);

        List<DealDto> deals = dealService.getActiveDeals(timeOfDay);

        log.info("Returning {} active deals for time: {}", deals.size(), timeOfDay);
        return new DealResponse(deals);
    }
}