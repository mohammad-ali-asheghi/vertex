package com.vertex.core.api.oauth;

import com.vertex.core.dto.request.RefreshTokenRequest;
import com.vertex.core.dto.response.CaptchaResponse;
import com.vertex.core.dto.response.TokenResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SuppressWarnings("unused")
public interface TokenApi {

    @PostMapping(value = {"/v1/token"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<TokenResponse> getToken(@RequestBody String encryptedBase64);

    @PostMapping(value = {"/v1/token/refresh"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request);

    @PostMapping(value = {"/v1/token/logout"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage logout(@RequestBody(required = false) RefreshTokenRequest request);

    @GetMapping(value = {"/v1/captcha"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<CaptchaResponse> getCaptcha();
}