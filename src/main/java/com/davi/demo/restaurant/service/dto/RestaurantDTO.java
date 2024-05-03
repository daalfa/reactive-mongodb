package com.davi.demo.restaurant.service.dto;

public record RestaurantDTO (
        String name,
        Double rating,
        Double price,
        Double distance,
        String cuisine
) {
}
