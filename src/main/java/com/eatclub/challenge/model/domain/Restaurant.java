package com.eatclub.challenge.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a restaurant with its details and deals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    private String objectId;
    private String name;
    private String address1;
    private String suburb;
    private List<String> cuisines;
    private String imageLink;

    /**
     * Opening time - format varies (12hr/24hr: "9:00am", "3:00pm", "8:00am").
     */
    private String open;

    /**
     * Closing time - format varies (12hr/24hr: "5:00pm", "11:00pm", "3:00pm").
     * May be before open for restaurants operating past midnight.
     */
    private String close;

    private List<Deal> deals;

    public List<Deal> getDeals() {
        return deals != null ? deals : List.of();
    }
}