package com.davi.demo.restaurant.service.dto;

import com.davi.demo.restaurant.service.config.ValidOrderBy;
import jakarta.validation.constraints.*;

public record RestaurantRequestDTO(
        @Size(min = 1, max = 50, message = "Name length must be between 1 and 50")
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Special characters not allowed")
        String name,

        @Size(min = 1, max = 50, message = "Cuisine length must be between 1 and 50")
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Special characters not allowed")
        String cuisine,

        @Digits(integer=3, fraction=2)
        @PositiveOrZero(message = "Distance must be positive")
        Double distance,

        @PositiveOrZero(message = "Price must be positive")
        @Digits(integer=3, fraction=2)
        Double price,

        @DecimalMin(value = "1", message = "Rating must be between 1 and 5")
        @DecimalMax(value = "5", message = "Rating must be between 1 and 5")
        @Digits(integer=3, fraction=2)
        Double rating,

        //snake_case to match request parameter
        @ValidOrderBy
        String order_by
) {
}
