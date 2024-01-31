package com.hrs.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

/**
 * The type Router validator.
 */
@Component
public class RouterValidator {

    /**
     * The constant openApiEndpoints.
     */
//ADD login
    public static final List<String> openApiEndpoints = List.of(
            "/user/register","/user/login"
    );

    /**
     * The Is secured.
     */
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
