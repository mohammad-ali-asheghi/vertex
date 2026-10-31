package com.vertex.core.dto.response;

import java.io.Serializable;

@SuppressWarnings("unused")
public record TokenResponse(
        String token,
        String refreshToken,
        Long expiresIn,
        String tokenType
) implements Serializable {

    public TokenResponse(String token) {
        this(token, null, null, "Bearer");
    }

    public TokenResponse(String token, String refreshToken) {
        this(token, refreshToken, 3600L, "Bearer");
    }

    public TokenResponse(String token, String refreshToken, Long expiresIn) {
        this(token, refreshToken, expiresIn, "Bearer");
    }
}