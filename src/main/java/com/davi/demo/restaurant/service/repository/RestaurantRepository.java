package com.davi.demo.restaurant.service.repository;

import com.davi.demo.restaurant.service.model.Restaurant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RestaurantRepository extends ReactiveMongoRepository<Restaurant, String> {

}
