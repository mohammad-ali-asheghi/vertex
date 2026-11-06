package com.vertex.stellarui.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.stellarui.auth.AuthManager;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Slf4j
@Configuration
public class FeignConfig {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    @Bean
    public ErrorDecoder errorDecoder() {
        ErrorDecoder defaultDecoder = new ErrorDecoder.Default();
        return (methodKey, response) -> {
            if (response.status() == 401 && !methodKey.toLowerCase().contains("tokenclient")) {
                return defaultDecoder.decode(methodKey, response);
            }

            String body = null;
            if (response.body() != null) {
                try (InputStream is = response.body().asInputStream()) {
                    body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.warn("Failed to read Feign response body for {}: {}", methodKey, e.getMessage());
                }
            }

            if (body != null && !body.isBlank()) {
                try {
                    JsonNode node = OBJECT_MAPPER.readTree(body);
                    if (node.hasNonNull("message")) {
                        String message = node.get("message").asText();
                        if (!message.isBlank()) {
                            return new ServiceException(message);
                        }
                    }
                } catch (Exception e) {
                    log.debug("Feign response body is not JSON or does not contain message: {}", body);
                }
            }

            return defaultDecoder.decode(methodKey, response);
        };
    }
}