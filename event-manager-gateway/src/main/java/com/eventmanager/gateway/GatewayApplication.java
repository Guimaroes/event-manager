package com.eventmanager.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/events/**", "/registrations/**")
						.uri("http://localhost:8089"))
				.route(p -> p
						.path("/auth/**", "/users/**")
						.uri("http://localhost:8088"))
				.route(p -> p
						.path("/email/**")
						.uri("http://localhost:8087"))
				.route(p -> p
						.path("/certificates/**")
						.uri("http://localhost:8086"))
				.build();
	}
}
