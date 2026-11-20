package com.vertex.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("oauth-server", r -> r.path("/oauth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://OAUTH-SERVER"))
                .route("oauth-ui-service", r -> r.path("/**")
                        .uri("lb://OAUTH-UI-SERVICE"))
                .build();
    }
}
