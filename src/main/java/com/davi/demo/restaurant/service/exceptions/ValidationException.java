package com.davi.demo.restaurant.service.exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException(final String message) {
        super(message);
    }
}
