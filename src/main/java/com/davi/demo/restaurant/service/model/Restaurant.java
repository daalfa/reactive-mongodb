package com.davi.demo.restaurant.service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {

    @Id
    private String id;

    @TextIndexed
    @Field(name = FIELD_NAME)
    private String name;

    @TextIndexed
    @Field(name = FIELD_CUISINE)
    private String cuisine; //todo: make a collection of cuisines

    @Field(name = FIELD_RATING)
    private Double rating;

    @Field(name = FIELD_PRICE)
    private Double price;

    @Field(name = FIELD_DISTANCE)
    private Double distance;

    public static final String FIELD_ID = "_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CUISINE = "cuisine";
    public static final String FIELD_RATING = "rating";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_DISTANCE = "distance";
}
