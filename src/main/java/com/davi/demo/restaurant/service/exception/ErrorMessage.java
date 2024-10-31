package com.davi.demo.restaurant.service.exception;

import java.util.List;

public record ErrorMessage(
        int status,
        String message,
        List<ErrorDetails> errors
) {

}