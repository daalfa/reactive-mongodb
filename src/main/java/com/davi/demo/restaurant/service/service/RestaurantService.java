package com.davi.demo.restaurant.service.service;

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
import java.util.stream.Collectors;

import static com.davi.demo.restaurant.service.enums.Param.*;
import static com.davi.demo.restaurant.service.model.Restaurant.*;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class RestaurantService {

    private static final int RESULT_LIMIT = 10;
    private static final String SORT_BY_SEPARATOR = ",";
    private static final String SORT_DIRECTION_SEPARATOR = " ";
    private static final Map<Param, String> PARAM_FIELD_MAP = Map.of(
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

    public Flux<RestaurantResponseDTO> getRestaurants(Map<String, String> queryParamMap) {

        Map<Param, String> pMap = convertParamMap(queryParamMap);

        Query query = buildQuery(pMap);

        return mongoTemplate.find(query, Restaurant.class).map(mapper::toDTO);
    }

    private Map<Param, String> convertParamMap(Map<String, String> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(e -> MAP_VALUE_PARAM.get(e.getKey()), Map.Entry::getValue));
    }

    private Query buildQuery(Map<Param, String> pMap) {

        Criteria criteria = buildCriteria(pMap);
        Sort sorting = buildSorting(pMap);

        Query query = new Query();
        query.addCriteria(criteria);
        query.fields()
                .include(FIELD_NAME, FIELD_CUISINE, FIELD_RATING, FIELD_PRICE, FIELD_DISTANCE)
                .exclude(FIELD_ID);
        query.with(sorting);
        query.limit(RESULT_LIMIT);
        return query;
    }

    /**
     * Build a Query Criteria based on a list of parameters.
     * If a parameter is null, then ignore the condition.
     * Return empty Criteria if all parameters are null.
     * This is not completely dynamic as each parameter has a type and a where clause.
     */
    private Criteria buildCriteria(Map<Param, String> pMap) {

        List<Criteria> criteriaList = new ArrayList<>();

        ofNullable(pMap.get(NAME))
                .map(restaurantNameFilter ->
                        Criteria.where(FIELD_NAME).regex(restaurantNameFilter, "i"))
                .ifPresent(criteriaList::add);

        ofNullable(pMap.get(CUISINE))
                .map(restaurantCuisineFilter ->
                        Criteria.where(FIELD_CUISINE).regex(restaurantCuisineFilter, "i"))
                .ifPresent(criteriaList::add);

        ofNullable(pMap.get(RATING))
                .map(restaurantMinRatingFilter ->
                        Criteria.where(FIELD_RATING).gte(restaurantMinRatingFilter))
                .ifPresent(criteriaList::add);

        ofNullable(pMap.get(PRICE))
                .map(restaurantMaxAvgPriceFilter ->
                        Criteria.where(FIELD_PRICE).lte(restaurantMaxAvgPriceFilter))
                .ifPresent(criteriaList::add);

        ofNullable(pMap.get(DISTANCE))
                .map(restaurantMaxDistanceFilter ->
                        Criteria.where(FIELD_DISTANCE).lte(restaurantMaxDistanceFilter))
                .ifPresent(criteriaList::add);

        return criteriaList.isEmpty() ? new Criteria() : new Criteria().andOperator(criteriaList);
    }

    //$orderby=Name desc, Distance asc, Rating desc
    private Sort buildSorting(Map<Param, String> pMap) {

        Sort.Order defaultDistanceOrder = new Sort.Order(Sort.Direction.ASC, FIELD_DISTANCE);
        Sort.Order defaultRatingOrder = new Sort.Order(Sort.Direction.DESC, FIELD_RATING);
        Sort.Order defaultPriceOrder = new Sort.Order(Sort.Direction.ASC, FIELD_PRICE);

        boolean hasCustomDistanceSorting = false;
        boolean hasCustomRatingSorting = false;
        boolean hasCustomPriceSorting = false;

        List<Sort.Order> orders = new ArrayList<>();

        String orderBy = pMap.get(ORDER_BY);

        if(orderBy != null) {
            for(String sortElement : orderBy.split(SORT_BY_SEPARATOR)) {
                String[] operators = sortElement.split(SORT_DIRECTION_SEPARATOR);
                String property = operators[0];
                String direction = operators[1];

                Param pProperty = MAP_VALUE_PARAM.get(property);

                if(pProperty == DISTANCE) {
                    hasCustomDistanceSorting = true;
                }
                if(pProperty == RATING) {
                    hasCustomRatingSorting = true;
                }
                if(pProperty == PRICE) {
                    hasCustomPriceSorting = true;
                }

                Sort.Direction sortDirection = direction.equalsIgnoreCase(Sort.Direction.DESC.name())
                        ? Sort.Direction.DESC : Sort.Direction.ASC;

                orders.add(new Sort.Order(sortDirection, PARAM_FIELD_MAP.get(pProperty)));
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
