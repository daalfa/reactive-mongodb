package com.davi.demo.restaurant.service.util;

import com.davi.demo.restaurant.service.model.Cuisine;
import com.davi.demo.restaurant.service.model.Restaurant;
import com.davi.demo.restaurant.service.repository.CuisineRepository;
import com.davi.demo.restaurant.service.repository.RestaurantRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelperCSV {

    private final static String DELIMITER = ",";
    private final static String CUISINES_CSV = "data/cuisines.csv";
    private final static String RESTAURANTS_CSV = "data/restaurants.csv";

    private HelperCSV() {

    };

    static public void LoadEntitiesFromCSV(CuisineRepository cuisineRepository, RestaurantRepository restaurantRepository) throws IOException {

        Map<String, String> cuisinesIdMap = new HashMap<>();

        List<String[]> cuisines = readCSV(CUISINES_CSV);
        cuisines.forEach(values -> {
            String id = values[0];
            String name = values[1];

            cuisinesIdMap.put(id, name);
            cuisineRepository.save(new Cuisine(name)).block();
        });

        List<String[]> restaurants = readCSV(RESTAURANTS_CSV);
        restaurants.forEach(values -> {
            String name = values[0];
            Double rating = Double.valueOf(values[1]);
            Double distance = Double.valueOf(values[2]);
            Double price = Double.valueOf(values[3]);
            String cuisine = cuisinesIdMap.get(values[4]);

            Restaurant restaurant = new Restaurant();
            restaurant.setName(name);
            restaurant.setRating(rating);
            restaurant.setDistance(distance);
            restaurant.setPrice(price);
            restaurant.setCuisine(cuisine);

            restaurantRepository.save(restaurant).block();
        });
    }

    private static List<String[]> readCSV(String path) throws IOException {
        List<String[]> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                lines.add(line.split(DELIMITER));
            }
        }
        return lines;
    }
}
