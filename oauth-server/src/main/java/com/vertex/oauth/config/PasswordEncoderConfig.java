package com.vertex.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Configuration
public class PasswordEncoderConfig {

    @Value("${security.password.encode-key}")
    private String ENCODE_KEY;

    @Value("${security.algorithm.encode-alg}")
    private String ENCODE_ALGORITHM;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                try {
                    Mac hmac = Mac.getInstance(ENCODE_ALGORITHM);
                    SecretKeySpec keySpec = new SecretKeySpec(
                            ENCODE_KEY.getBytes(StandardCharsets.UTF_8),
                            ENCODE_ALGORITHM
                    );
                    hmac.init(keySpec);
                    byte[] hash = hmac.doFinal(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
                    return Base64.getEncoder().encodeToString(hash);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return Objects.equals(encode(rawPassword), encodedPassword);
            }
        };
    }
}
