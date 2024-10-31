package com.davi.demo.restaurant.service.exception;

import java.util.List;

public class ValidationException extends RuntimeException{
    private final List<ErrorDetails> errors;

    public ValidationException(final String message, List<ErrorDetails> errors) {
        super(message);
        this.errors = errors;
    }

    public List<ErrorDetails> getErrors() {
        return errors;
    }
}
