//package com.davi.demo.restaurant.service.integration;
//
//
//import com.davi.demo.restaurant.service.dto.RestaurantResponseDTO;
//import com.davi.demo.restaurant.service.model.Cuisine;
//import com.davi.demo.restaurant.service.model.Restaurant;
//import com.davi.demo.restaurant.service.repository.CuisineRepository;
//import com.davi.demo.restaurant.service.repository.RestaurantRepository;
//import lombok.Builder;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureWebTestClient
//class ApplicationTest {
//
//	@Container
//	@ServiceConnection
//	static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
//
//	@Autowired
//	private WebTestClient webTestClient;
//
//	@Autowired
//	private CuisineRepository cuisineRepository;
//
//	@Autowired
//	private RestaurantRepository restaurantRepository;
//
//	@Autowired
//	private ReactiveMongoTemplate mongoTemplate;
//
//	@BeforeEach
//	void setup() {
//		cuisineRepository.deleteAll().block();
//		restaurantRepository.deleteAll().block();
//
//		cuisineRepository.save(new Cuisine("chinese")).block();
//
//		Restaurant restaurant = RestaurantBuilder()
//				.name("Tasty 1")
//				.distance(1.0)
//				.rating(5.0)
//				.price(10.0)
//				.cuisine("chinese")
//				.build();
//
//		restaurantRepository.save(restaurant).block();
//	}
//
//	@Test
//	void shouldGetARestaurant() {
//		webTestClient.get().uri("/v1/restaurants")
//				.exchange()
//				.expectStatus().isOk()
//				.expectBody()
//				.jsonPath("$").isArray()
//				.jsonPath("$.size()").isEqualTo(1)
//				.jsonPath("$.[0].name").isEqualTo("Tasty 1")
//				.jsonPath("$.[0].distance").isEqualTo("1.0")
//				.jsonPath("$.[0].rating").isEqualTo("5.0")
//				.jsonPath("$.[0].price").isEqualTo("10.0")
//				.jsonPath("$.[0].cuisine").isEqualTo("chinese");
//	}
//
//	@Test
//	void shouldGetSortedRestaurants() {
//		// Tasty 0 is sorted before because price is < than Tasty 1
//		Restaurant restaurant0 = RestaurantBuilder()
//				.name("Tasty 0")
//				.distance(1.0)
//				.rating(5.0)
//				.price(9.0)
//				.cuisine("chinese")
//				.build();
//
//		// Tasty 2 is after because rating is < than Tasty 1
//		Restaurant restaurant2 = RestaurantBuilder()
//				.name("Tasty 2")
//				.distance(1.0)
//				.rating(4.0)
//				.price(10.0)
//				.cuisine("chinese")
//				.build();
//
//		// Tasty 3 is after 2 because distance is > than Tasty 2
//		Restaurant restaurant3 = RestaurantBuilder()
//				.name("Tasty 3")
//				.distance(2.0)
//				.rating(4.0)
//				.price(10.0)
//				.cuisine("chinese")
//				.build();
//
//		Restaurant restaurant4 = RestaurantBuilder()
//				.name("Tasty 4")
//				.distance(4.0)
//				.rating(4.0)
//				.price(10.0)
//				.cuisine("chinese")
//				.build();
//
//		Restaurant restaurant5 = RestaurantBuilder()
//				.name("Tasty 5")
//				.distance(5.0)
//				.rating(4.0)
//				.price(10.0)
//				.cuisine("chinese")
//				.build();
//
//		restaurantRepository.saveAll(List.of(restaurant0, restaurant2, restaurant3, restaurant4, restaurant5)).blockLast();
//
//		webTestClient.get().uri("/v1/restaurants")
//				.exchange()
//				.expectStatus().isOk()
//				.expectBody()
//				.jsonPath("$").isArray()
//				.jsonPath("$.size()").isEqualTo(5)
//				.jsonPath("$.[0].name").isEqualTo("Tasty 0")
//				.jsonPath("$.[1].name").isEqualTo("Tasty 1")
//				.jsonPath("$.[2].name").isEqualTo("Tasty 2")
//				.jsonPath("$.[3].name").isEqualTo("Tasty 3")
//				.jsonPath("$.[4].name").isEqualTo("Tasty 4");
//	}
//
//	@Test
//	void shouldFilterRestaurants() {
//		List<Restaurant> restaurants = List.of(
//				RestaurantBuilder().name("Best pasta").distance(2.0).rating(2.0).price(15.0).cuisine("italian").build(),  	   //!name
//				RestaurantBuilder().name("SuperDelicious").distance(2.0).rating(2.0).price(10.0).cuisine("brazilian").build(), //1st
//				RestaurantBuilder().name("Tasty Deli").distance(2.0).rating(3.0).price(10.0).cuisine("indian").build(),	       //!cuisine
//				RestaurantBuilder().name("Italian Delicate").distance(3.0).rating(4.0).price(30.0).cuisine("italian").build(), //too expensive
//				RestaurantBuilder().name("The Placedeli").distance(3.0).rating(1.0).price(15.0).cuisine("italian").build(),	   //low rating
//				RestaurantBuilder().name("Delicious").distance(4.0).rating(5.0).price(15.0).cuisine("brazilian").build()	   //too far
//				);
//
//		restaurantRepository.saveAll(restaurants).blockLast();
//
//		List<RestaurantResponseDTO> restaurantList = webTestClient.get().uri(uriBuilder -> uriBuilder
//						.path("/v1/restaurants")
//						.queryParam("name", "Deli")
//						.queryParam("distance", "3")
//						.queryParam("rating", "2")
//						.queryParam("price", "20")
//						.queryParam("cuisine", "lian")
//						.build())
//				.exchange()
//				.expectStatus().isOk()
//				.expectBodyList(RestaurantResponseDTO.class).returnResult().getResponseBody();
//
//		assertThat(restaurantList).hasSize(1);
//		assertThat(restaurantList.get(0).name()).isEqualTo("SuperDelicious");
//	}
//
//	@Test
//	void shouldValidateParameter() {
//		webTestClient.get().uri(uriBuilder -> uriBuilder
//						.path("/v1/restaurants")
//						.queryParam("names", "Deli")
//						.build())
//				.exchange()
//				.expectStatus().is4xxClientError()
//				.expectBody(String.class)
//				.consumeWith(response -> {
//					String errorBody = response.getResponseBody();
//					assertThat(errorBody).contains("Invalid parameters");
//				});
//
//	}
//
//	@Builder(builderMethodName = "RestaurantBuilder")
//	Restaurant restaurantBuilder(String id,
//								 String name,
//								 Double distance,
//								 Double rating,
//								 Double price,
//								 String cuisine) {
//
//		Restaurant restaurant = new Restaurant();
//		restaurant.setId(id);
//		restaurant.setName(name);
//		restaurant.setDistance(distance);
//		restaurant.setRating(rating);
//		restaurant.setPrice(price);
//		restaurant.setCuisine(cuisine);
//		return restaurant;
//	}
//}