package com.davi.demo.restaurant.service.config;

import com.davi.demo.restaurant.service.enums.Param;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class POrderValidator implements ConstraintValidator<ValidOrderBy, String> {

    @Override
    public boolean isValid(String orderBy, ConstraintValidatorContext context) {
        if (orderBy == null) {
            return true;
        }

        String[] orders = orderBy.split(",");
        for (String order : orders) {
            String[] parts = order.strip().toLowerCase().split("\\s+");

            boolean isValidFormat = isValidOrderFormat(parts, context);
            boolean isValidProperty = isValidPropertyName(parts, context);
            boolean isValidDirection = isValidDirection(parts, context);

            if(!isValidFormat || !isValidProperty || !isValidDirection) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidOrderFormat(String[] parts, ConstraintValidatorContext context) {
        if (parts.length != 2) {
            addError(context, "Invalid order format. Must be 'property asc/desc'");
            return false;
        }
        return true;
    }

    private boolean isValidPropertyName(String[] parts, ConstraintValidatorContext context) {
        String property = parts[0];
        if (property.equals(Param.ORDER_BY.getValue()) || !Param.PARAM_VALUES.contains(property)) {
            addError(context, "Invalid property: '" + property + "'");
            return false;
        }
        return true;
    }

    private boolean isValidDirection(String[] parts, ConstraintValidatorContext context) {
        if(parts.length > 1) {
            String direction = parts[1];
            if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
                addError(context, "Invalid sort direction: '" + direction + "'. Must be 'asc' or 'desc'");
                return false;
            }
        }
        return true;
    }

    private static void addError(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}