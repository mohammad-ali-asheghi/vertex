package com.vertex.backendcore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Slf4j
@SuppressWarnings("unused")
public final class HttpUtil {

    private static final Integer CACHE_SIZE = 10000;
    private static final UserAgentAnalyzer analyzer = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withCache(CACHE_SIZE)
            .build();

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = 7;

    // Header names for IP detection
    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP"
    };

    private HttpUtil() {

    }

    public static boolean isAuthenticated() {
        return getTokenFromCurrentRequest().isPresent();
    }

    public static Optional<String> getTokenFromCurrentRequest() {
        return getCurrentHttpServletRequest()
                .map(HttpUtil::getAuthorizationHeader)
                .filter(StringUtils::isNotBlank)
                .map(token -> token.startsWith(BEARER_PREFIX) ? token.substring(BEARER_PREFIX_LENGTH) : token);
    }

    private static String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    public static Optional<HttpServletRequest> getCurrentHttpServletRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        // Try to get IP from headers
        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                return extractFirstIp(ip);
            }
        }

        // Fallback to remote address
        String remoteAddr = request.getRemoteAddr();

        // Handle IPv6 localhost
        if (LOCALHOST_IPV6.equals(remoteAddr)) {
            try {
                remoteAddr = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.debug("Could not get localhost address", e);
                return LOCALHOST_IPV4;
            }
        }

        return remoteAddr;
    }

    private static boolean isValidIp(String ip) {
        return StringUtils.isNotBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }

    private static String extractFirstIp(String ip) {
        if (ip != null && ip.contains(",")) {
            return ip.split(",")[0].trim();
        }
        return ip;
    }

    public static String extractToken(String authorization) {
        if (authorization.startsWith(BEARER_PREFIX)) {
            authorization = authorization.substring(BEARER_PREFIX_LENGTH);
        }
        return authorization;
    }

    public static Map<String, String> getHeadersAsMap(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        if (request != null) {
            Collections.list(request.getHeaderNames())
                    .forEach(name -> headers.put(name, request.getHeader(name)));
        }
        return headers;
    }

    public static Map<String, String> getHeadersAsMap(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        if (response != null) {
            response.getHeaderNames()
                    .forEach(name -> headers.put(name, response.getHeader(name)));
        }
        return headers;
    }

    public static String analyse(String userAgentString) {
        UserAgent ua = analyzer.parse(userAgentString);

        String browser = ua.getValue("AgentName");
        String browserV = ua.getValue("AgentVersion");
        String os = ua.getValue("OperatingSystemNameVersion");
        String device = ua.getValue("DeviceClass");
        String bot = ua.getValue("IsBot");

        return String.format(
                "Browser: %s %s, OS: %s, Device: %s, Bot: %s",
                browser, browserV, os, device, bot
        );
    }

    // The corrected parsing method
    public static Jws<Claims> parseToken(String token, SecretKey secretKey) {
        return Jwts.parser()          // <-- Replaces parserBuilder()
                .verifyWith(secretKey) // <-- Replaces setSigningKey()
                .build()               // <-- Returns the immutable JwtParser
                .parseSignedClaims(token); // <-- Replaces parseClaimsJws()
    }

    // The corrected username extraction method
    public static String getCurrentUsername(String token, SecretKey secretKey) {
        try {
            Jws<Claims> jws = parseToken(token, secretKey);
            return jws.getPayload().getSubject(); // <-- Replaces .getBody()
        } catch (Exception e) {
            log.error("Error extracting username from token", e);
            return null;
        }
    }
}