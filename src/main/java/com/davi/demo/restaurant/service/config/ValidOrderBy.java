package com.davi.demo.restaurant.service.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = POrderValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

public @interface ValidOrderBy {
    String message() default "Invalid order by";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default
    {};
}
