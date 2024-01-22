package com.hrs.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * The type Gateway configuration.
 */
@Configuration
public class GatewayConfiguration {

    private final AuthenticationFilter authFilter;

    /**
     * Instantiates a new Gateway configuration.
     *
     * @param filter the filter
     */
    public GatewayConfiguration(AuthenticationFilter filter) {
        this.authFilter = filter;
    }


    /**
     * Custom route locator route locator.
     *
     * @param builder the builder
     * @return the route locator
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("User-Service", route -> route.path("/user/**").filters(f-> f.filter((exchange, chain) ->
                        {
                            ServerHttpRequest req = exchange.getRequest();
                            addOriginalRequestUrl(exchange, req.getURI());
                            String path = req.getURI().getRawPath();
                            String newPath = path.replaceAll(
                                    "userId","")
                                    ;
                            ServerHttpRequest request = req.mutate().path(newPath).build();
                            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());
                            return chain.filter(exchange.mutate().request(request).build());
                        }))
                        .uri("lb://USER-SERVICE"))
                .route("Hotel-Service", r -> r.path("/hotel/**", "/room/**").filters(f-> f.filter(authFilter))
                        .uri("lb://HOTEL-SERVICE"))
                .route("Booking-Service", r -> r.path("/booking/**").filters(f-> f.filter(authFilter))
                        .uri("lb://BOOKING-SERVICE"))
                .build();
    }
}
