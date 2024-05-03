package com.davi.demo.restaurant.service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cuisine")
@Data
public class Cuisine {
    @Id
    private String id;

    @NonNull
    @TextIndexed
    private String name;
}
