package com.eatclub.challenge.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a deal offered by a restaurant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deal {

    private String objectId;
    private String discount;
    private String dineIn;
    private String lightning;
    private String qtyLeft;

    // Optional deal-specific timing constraints
    private String open;
    private String close;
    private String start;
    private String end;
}