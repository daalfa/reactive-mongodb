package com.davi.demo.restaurant.service.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Collection;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(value = {NotFoundException.class, ValidationException.class})
//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleCustomException(Exception e) {
//        return new ErrorResponse(e.getMessage());
//    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    public List<FieldError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//        log.error(ex.getMessage());
//        return ex.getBindingResult().getFieldErrors();
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    public List<FieldError> handleConstraintViolationException(ConstraintViolationException ex) {
//        log.error(ex.getMessage());
//        return ex.getConstraintViolations().stream()
//                .map(violation -> new FieldError(
//                        violation.getRootBeanClass().getName(),
//                        violation.getPropertyPath().toString(),
//                        violation.getMessage()
//                )).toList();
//    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleWebExchangeBindException(WebExchangeBindException ex) {
        String errorCode = String.valueOf(HttpStatus.BAD_REQUEST.value());

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

//    @ExceptionHandler(HandlerMethodValidationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorMessage handleValidationErrors(HandlerMethodValidationException ex) {
//        String errorCode = String.valueOf(HttpStatus.BAD_REQUEST.value());
//
//        List<ErrorDetails> errors = ex.getAllValidationResults().stream()
//                .map(ParameterValidationResult::getResolvableErrors)
//                .flatMap(Collection::stream)
//                .map(e -> new ErrorDetails(
//                        null,
//                        null,
//                        e.getDefaultMessage()
//                )).toList();
//
//        return new ErrorMessage(errorCode, "Validation failed", errors);
//    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity handleGeneralExceptions(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
