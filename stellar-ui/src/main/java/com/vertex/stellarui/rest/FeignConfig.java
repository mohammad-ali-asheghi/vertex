package com.vertex.stellarui.rest;

import com.vertex.stellarui.auth.AuthManager;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Configuration
public class FeignConfig {

    private static final String BEARER_PREFIX = "Bearer ";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String url = template.url();

            if (url.endsWith("/v1/token") || url.endsWith("/v1/captcha")) {
                return;
            }

            String token = AuthManager.getToken();

            if (token != null && !token.isBlank()) {
                if (!token.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
                    token = BEARER_PREFIX + token;
                }

                template.header(AUTHORIZATION, token);
            }
        };
    }
}