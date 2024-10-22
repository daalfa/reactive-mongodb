//package com.davi.demo.restaurant.service.model;
//
//import com.davi.demo.restaurant.service.enums.Param;
//import com.davi.demo.restaurant.service.exceptions.ValidationException;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//public class Validation {
//    private static final int PARAMETER_MAX_LEN = 40;
//
//
//    /**
//     * Validate if there are no unknown query parameters. <br>
//     * Validate if values are not empty or null. <br>
//     * Convert the Map keys to lowercase.
//     */
//    private Mono<Map<Param, String>> validateAndNormalizeParameters(Map<String, String> paramMap) {
//
//        return Flux.fromIterable(paramMap.entrySet())
//                .flatMap(e -> {
//                    if (e.getValue() == null || e.getValue().isEmpty() || e.getValue().length() > PARAMETER_MAX_LEN) {
//                        log.error("Invalid parameter: {}", e.getKey());
//                        return Mono.error(new ValidationException(STR."Invalid parameter: \{e.getKey()}"));
//                    } else {
//                        return Mono.just(e);
//                    }
//                })
//                .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue))
//                .flatMap(map -> {
//                    List<String> invalidParams = map.keySet().stream()
//                            .filter(param -> !VALID_PARAMETERS.contains(param))
//                            .toList();
//
//                    if (!invalidParams.isEmpty()) {
//                        log.error("Invalid parameters: {}", invalidParams);
//                        return Mono.error(new ValidationException(STR."Invalid parameters: \{invalidParams}"));
//                    }
//
//                    return Mono.just(map);
//                });
//    }
//
//    /**
//     * Validate if the text query do not have special symbols. <br>
//     * This is a way to sanitize the search text for only
//     * words, numbers, hyphen and white space <br>
//     * Examples: <br>
//     * District9 = valid <br>
//     * Tex-Mex = valid <br>
//     * Bistro&Bar = valid ('&' should be encoded as '%26') <br>
//     * Fish and Chips = valid
//     */
//    private Mono<String> getValidatedStringExpression(String parameterName, String value) {
//        if(value == null) return Mono.empty();
//
//        Pattern pattern = Pattern.compile("^[a-zA-Z0-9& -]+$");
//        if(!pattern.matcher(value).matches()) {
//            log.error("Parameter {} has invalid expression {}", parameterName, value);
//            return Mono.error(new ValidationException(STR."Parameter \{parameterName} has invalid expression \{value}"));
//        }
//
//        return Mono.just(value);
//    }
//
//    /**
//     * Validate if the value is a valid number without Exponent 'E'<br>
//     * Return the parsed number or empty
//     */
//    private Mono<Double> getValidatedNumericExpression(String parameterName, String value) {
//        if(value == null) return Mono.empty();
//
//        Pattern pattern = Pattern.compile("[EeNn-]");
//        if(pattern.matcher(value).find()) {
//            log.error("Parameter {} has invalid format {}", parameterName, value);
//            return Mono.error(new ValidationException(STR."Parameter \{parameterName} has invalid format \{value}"));
//        }
//
//        try {
//            return Mono.just(Double.parseDouble(value));
//        } catch (NumberFormatException e) {
//            log.error("Parameter {} has invalid format {}", parameterName, value);
//            return Mono.error(new ValidationException(STR."Parameter \{parameterName} has invalid format \{value}"));
//        }
//    }
//}
