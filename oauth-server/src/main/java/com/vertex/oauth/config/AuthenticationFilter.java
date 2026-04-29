package com.vertex.oauth.config;

import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.security.HttpUtil;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public AuthenticationFilter(
            @Value("${security.password.secret-key}") String secretKey,
            @Lazy JwtTokenProvider jwtTokenProvider,
            UserDetailsService userDetailsService
    ) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = HttpUtil.getTokenFromCurrentRequest().orElse(null);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token, request)) {
                authenticateUser(token);
            }
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token) {
        String username = HttpUtil.getCurrentUsername(token, secretKey);

        if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserEntity userDetails = (UserEntity) userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated user: {}", username);
        }
    }
}