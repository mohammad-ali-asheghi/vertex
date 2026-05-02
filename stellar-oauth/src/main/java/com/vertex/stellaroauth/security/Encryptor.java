package com.vertex.stellaroauth.security;

import com.vertex.core.config.CommonConstant;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class Encryptor {

    private final StringRedisTemplate redisTemplate;

    public String encryptData(String username, String password, String captchaId, String captchaAnswer) {
        try {
            String publicKeyBase64 = redisTemplate.opsForValue().get(CommonConstant.PUBLIC_KEY);

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
