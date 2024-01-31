//package com.hrs.gateway.exceptionHandler;
//
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class GlobalExceptionHandler {
//
//    @Bean
//    @Order(-1) // Make sure this filter is executed before others
//    public GlobalFilter customErrorFilter() {
//        return (exchange, chain) -> {
//            return chain.filter(exchange)
//                    .onErrorResume(ex -> handleException(ex, exchange));
//        };
//    }
//
//    private Mono<Void> handleException(Throwable ex, ServerWebExchange exchange) {
//        // Customize the error response based on the exception
//        HttpStatus httpStatus = determineHttpStatus(ex);
//
//        // You can also log the exception or perform other actions here
//
//        // Set a custom response message
//        exchange.getResponse().setStatusCode(httpStatus);
//        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
//        String responseBody = "{\"error\": \"" + ex.getMessage() + "\"}";
//        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
//                .bufferFactory().wrap(responseBody.getBytes())));
//    }
//
//    private HttpStatus determineHttpStatus(Throwable ex) {
//        // Determine the appropriate HTTP status based on the exception type
//        // You can customize this logic based on your requirements
//        return HttpStatus.INTERNAL_SERVER_ERROR;
//    }
//}
