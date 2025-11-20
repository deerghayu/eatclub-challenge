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
    public DealResponse getDeals(@RequestParam(required = false) String timeOfDay) {
        log.info("Received request to get deals with timeOfDay filter: {}", timeOfDay);

        // No filtering yet - returns all deals
        List<DealDto> deals = dealService.getAllDeals();

        log.info("Returning {} deals", deals.size());
        return new DealResponse(deals);
    }
}