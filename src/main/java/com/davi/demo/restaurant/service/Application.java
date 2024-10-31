package com.davi.demo.restaurant.service;

import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner checkConnection(MongoClient mongoClient) {
		return args -> {
			Flux.from(mongoClient.listDatabaseNames())
					.doOnNext(dbName -> System.out.println("MongoDB connection successful! Database: " + dbName))
					.doOnError(e -> System.err.println("Failed to connect to MongoDB: " + e.getMessage()))
					.blockFirst();
		};
	}
}
