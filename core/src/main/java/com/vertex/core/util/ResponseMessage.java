package com.vertex.core.util;

import com.vertex.core.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
@Getter
@Setter
public class ResponseMessage {

    private String message;
    private int code;

    public ResponseMessage(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public ResponseMessage() {
    }

    public static ResponseMessage success() {
        ResponseCode code = ResponseCode.SUCCESS;
        return new ResponseMessage(code.getMessage(), code.getCode());
    }

    public static ResponseMessage fail() {
        ResponseCode code = ResponseCode.FAIL;
        return new ResponseMessage(code.getMessage(), code.getCode());
    }

    public static ResponseMessage success(String message) {
        return new ResponseMessage(message, ResponseCode.SUCCESS.getCode());
    }

    public static ResponseMessage fail(String message) {
        return new ResponseMessage(message, ResponseCode.FAIL.getCode());
    }

    public static ResponseMessage unauthorized() {
        ResponseCode code = ResponseCode.UNAUTHORIZE;
        return new ResponseMessage(code.getMessage(), code.getCode());
    }
}
