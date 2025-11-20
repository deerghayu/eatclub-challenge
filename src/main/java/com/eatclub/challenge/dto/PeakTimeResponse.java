package com.eatclub.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object for peak time window API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeakTimeResponse {

    private String peakTimeStart;
    private String peakTimeEnd;
}