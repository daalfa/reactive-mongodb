package com.davi.demo.restaurant.service.service;


import com.davi.demo.restaurant.service.dto.RestaurantDTO;
import com.davi.demo.restaurant.service.exceptions.ValidationException;
import com.davi.demo.restaurant.service.mapper.RestaurantMapper;
import com.davi.demo.restaurant.service.model.Restaurant;
import com.mongodb.BasicDBList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    private static final String LONG_TEXT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String VALID_TEXT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmn";

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @Mock
    private RestaurantMapper mapper;

    @InjectMocks
    private RestaurantService restaurantService;

    @BeforeEach
    void setup() {

    }

    @Test
    public void givenFiveParameters_whenGetRestaurants_thenQueryShouldHaveFiveCriteria() {

        var captor = ArgumentCaptor.forClass(Query.class);
        var restaurant = new Restaurant();
        var restaurantDTO = new RestaurantDTO(null, null, null, null, null);

        when(mapper.toDTO(restaurant)).thenReturn(restaurantDTO);

        doReturn(Flux.just(restaurant))
                .when(mongoTemplate).find(captor.capture(), any());

        Flux<RestaurantDTO> response = restaurantService.getRestaurants(Map.of(
                "name", VALID_TEXT,
                "price", "+1.0",
                "rating", "4.",
                "distance", ".2",
                "cuisine", VALID_TEXT));

        StepVerifier.create(response).expectNext(restaurantDTO).verifyComplete();

        var query = captor.getValue();
        assertThat(query).isNotNull();
        assertThat(((BasicDBList)query.getQueryObject().get("$and")).size()).isEqualTo(5);
    }

    @Test
    public void givenZeroParameters_whenGetRestaurants_thenQueryShouldHaveZeroCriteria() {

        var captor = ArgumentCaptor.forClass(Query.class);
        var restaurant = new Restaurant();
        var restaurantDTO = new RestaurantDTO(null, null, null, null, null);

        when(mapper.toDTO(restaurant)).thenReturn(restaurantDTO);

        doReturn(Flux.just(restaurant))
                .when(mongoTemplate).find(captor.capture(), any());

        Flux<RestaurantDTO> response = restaurantService.getRestaurants(Map.of());

        StepVerifier.create(response).expectNext(restaurantDTO).verifyComplete();

        var query = captor.getValue();
        assertThat(query).isNotNull();
        assertThat(query.getQueryObject().size()).isEqualTo(0);
    }

    @Test
    public void givenInvalidNameParameter_whenGetRestaurants_thenQueryShouldThrowValidationError() {

        Flux<RestaurantDTO> response = restaurantService.getRestaurants(Map.of(
                "name", ".*delicious"));

        StepVerifier.create(response)
                .expectErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(ValidationException.class)
                        .hasMessage("Parameter name has invalid expression .*delicious"))
                .verify();
    }

    @ParameterizedTest
    @MethodSource("provideTestFieldValidations")
    public void testFieldValidations(String name, String value, String message) {

        HashMap<String, String> map = new HashMap<>();
        map.put(name, value);
        Flux<RestaurantDTO> response = restaurantService.getRestaurants(map);

        StepVerifier.create(response)
                .expectErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(ValidationException.class)
                        .hasMessage(message))
                .verify();
    }

    private static Stream<Arguments> provideTestFieldValidations() {
        return Stream.of(
                Arguments.of("extra", "extra", "Invalid parameters: [extra]"),
                Arguments.of("name", ".*delicious", "Parameter name has invalid expression .*delicious"),
                Arguments.of("cuisine", ".*delicious", "Parameter cuisine has invalid expression .*delicious"),
                Arguments.of("name", null, "Invalid parameter: name"),
                Arguments.of("cuisine", null, "Invalid parameter: cuisine"),
                Arguments.of("name", "", "Invalid parameter: name"),
                Arguments.of("cuisine", "", "Invalid parameter: cuisine"),
                Arguments.of("name", LONG_TEXT, "Invalid parameter: name"),
                Arguments.of("cuisine", LONG_TEXT, "Invalid parameter: cuisine"),
                Arguments.of("price", "abc", "Parameter price has invalid format abc"),
                Arguments.of("rating", "abc", "Parameter rating has invalid format abc"),
                Arguments.of("distance", "abc", "Parameter distance has invalid format abc"),
                Arguments.of("price", "1E1", "Parameter price has invalid format 1E1"),
                Arguments.of("rating", "1e1", "Parameter rating has invalid format 1e1"),
                Arguments.of("distance", "-1", "Parameter distance has invalid format -1"),
                Arguments.of("price", "NaN", "Parameter price has invalid format NaN"),
                Arguments.of("rating", "Infinity", "Parameter rating has invalid format Infinity"),
                Arguments.of("distance", "", "Invalid parameter: distance")
        );
    }
}