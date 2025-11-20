package com.eatclub.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Response wrapper for deals API.
 */
@Data
@AllArgsConstructor
@Schema(description = "Response containing list of active restaurant deals")
public class DealResponse {

    @Schema(description = "List of active deals at the specified time", required = true)
    private List<DealDto> deals;
}