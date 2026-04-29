package com.vertex.oauth.config;

import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.security.HttpUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationTime;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(
            @Value("${security.password.secret-key}") String secretKey,
            @Value("${security.token.expiration-time}") long expirationTime,
            UserDetailsService userDetailsService
    ) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
        this.userDetailsService = userDetailsService;
    }

    public String generateToken(String username, HttpServletRequest request) {
        try {
            UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(username);

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("authorities", extractAuthorities(user));
            addSecurityContext(claims, request);

            long now = System.currentTimeMillis();

            String token = Jwts.builder()
                    .claims(claims)
                    .subject(username)
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + expirationTime))
                    .signWith(secretKey)
                    .compact();

            log.info("Token generated successfully for user: {}", username);
            return HttpUtil.BEARER_PREFIX + token;
        } catch (Exception e) {
            log.error("Error generating token for user: {}", username, e);
            throw new JwtGenerationException("Failed to generate token", e);
        }
    }

    public Jws<Claims> getAllClaims(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Token is empty or null");
        }
        String cleanToken = HttpUtil.extractToken(token);
        return HttpUtil.parseToken(cleanToken, secretKey);
    }

    public boolean validateToken(String token, HttpServletRequest request) {
        try {
            Jws<Claims> jwsClaims = getAllClaims(token);
            return validateTokenContext(jwsClaims, request);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    private boolean validateTokenContext(Jws<Claims> claimsJws, HttpServletRequest request) {
        Claims claims = claimsJws.getPayload();
        String currentIp = HttpUtil.getClientIpAddress(request);
        String currentUserAgent = HttpUtil.analyse(request.getHeader("User-Agent"));
        String tokenIp = claims.get("ipAddress", String.class);
        String tokenUserAgent = claims.get("userAgent", String.class);

        if (tokenIp != null && !currentIp.equals(tokenIp)) {
            log.warn("IP mismatch. Token: {}, Current: {}", tokenIp, currentIp);
            return false;
        }

        if (tokenUserAgent != null && !currentUserAgent.equals(tokenUserAgent)) {
            log.warn("User-Agent mismatch");
            return false;
        }

        return true;
    }

    private List<String> extractAuthorities(UserEntity user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    private void addSecurityContext(Map<String, Object> claims, HttpServletRequest request) {
        claims.put("ipAddress", HttpUtil.getClientIpAddress(request));
        claims.put("userAgent", HttpUtil.analyse(request.getHeader("User-Agent")));
        claims.put("loginTime", System.currentTimeMillis());
    }

    public static class JwtGenerationException extends RuntimeException {
        public JwtGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}