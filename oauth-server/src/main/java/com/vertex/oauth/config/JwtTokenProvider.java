package com.vertex.oauth.config;

import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.security.HttpUtil;
import com.vertex.backendcore.service.redis.CacheService;
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
import java.util.*;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String REVOKED_TOKEN_PREFIX = "REVOKED_TOKEN:";
    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";
    private static final long DEFAULT_REFRESH_EXPIRATION_TIME = 14L * 24 * 60 * 60 * 1000; // 14 days

    private final SecretKey secretKey;
    private final long expirationTime;
    private final UserDetailsService userDetailsService;
    private final CacheService cacheService;

    public JwtTokenProvider(
            @Value("${security.password.secret-key}") String secretKey,
            @Value("${security.token.expiration-time}") long expirationTime,
            UserDetailsService userDetailsService,
            CacheService cacheService
    ) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
        this.userDetailsService = userDetailsService;
        this.cacheService = cacheService;
    }

    public String generateToken(String username, HttpServletRequest request) {
        try {
            UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(username);

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put(CLAIM_TOKEN_TYPE, TYPE_ACCESS);
            claims.put("authorities", extractAuthorities(user));
            addSecurityContext(claims, request);

            long now = System.currentTimeMillis();

            String token = Jwts.builder()
                    .claims(claims)
                    .subject(username)
                    .id(UUID.randomUUID().toString())
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + expirationTime))
                    .signWith(secretKey)
                    .compact();

            log.info("Access token generated successfully for user: {}", username);
            return HttpUtil.BEARER_PREFIX + token;
        } catch (Exception e) {
            log.error("Error generating token for user: {}", username, e);
            throw new JwtGenerationException("Failed to generate token", e);
        }
    }

    public String generateRefreshToken(String username, HttpServletRequest request) {
        try {
            UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(username);

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put(CLAIM_TOKEN_TYPE, TYPE_REFRESH);
            addSecurityContext(claims, request);

            long now = System.currentTimeMillis();

            String refreshToken = Jwts.builder()
                    .claims(claims)
                    .subject(username)
                    .id(UUID.randomUUID().toString())
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + DEFAULT_REFRESH_EXPIRATION_TIME))
                    .signWith(secretKey)
                    .compact();

            log.info("Refresh token generated successfully for user: {}", username);
            return refreshToken;
        } catch (Exception e) {
            log.error("Error generating refresh token for user: {}", username, e);
            throw new JwtGenerationException("Failed to generate refresh token", e);
        }
    }

    public Jws<Claims> getAllClaims(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Token is empty or null");
        }
        String cleanToken = HttpUtil.extractToken(token);
        return HttpUtil.parseToken(cleanToken, secretKey);
    }

    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token, HttpServletRequest request) {
        try {
            String cleanToken = HttpUtil.extractToken(token);
            if (isTokenRevoked(cleanToken)) {
                log.warn("Attempt to use revoked token");
                return false;
            }
            Jws<Claims> jwsClaims = getAllClaims(cleanToken);
            String tokenType = jwsClaims.getPayload().get(CLAIM_TOKEN_TYPE, String.class);
            if (tokenType != null && !TYPE_ACCESS.equals(tokenType)) {
                log.warn("Expected access token but found: {}", tokenType);
                return false;
            }
            return validateTokenContext(jwsClaims, request);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public boolean validateRefreshToken(String refreshToken, HttpServletRequest request) {
        try {
            String cleanToken = HttpUtil.extractToken(refreshToken);
            if (isTokenRevoked(cleanToken)) {
                log.warn("Attempt to use revoked refresh token");
                return false;
            }
            Jws<Claims> jwsClaims = getAllClaims(cleanToken);
            String tokenType = jwsClaims.getPayload().get(CLAIM_TOKEN_TYPE, String.class);
            if (!TYPE_REFRESH.equals(tokenType)) {
                log.warn("Expected refresh token but found: {}", tokenType);
                return false;
            }
            return validateTokenContext(jwsClaims, request);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT refresh token");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT refresh token: {}", e.getMessage());
        }
        return false;
    }

    public void revokeToken(String token) {
        if (StringUtils.isBlank(token)) return;
        try {
            String cleanToken = HttpUtil.extractToken(token);
            Jws<Claims> claims = getAllClaims(cleanToken);
            Date expiration = claims.getPayload().getExpiration();
            long remainingMillis = expiration.getTime() - System.currentTimeMillis();
            if (remainingMillis > 0) {
                long minutes = Math.max(1, remainingMillis / (60 * 1000));
                cacheService.setCache(REVOKED_TOKEN_PREFIX + cleanToken, "revoked", minutes);
                log.info("Token revoked successfully for subject: {}", claims.getPayload().getSubject());
            }
        } catch (Exception e) {
            log.warn("Failed to parse token during revocation: {}", e.getMessage());
        }
    }

    public boolean isTokenRevoked(String cleanToken) {
        try {
            return cacheService.hasKey(REVOKED_TOKEN_PREFIX + cleanToken);
        } catch (Exception e) {
            log.warn("Redis check for revoked token failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean validateTokenContext(Jws<Claims> claimsJws, HttpServletRequest request) {
        if (request == null) {
            return true;
        }
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
        if (request != null) {
            claims.put("ipAddress", HttpUtil.getClientIpAddress(request));
            claims.put("userAgent", HttpUtil.analyse(request.getHeader("User-Agent")));
        }
        claims.put("loginTime", System.currentTimeMillis());
    }

    public static class JwtGenerationException extends RuntimeException {
        public JwtGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}