package com.davi.demo.restaurant.service.dto;

public record RestaurantResponseDTO(
        String name,
        Double rating,
        Double price,
        Double distance,
        String cuisine
) {
}
