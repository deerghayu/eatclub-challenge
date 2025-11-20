package com.eatclub.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Response wrapper for deals API.
 */
@Data
@AllArgsConstructor
public class DealResponse {
    private List<DealDto> deals;
}