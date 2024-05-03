package com.davi.demo.restaurant.service.web;

import com.davi.demo.restaurant.service.dto.RestaurantDTO;
import com.davi.demo.restaurant.service.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.*;

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
  public Flux<RestaurantDTO> getRestaurants(@RequestParam Map<String, String> paramMap) {
    return restaurantService.getRestaurants(paramMap);
  }
}
