package com.vertex.core.util;

import com.vertex.core.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
@Getter
@Setter
public class RestResponse<T> extends ResponseMessage {

    private T data;

    public RestResponse() {
        super();
    }

    public RestResponse(String message, int code, T data) {
        super(message, code);
        this.data = data;
    }

    public static <T> RestResponse<T> ok(T data) {
        ResponseCode responseCode = ResponseCode.SUCCESS;
        return new RestResponse<>(responseCode.getMessage(), responseCode.getCode(), data);
    }

    public static <T> RestResponse<T> error(String message, int code) {
        RestResponse<T> response = new RestResponse<>();
        response.setMessage(message);
        response.setCode(code);
        return response;
    }
}