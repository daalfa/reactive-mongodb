package com.davi.demo.restaurant.service.controller;

import com.davi.demo.restaurant.service.dto.RestaurantResponseDTO;
import com.davi.demo.restaurant.service.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/v1/restaurants")
@Slf4j
public class RestaurantController {

  private final RestaurantService restaurantService;

  public RestaurantController(final RestaurantService restaurantService) {
    this.restaurantService = restaurantService;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Flux<RestaurantResponseDTO> getRestaurants(@RequestParam Map<String, String> paramMap) {
    return restaurantService.getRestaurants(paramMap);
  }
}
