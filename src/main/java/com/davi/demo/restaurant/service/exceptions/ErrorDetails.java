package com.davi.demo.restaurant.service.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetails(
        String field,
        Object rejectedValue,
        String message
) {
    public ErrorDetails {
        rejectedValue = rejectedValue != null ? rejectedValue.toString() : null;
    }
}