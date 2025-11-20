package com.eatclub.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object for peak time window API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Peak time window when maximum deals are available")
public class PeakTimeResponse {

    @Schema(description = "Start time of the peak window", example = "6:00pm", nullable = true)
    private String peakTimeStart;

    @Schema(description = "End time of the peak window", example = "9:00pm", nullable = true)
    private String peakTimeEnd;
}