package com.davi.demo.restaurant.service.repository;

import com.davi.demo.restaurant.service.model.Restaurant;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RestaurantRepository extends ReactiveMongoRepository<Restaurant, String> {

}
