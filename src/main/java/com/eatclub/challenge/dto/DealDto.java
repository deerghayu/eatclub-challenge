package com.eatclub.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * DTO representing a deal along with its associated restaurant details.
 */
@Data
@Builder
@Schema(description = "Restaurant deal with associated restaurant information")
public class DealDto {

    @Schema(description = "Unique identifier for the restaurant", example = "abc123")
    private String restaurantObjectId;

    @Schema(description = "Name of the restaurant", example = "The Great Pizza Co")
    private String restaurantName;

    @Schema(description = "Street address of the restaurant", example = "123 Main Street")
    private String restaurantAddress1;

    @Schema(description = "Suburb where the restaurant is located", example = "Sydney CBD")
    private String restaurantSuburb;

    @Schema(description = "Restaurant opening time", example = "11:00am")
    private String restaurantOpen;

    @Schema(description = "Restaurant closing time", example = "10:00pm")
    private String restaurantClose;

    @Schema(description = "Unique identifier for the deal", example = "deal456")
    private String dealObjectId;

    @Schema(description = "Discount percentage or amount", example = "20%")
    private String discount;

    @Schema(description = "Whether the deal is valid for dine-in", example = "true")
    private String dineIn;

    @Schema(description = "Whether this is a lightning deal", example = "false")
    private String lightning;

    @Schema(description = "Quantity of deals remaining", example = "10")
    private String qtyLeft;
}