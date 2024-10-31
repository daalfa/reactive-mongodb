package com.davi.demo.restaurant.service.config;

import com.davi.demo.restaurant.service.exception.ErrorDetails;
import com.davi.demo.restaurant.service.exception.ValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.davi.demo.restaurant.service.enums.Param.PARAM_VALUES;

@Configuration
public class ValidationFilter {

    @Bean
    public WebFilter requestParameterValidationFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            return validateQueryParams(request.getQueryParams())
                    .then(chain.filter(exchange));
        };
    }

    /*
     * Validate unknown query params not in Param ENUM
     */
    private Mono<Void> validateQueryParams(MultiValueMap<String, String> queryParams) {
        List<ErrorDetails> errorList = queryParams.entrySet().stream()
                .filter(entry -> !PARAM_VALUES.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream()
                        .map(v -> new ErrorDetails(entry.getKey(), v, "Invalid query field")))
                .toList();

        if (!errorList.isEmpty()) {
            var e = new ValidationException("Validation failed", errorList);
            return Mono.error(e);
        } else {
            return Mono.empty();
        }
    }

}