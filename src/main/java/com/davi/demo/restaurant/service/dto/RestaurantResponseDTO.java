package com.davi.demo.restaurant.service.dto;

import java.util.List;

public record RestaurantResponseDTO(
        String name,
        Double rating,
        Double price,
        Double distance,
        List<String> cuisine
) {
}
