package com.hrs.gateway.config;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * The type Authentication filter.
 */
@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {

    private RouterValidator routerValidator;
    private JwtUtil jwtUtil;

    /**
     * Instantiates a new Authentication filter.
     *
     * @param routerValidator the router validator
     * @param jwtUtil         the jwt util
     */
    public AuthenticationFilter(RouterValidator routerValidator, JwtUtil jwtUtil) {
        this.routerValidator = routerValidator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (this.isAuthMissing(request)) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String token = this.getAuthHeader(request);

            if (jwtUtil.isInvalid(token)) {
                return this.onError(exchange, HttpStatus.FORBIDDEN);
            }

            this.updateRequest(exchange, token);
        }

            // Get the current path
            String originalPath = exchange.getRequest().getPath().toString();

            // Check if the path contains "userInfo"
        exchange = updateWebExchangePathIfRequired(exchange, originalPath);

        return chain.filter(exchange);

    }

    private ServerWebExchange updateWebExchangePathIfRequired(ServerWebExchange exchange, String originalPath) {
        if (originalPath.contains("userInfo")) {
            // Modify the path by replacing "userInfo" with the userId from the token
            String modifiedPath = originalPath.replace("userInfo", jwtUtil.getAllClaimsFromToken(getAuthHeader(exchange.getRequest())).get("userId").toString());

            // Set the modified path in the request
            exchange = exchange.mutate()
                    .request(exchange.getRequest().mutate().path(modifiedPath).build())
                    .build();
        }
        return exchange;
    }

    private Mono<Void> handleException(Throwable ex, ServerWebExchange exchange) {
        // Customize the error response based on the exception
        HttpStatus httpStatus = determineHttpStatus(ex);

        // You can also log the exception or perform other actions here

        // Set a custom response message
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String responseBody = "{\"error\": \"" + ex.getMessage() + "\"}";
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(responseBody.getBytes())));
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        // Determine the appropriate HTTP status based on the exception type
        // You can customize this logic based on your requirements
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private void updateRequest(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        exchange.getRequest().mutate()
                .header("userId",  String.valueOf(claims.get("userId")))
                .header("role", String.valueOf(claims.get("role")))
                .build();
    }
}