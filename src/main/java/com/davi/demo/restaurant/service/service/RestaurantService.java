package com.davi.demo.restaurant.service.service;

import com.davi.demo.restaurant.service.dto.RestaurantDTO;
import com.davi.demo.restaurant.service.exceptions.ValidationException;
import com.davi.demo.restaurant.service.mapper.RestaurantMapper;
import com.davi.demo.restaurant.service.model.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestaurantService {

    private static final Set<String> VALID_PARAMETERS = Set.of(
            "name", "rating", "price", "distance", "cuisine");
    private static final int PARAMETER_MAX_LEN = 40;
    private static final int RESULT_LIMIT = 5;

    private final ReactiveMongoTemplate mongoTemplate;
    private final RestaurantMapper mapper;

    public RestaurantService(ReactiveMongoTemplate mongoTemplate, RestaurantMapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
    }

    public Flux<RestaurantDTO> getRestaurants(Map<String, String> queryParamMap) {
        return validateAndNormalizeParameters(queryParamMap).flatMapMany(map -> {
            String name = map.get("name");
            String rating = map.get("rating");
            String price = map.get("price");
            String distance = map.get("distance");
            String cuisine = map.get("cuisine");

            return buildCriteria(name, rating, price, distance, cuisine).flatMapMany(criteria -> {
                Sort sorting = Sort.by(Sort.Direction.ASC, "distance")
                        .and(Sort.by(Sort.Direction.DESC, "rating"))
                        .and(Sort.by(Sort.Direction.ASC, "price"));

                Query query = buildQuery(criteria, sorting);

                return mongoTemplate.find(query, Restaurant.class).map(mapper::toDTO);
            });
        });
    }

    private Query buildQuery(Criteria criteria, Sort sorting) {
        Query query = new Query();
        query.addCriteria(criteria);
        query.fields().include("name", "rating", "price", "distance", "cuisine").exclude("_id");
        query.with(sorting);
        query.limit(RESULT_LIMIT);
        return query;
    }

    /**
     * Constructs a Query Criteria dynamically based on the parameters. <br>
     * If a parameter is null, then ignore the condition. <br>
     * Return empty Criteria if all parameters are null.
     */
    private Mono<Criteria> buildCriteria(String name, String rating, String price, String distance, String cuisine) {

        Mono<Criteria> nameCriteria = getValidatedStringExpression("name", name)
                .map(value -> Criteria.where("name").regex(value, "i"));

        Mono<Criteria> cuisineCriteria = getValidatedStringExpression("cuisine", cuisine)
                .map(value -> Criteria.where("cuisine").regex(value, "i"));

        Mono<Criteria> ratingCriteria = getValidatedNumericExpression("rating", rating)
                .map(value -> Criteria.where("rating").gte(value));

        Mono<Criteria> priceCriteria = getValidatedNumericExpression("price", price)
                .map(value -> Criteria.where("price").lte(value));

        Mono<Criteria> distanceCriteria = getValidatedNumericExpression("distance", distance)
                .map(value -> Criteria.where("distance").lte(value));

       return Flux.merge(nameCriteria, cuisineCriteria, ratingCriteria, priceCriteria, distanceCriteria)
               .collectList()
               .filter(criteriaList -> !criteriaList.isEmpty())
               .flatMap(criteriaList -> Mono.just(new Criteria().andOperator(criteriaList)))
               .switchIfEmpty(Mono.just(new Criteria()));
    }

    /**
     * Validate if there are no unknown query parameters. <br>
     * Validate if values are not empty or null. <br>
     * Convert the Map keys to lowercase.
     */
    private Mono<Map<String, String>> validateAndNormalizeParameters(Map<String, String> paramMap) {

        return Flux.fromIterable(paramMap.entrySet())
                .flatMap(e -> {
                    if (e.getValue() == null || e.getValue().isEmpty() || e.getValue().length() > PARAMETER_MAX_LEN) {
                        log.error("Invalid parameter: {}", e.getKey());
                        return Mono.error(new ValidationException(STR."Invalid parameter: \{e.getKey()}"));
                    } else {
                        return Mono.just(e);
                    }
                })
                .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue))
                .flatMap(map -> {
                    List<String> invalidParams = map.keySet().stream()
                            .filter(param -> !VALID_PARAMETERS.contains(param))
                            .toList();

                    if (!invalidParams.isEmpty()) {
                        log.error("Invalid parameters: {}", invalidParams);
                        return Mono.error(new ValidationException(STR."Invalid parameters: \{invalidParams}"));
                    }

                    return Mono.just(map);
                });
    }

    /**
     * Validate if the text query do not have special symbols. <br>
     * This is a way to sanitize the search text for only
     * words, numbers, hyphen and white space <br>
     * Examples: <br>
     * District9 = valid <br>
     * Tex-Mex = valid <br>
     * Bistro&Bar = valid ('&' should be encoded as '%26') <br>
     * Fish and Chips = valid
     */
    private Mono<String> getValidatedStringExpression(String parameterName, String value) {
        if(value == null) return Mono.empty();

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9& -]+$");
        if(!pattern.matcher(value).matches()) {
            log.error("Parameter {} has invalid expression {}", parameterName, value);
            return Mono.error(new ValidationException(STR."Parameter \{parameterName} has invalid expression \{value}"));
        }

        return Mono.just(value);
    }

    /**
     * Validate if the value is a valid number without Exponent 'E'<br>
     * Return the parsed number or empty
     */
    private Mono<Double> getValidatedNumericExpression(String parameterName, String value) {
        if(value == null) return Mono.empty();

        Pattern pattern = Pattern.compile("[EeNn-]");
        if(pattern.matcher(value).find()) {
            log.error("Parameter {} has invalid format {}", parameterName, value);
            return Mono.error(new ValidationException(STR."Parameter \{parameterName} has invalid format \{value}"));
        }

        try {
            return Mono.just(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            log.error("Parameter {} has invalid format {}", parameterName, value);
            return Mono.error(new ValidationException(STR."Parameter \{parameterName} has invalid format \{value}"));
        }
    }
}
