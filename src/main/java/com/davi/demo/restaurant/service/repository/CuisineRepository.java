package com.davi.demo.restaurant.service.repository;

import com.davi.demo.restaurant.service.model.Cuisine;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CuisineRepository extends ReactiveMongoRepository<Cuisine, String> {
}
