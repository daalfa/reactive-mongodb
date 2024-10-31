package com.davi.demo.restaurant.service.enums;

import java.util.List;
import java.util.Map;

public enum Param {
    NAME("name"),
    DISTANCE("distance"),
    RATING("rating"),
    PRICE("price"),
    CUISINE("cuisine"),
    ORDER_BY("order_by");

    private final String value;

    public static final Map<String, Param> VALUE_TO_PARAM_MAP = Map.of(
            NAME.value, NAME,
            DISTANCE.value, DISTANCE,
            RATING.value, RATING,
            PRICE.value, PRICE,
            CUISINE.value, CUISINE);

    public static final List<String> PARAM_VALUES = List.of(
            NAME.value,
            DISTANCE.value,
            RATING.value,
            PRICE.value,
            CUISINE.value,
            ORDER_BY.value);

    Param(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
