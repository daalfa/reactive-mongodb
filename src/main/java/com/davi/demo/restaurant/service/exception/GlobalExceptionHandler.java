package com.davi.demo.restaurant.service.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(value = {NotFoundException.class, ValidationException.class})
//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    public ErrorMessage handleCustomException(Exception e) {
//        int errorCode = HttpStatus.BAD_REQUEST.value();
//        return new ErrorMessage(errorCode, e.getMessage(), null);
//    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleWebExchangeBindException(WebExchangeBindException ex) {
        int errorCode = HttpStatus.BAD_REQUEST.value();

        log.error("WebExchangeBindException: {}", ex.getMessage());

        List<ErrorDetails> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDetails(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                        ))
                .toList();
        return new ErrorMessage(errorCode, "Validation failed", errors);
    }
}
