package com.eatclub.challenge.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO representing a deal along with its associated restaurant details.
 */
@Data
@Builder
public class DealDto {
    private String restaurantObjectId;
    private String restaurantName;
    private String restaurantAddress1;
    private String restaurantSuburb;
    private String restaurantOpen;
    private String restaurantClose;
    private String dealObjectId;
    private String discount;
    private String dineIn;
    private String lightning;
    private String qtyLeft;
}