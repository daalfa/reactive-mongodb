package com.davi.demo.restaurant.service.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(final String message) {
        super(message);
    }
}
