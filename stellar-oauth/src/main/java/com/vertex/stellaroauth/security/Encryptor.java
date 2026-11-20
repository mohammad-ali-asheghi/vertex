package com.vertex.stellaroauth.security;

import com.vertex.core.config.CommonConstant;
import com.vertex.core.config.MessageProvider;
import com.vertex.stellaroauth.service.TokenClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Component
public class Encryptor {

    private final StringRedisTemplate redisTemplate;
    private final TokenClient tokenClient;

    public Encryptor(StringRedisTemplate redisTemplate, @Lazy TokenClient tokenClient) {
        this.redisTemplate = redisTemplate;
        this.tokenClient = tokenClient;
    }

    public String encryptData(String username, String password, String captchaId, String captchaAnswer) {
        String publicKeyBase64 = null;
        try {
            if (redisTemplate != null) {
                publicKeyBase64 = redisTemplate.opsForValue().get(CommonConstant.PUBLIC_KEY);
            }
        } catch (Exception ex) {
            log.warn("Redis read for public key failed: {}", ex.getMessage());
        }

        if (publicKeyBase64 == null || publicKeyBase64.isBlank()) {
            try {
                publicKeyBase64 = tokenClient.getPublicKey();
                if (publicKeyBase64 != null && !publicKeyBase64.isBlank() && redisTemplate != null) {
                    redisTemplate.opsForValue().set(CommonConstant.PUBLIC_KEY, publicKeyBase64);
                }
            } catch (Exception ex) {
                log.warn("Fetch public key from TokenClient failed: {}", ex.getMessage());
            }
        }

        if (publicKeyBase64 == null || publicKeyBase64.isBlank()) {
            throw new IllegalStateException("کلید عمومی RSA یافت نشد. لطفاً مطمئن شوید سرویس احراز هویت اجرا شده است.");
        }

        try {
            byte[] publicBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(CommonConstant.RSA);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance(CommonConstant.INSTANCE_KEY);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            String dataToEncrypt = username +
                    "|" +
                    password +
                    "|" +
                    LocalDateTime.now() +
                    "|" +
                    captchaId +
                    "|" +
                    captchaAnswer;
            byte[] encryptedBytes = cipher.doFinal(dataToEncrypt.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
