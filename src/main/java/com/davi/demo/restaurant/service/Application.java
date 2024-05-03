package com.davi.demo.restaurant.service;

import com.davi.demo.restaurant.service.util.HelperCSV;
import com.davi.demo.restaurant.service.repository.CuisineRepository;
import com.davi.demo.restaurant.service.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner addRestaurantsAtStartup(RestaurantRepository restaurantRepository,
													 CuisineRepository cuisineRepository,
													 @Value("${application.initializeDatabase:false}") Boolean initDB) {
		return args -> {
			if(initDB) {
				restaurantRepository.deleteAll().block();
				cuisineRepository.deleteAll().block();

				HelperCSV.LoadEntitiesFromCSV(cuisineRepository, restaurantRepository);
			}
		};
	}
}
