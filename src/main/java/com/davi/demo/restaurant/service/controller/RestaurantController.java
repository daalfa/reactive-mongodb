package com.davi.demo.restaurant.service.controller;

import com.davi.demo.restaurant.service.dto.RestaurantRequestDTO;
import com.davi.demo.restaurant.service.dto.RestaurantResponseDTO;
import com.davi.demo.restaurant.service.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(final RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<RestaurantResponseDTO> getRestaurants(@Valid RestaurantRequestDTO restaurantRequestDTO) {
        return restaurantService.getRestaurants(restaurantRequestDTO);
    }
}
