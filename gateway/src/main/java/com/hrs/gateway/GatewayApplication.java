package com.hrs.gateway;

import com.hrs.gateway.exceptionHandler.CustomException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

/**
 * The type Gateway application.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

//	@Bean
//	public GlobalFilter customErrorFilter() {
//		return (exchange, chain) -> {
//			return chain.filter(exchange)
//					.onErrorResume(ex -> {
//						// Handle the error and return a custom error response
//						return Mono.error(new CustomException("Gateway Error"));
//					});
//		};
//	}

	}

