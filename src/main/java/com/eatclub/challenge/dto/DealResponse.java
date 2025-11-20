package com.eatclub.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response wrapper for deals API with pagination support.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated response containing list of active restaurant deals")
public class DealResponse {

    @Schema(description = "List of active deals at the specified time", required = true)
    private List<DealDto> deals;

    @Schema(description = "Total number of deals across all pages", example = "50")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;

    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int currentPage;

    @Schema(description = "Number of items per page", example = "10")
    private int pageSize;

    @Schema(description = "Whether there is a next page", example = "true")
    private boolean hasNext;

    @Schema(description = "Whether there is a previous page", example = "false")
    private boolean hasPrevious;

    /**
     * Creates a non-paginated response (for backwards compatibility).
     */
    public DealResponse(List<DealDto> deals) {
        this.deals = deals;
        this.totalElements = deals.size();
        this.totalPages = 1;
        this.currentPage = 0;
        this.pageSize = deals.size();
        this.hasNext = false;
        this.hasPrevious = false;
    }
}