package com.davi.demo.restaurant.service.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.FieldError;

import java.util.List;

public record ErrorMessage(
        String status,
        String message,
        List<ErrorDetails> errors
) {
}