package com.vertex.oauth.controller;


import com.vertex.backendcore.service.redis.CacheService;
import com.vertex.core.api.oauth.TokenApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.dto.response.TokenResponse;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import static com.vertex.backendcore.security.CryptoUtil.decryptWithPrivateRsaKey;

@Slf4j
@RestController
public class TokenController implements TokenApi {

    private final long expirationRequestTime;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CacheService cacheService;

    public TokenController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            CacheService cacheService,
            @Value("${security.token.expiration-request-time}") long expirationRequestTime
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.expirationRequestTime = expirationRequestTime;
        this.cacheService = cacheService;
    }

    @Override
    public RestResponse<TokenResponse> getToken(String encryptedBase64) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        SecurityInfo si = decrypt(encryptedBase64);
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(si.username, si.password)
            );
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (Exception e) {
            throw new ServiceException(ResponseMessage.unauthorized().getMessage());
        }
        return RestResponse.ok(new TokenResponse(jwtTokenProvider.generateToken(si.username, request)));
    }

    private SecurityInfo decrypt(String encryptedBase64) {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        byte[] decryptedBytes = decryptWithPrivateRsaKey(
                encryptedBytes,
                cacheService.getCacheByKey(CommonConstant.PRIVATE_KEY),
                CommonConstant.INSTANCE_KEY
        );
        String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);
        String[] data = decryptedData.split("\\|");
        LocalDateTime requestTime = LocalDateTime.parse(data[2]);
        if (requestTime.isAfter(LocalDateTime.now().plusSeconds(expirationRequestTime)))
            return new SecurityInfo(null, null, null);
        return new SecurityInfo(data[0], data[1], null);
    }

    record SecurityInfo(String username, String password, LocalDateTime requestTime) {
    }
}