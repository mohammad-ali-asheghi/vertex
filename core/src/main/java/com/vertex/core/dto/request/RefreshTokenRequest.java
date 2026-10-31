package com.vertex.core.dto.request;

import java.io.Serializable;

public record RefreshTokenRequest(
        String refreshToken
) implements Serializable {
}
