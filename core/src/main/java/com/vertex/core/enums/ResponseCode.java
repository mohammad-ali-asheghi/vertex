package com.vertex.core.enums;

import lombok.Getter;

@Getter
public enum ResponseCode {

    SUCCESS(0, "success"),
    FAIL(100, "fail"),
    UNAUTHORIZE(401, "unauthorize");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
