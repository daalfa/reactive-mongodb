package com.davi.demo.restaurant.service.service;

import com.davi.demo.restaurant.service.dto.RestaurantRequestDTO;
import com.davi.demo.restaurant.service.dto.RestaurantResponseDTO;
import com.davi.demo.restaurant.service.enums.Param;
import com.davi.demo.restaurant.service.mapper.RestaurantMapper;
import com.davi.demo.restaurant.service.model.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.davi.demo.restaurant.service.enums.Param.*;
import static com.davi.demo.restaurant.service.model.Restaurant.*;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class RestaurantService {

    private static final int RESULT_LIMIT = 20;
    private static final String SORT_BY_SEPARATOR = ",";
    private static final String SORT_DIRECTION_SEPARATOR = "\\s+";
    private static final Map<Param, String> PARAM_TO_FIELD_MAP = Map.of(
            NAME, FIELD_NAME,
            CUISINE, FIELD_CUISINE,
            DISTANCE, FIELD_DISTANCE,
            RATING, FIELD_RATING,
            PRICE, FIELD_PRICE);

    private final ReactiveMongoTemplate mongoTemplate;
    private final RestaurantMapper mapper;

    public RestaurantService(ReactiveMongoTemplate mongoTemplate, RestaurantMapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
    }

    public Flux<RestaurantResponseDTO> getRestaurants(RestaurantRequestDTO request) {

        Query query = buildQuery(request);

        return mongoTemplate.find(query, Restaurant.class)
                .map(mapper::toRestaurantResponseDTO);
    }

    private Query buildQuery(RestaurantRequestDTO request) {

        Criteria criteria = buildCriteria(request);
        Sort sorting = buildSorting(request);

        Query query = new Query();
        query.addCriteria(criteria);
        query.fields()
                .include(FIELD_NAME, FIELD_CUISINE, FIELD_RATING, FIELD_PRICE, FIELD_DISTANCE)
                .exclude(FIELD_ID);
        query.with(sorting);
//        query.limit(RESULT_LIMIT);
        return query;
    }

    /*
     * Build a Query Criteria based on not null properties.
     * If a properties is null, then ignore the condition.
     * Return empty Criteria if all parameters are null.
     */
    private Criteria buildCriteria(RestaurantRequestDTO request) {

        List<Criteria> criteriaList = new ArrayList<>();

        ofNullable(request.name())
                .map(restaurantNameFilter ->
                        Criteria.where(FIELD_NAME).regex(restaurantNameFilter, "i"))
                .ifPresent(criteriaList::add);

        ofNullable(request.cuisine())
                .map(restaurantCuisineFilter ->
                        Criteria.where(FIELD_CUISINE).regex(restaurantCuisineFilter, "i"))
                .ifPresent(criteriaList::add);

        ofNullable(request.rating())
                .map(restaurantMinRatingFilter ->
                        Criteria.where(FIELD_RATING).gte(restaurantMinRatingFilter))
                .ifPresent(criteriaList::add);

        ofNullable(request.price())
                .map(restaurantMaxAvgPriceFilter ->
                        Criteria.where(FIELD_PRICE).lte(restaurantMaxAvgPriceFilter))
                .ifPresent(criteriaList::add);

        ofNullable(request.distance())
                .map(restaurantMaxDistanceFilter ->
                        Criteria.where(FIELD_DISTANCE).lte(restaurantMaxDistanceFilter))
                .ifPresent(criteriaList::add);

        return criteriaList.isEmpty() ? new Criteria() : new Criteria().andOperator(criteriaList);
    }

    /*
     * Create a sorting filter based on order_by parameter.
     * Syntax is: property asc/desc comma separated.
     * Example 1: order_by=name desc
     * Example 2: order_by=distance asc, Rating desc, Price asc
     * By default the 3 sorting above will apply if not overwritten.
     */
    private Sort buildSorting(RestaurantRequestDTO request) {

        Sort.Order defaultDistanceOrder = new Sort.Order(Sort.Direction.ASC, FIELD_DISTANCE);
        Sort.Order defaultRatingOrder = new Sort.Order(Sort.Direction.DESC, FIELD_RATING);
        Sort.Order defaultPriceOrder = new Sort.Order(Sort.Direction.ASC, FIELD_PRICE);

        boolean hasCustomDistanceSorting = false;
        boolean hasCustomRatingSorting = false;
        boolean hasCustomPriceSorting = false;

        List<Sort.Order> orders = new ArrayList<>();

        String orderBy = request.order_by();

        if(orderBy != null) {
            for(String sortElement : orderBy.split(SORT_BY_SEPARATOR)) {
                String[] parts = sortElement.strip().toLowerCase().split(SORT_DIRECTION_SEPARATOR);
                String property = parts[0];
                String direction = parts[1];

                Param param = VALUE_TO_PARAM_MAP.get(property);

                if(param == DISTANCE) {
                    hasCustomDistanceSorting = true;
                }
                if(param == RATING) {
                    hasCustomRatingSorting = true;
                }
                if(param == PRICE) {
                    hasCustomPriceSorting = true;
                }

                Sort.Direction sortDirection = direction.equalsIgnoreCase(Sort.Direction.DESC.name())
                        ? Sort.Direction.DESC : Sort.Direction.ASC;

                orders.add(new Sort.Order(sortDirection, PARAM_TO_FIELD_MAP.get(param)));
            }
        }

        if(!hasCustomDistanceSorting) {
            orders.add(defaultDistanceOrder);
        }
        if(!hasCustomRatingSorting) {
            orders.add(defaultRatingOrder);
        }
        if(!hasCustomPriceSorting) {
            orders.add(defaultPriceOrder);
        }

        return Sort.by(orders);
    }
}
