package com.vertex.oauth.controller;


import com.vertex.backendcore.service.CaptchaGeneratorService;
import com.vertex.backendcore.service.redis.CacheService;
import com.vertex.core.api.oauth.TokenApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.config.MessageProvider;
import com.vertex.core.dto.response.CaptchaResponse;
import com.vertex.core.dto.response.TokenResponse;
import com.vertex.core.exceptions.ServiceException;
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
import java.util.UUID;

import static com.vertex.backendcore.security.CryptoUtil.decryptWithPrivateRsaKey;

@Slf4j
@RestController
public class TokenController implements TokenApi {

    private final long expirationRequestTime;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CacheService cacheService;
    private final CaptchaGeneratorService captchaGeneratorService;

    public TokenController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            CacheService cacheService,
            CaptchaGeneratorService captchaGeneratorService,
            @Value("${security.token.expiration-request-time}") long expirationRequestTime
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.expirationRequestTime = expirationRequestTime;
        this.cacheService = cacheService;
        this.captchaGeneratorService = captchaGeneratorService;
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
            throw new ServiceException(MessageProvider.getMessage("Unauthorized"));
        }
        return RestResponse.ok(new TokenResponse(jwtTokenProvider.generateToken(si.username, request)));
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
            throw new ServiceException(MessageProvider.getMessage("CaptchaIsRequired"));
        }

        String cacheKey = CommonConstant.CAPTCHA + captchaId;
        String expectedAnswer = cacheService.getCacheByKey(cacheKey);

        if (expectedAnswer == null) {
            throw new ServiceException(MessageProvider.getMessage("CaptchaTimeout"));
        }

        if (!expectedAnswer.equals(captchaAnswer)) {
            throw new ServiceException(MessageProvider.getMessage("CaptchaNotMatch"));
        }

        cacheService.deleteCache(cacheKey);
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
            throw new ServiceException(MessageProvider.getMessage("RequestTimeout"));

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