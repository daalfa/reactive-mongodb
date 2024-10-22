package com.davi.demo.restaurant.service.enums;

import java.util.Map;

public enum Param {
    NAME("name"),
    DISTANCE("distance"),
    RATING("rating"),
    PRICE("price"),
    CUISINE("cuisine"),
    ORDER_BY("order_by");

    private final String value;

    public static final Map<String, Param> MAP_VALUE_PARAM = Map.of(
            NAME.value, NAME,
            DISTANCE.value, DISTANCE,
            RATING.value, RATING,
            PRICE.value, PRICE,
            CUISINE.value, CUISINE);

    Param(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
