package com.vertex.oauth.controller;

import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.service.CaptchaGeneratorService;
import com.vertex.backendcore.service.redis.CacheService;
import com.vertex.core.api.oauth.TokenApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.config.MessageProvider;
import com.vertex.core.dto.request.RefreshTokenRequest;
import com.vertex.core.dto.response.CaptchaResponse;
import com.vertex.core.dto.response.TokenResponse;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import static com.vertex.backendcore.security.CryptoUtil.decryptWithPrivateRsaKey;

@Slf4j
@RestController
public class TokenController implements TokenApi {

    private final JwtTokenProvider jwtTokenProvider;
    private final CaptchaGeneratorService captchaGeneratorService;
    private final AuthenticationManager authenticationManager;
    private final CacheService cacheService;
    private final UserDetailsService userDetailsService;

    @Value("${security.request.expiration-time}")
    private Long expirationRequestTime;

    public TokenController(
            JwtTokenProvider jwtTokenProvider,
            CaptchaGeneratorService captchaGeneratorService,
            AuthenticationManager authenticationManager,
            CacheService cacheService,
            UserDetailsService userDetailsService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.captchaGeneratorService = captchaGeneratorService;
        this.authenticationManager = authenticationManager;
        this.cacheService = cacheService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public RestResponse<TokenResponse> getToken(String encryptedBase64) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        SecurityInfo si = decrypt(encryptedBase64);
        validateCaptcha(si.captchaId(), si.captchaAnswer());
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(si.username, si.password)
            );
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (Exception e) {
            log.warn("Authentication failed for user: {}", si.username());
            throw new ServiceException(MessageProvider.getMessageByKey("Unauthorized"));
        }

        String accessToken = jwtTokenProvider.generateToken(si.username, request);
        String refreshToken = jwtTokenProvider.generateRefreshToken(si.username, request);
        log.info("Tokens successfully generated for user: {}", si.username());

        return RestResponse.ok(new TokenResponse(accessToken, refreshToken, 3600L, "Bearer"));
    }

    @Override
    public RestResponse<TokenResponse> refreshToken(RefreshTokenRequest request) {
        if (request == null || StringUtils.isBlank(request.refreshToken())) {
            throw new ServiceException(MessageProvider.getMessageByKey("RefreshTokenRequired"));
        }

        HttpServletRequest servletRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        boolean isValid = jwtTokenProvider.validateRefreshToken(request.refreshToken(), servletRequest);
        if (!isValid) {
            log.warn("Invalid or expired refresh token attempted");
            throw new ServiceException(MessageProvider.getMessageByKey("InvalidOrExpiredRefreshToken"));
        }

        String username = jwtTokenProvider.getUsernameFromToken(request.refreshToken());
        UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(username);
        if (user == null || !user.isEnabled() || !user.isAccountNonLocked()) {
            throw new ServiceException(MessageProvider.getMessageByKey("AccountDisabledOrLocked"));
        }

        // Revoke the old refresh token to prevent replay attacks (refresh token rotation)
        jwtTokenProvider.revokeToken(request.refreshToken());

        String newAccessToken = jwtTokenProvider.generateToken(username, servletRequest);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username, servletRequest);

        log.info("Token successfully refreshed for user: {}", username);
        return RestResponse.ok(new TokenResponse(newAccessToken, newRefreshToken, 3600L, "Bearer"));
    }

    @Override
    public ResponseMessage logout(RefreshTokenRequest request) {
        HttpServletRequest servletRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String authHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authHeader)) {
            jwtTokenProvider.revokeToken(authHeader);
        }

        if (request != null && StringUtils.isNotBlank(request.refreshToken())) {
            jwtTokenProvider.revokeToken(request.refreshToken());
        }

        SecurityContextHolder.clearContext();
        log.info("User session logged out and tokens revoked");
        return ResponseMessage.success();
    }

    @Override
    public RestResponse<CaptchaResponse> getCaptcha() {
        String captchaText = captchaGeneratorService.generateRandomText();
        String captchaId = UUID.randomUUID().toString();

        cacheService.setCache(CommonConstant.CAPTCHA + captchaId, captchaText, 2);
        log.debug("Captcha generated: {} | ID: {}", captchaText, captchaId);
        String base64Image = captchaGeneratorService.generateBase64Image(captchaText);

        return RestResponse.ok(new CaptchaResponse(captchaId, base64Image));
    }

    private void validateCaptcha(String captchaId, String captchaAnswer) {
        if (captchaId == null || captchaAnswer == null) {
            throw new ServiceException(MessageProvider.getMessageByKey("CaptchaIsRequired"));
        }

        String cacheKey = CommonConstant.CAPTCHA + captchaId;
        String expectedAnswer = cacheService.getCacheByKey(cacheKey);

        if (expectedAnswer == null) {
            throw new ServiceException(MessageProvider.getMessageByKey("CaptchaTimeout"));
        }

        // Challenge is single-use: immediately delete
        cacheService.deleteCache(cacheKey);

        if (!expectedAnswer.equalsIgnoreCase(captchaAnswer.trim())) {
            throw new ServiceException(MessageProvider.getMessageByKey("CaptchaNotMatch"));
        }
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
            throw new ServiceException(MessageProvider.getMessageByKey("RequestTimeout"));

        String captchaId = data.length > 3 ? data[3] : null;
        String captchaAnswer = data.length > 4 ? data[4] : null;

        return new SecurityInfo(data[0], data[1], requestTime, captchaId, captchaAnswer);
    }

    record SecurityInfo(String username,
                        String password,
                        LocalDateTime requestTime,
                        String captchaId,
                        String captchaAnswer) {
    }
}