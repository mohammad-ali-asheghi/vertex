package com.vertex.backendcore.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertex.backendcore.entity.redis.CodeTypeDocument;
import com.vertex.backendcore.entity.redis.CodeTypeItemDocument;
import com.vertex.backendcore.security.CryptoUtil;
import com.vertex.core.config.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisDataInitializer implements CommandLineRunner {

    @Value("${spring.application.name:default-service}")
    private String serviceName;

    private static final String CODE_TYPE_FILE_NAME = "code-type.json";
    private static final String CODE_TYPE_ITEM_FILE_NAME = "code-type-item.json";
    private static final String OAUTH_SERVICE_NAME = "OAUTH-SERVICE";

    private final RedisTemplate<String, String> redisTemplate;
    private final CodeTypeService codeTypeService;
    private final CodeTypeItemService codeTypeItemService;
    private final ObjectMapper objectMapper;

    private static final String HASH_KEY_PREFIX = "migration:hash:";

    @Override
    public void run(String... args) throws Exception {
        syncData(CODE_TYPE_FILE_NAME, CodeTypeDocument.class);
        syncData(CODE_TYPE_ITEM_FILE_NAME, CodeTypeItemDocument.class);
        generatedKey();
    }

    private void generatedKey() {
        if (!serviceName.equals(OAUTH_SERVICE_NAME))
            return;
        clearExistingKeys();
        try {
            generateAndStoreNewKeys();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void syncData(String fileName, Class<T> clazz) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:data/" + fileName);

        if (!resource.exists()) {
            System.out.println("⚠️ File " + fileName + " not found in this service. Skipping...");
            return;
        }

        byte[] fileBytes = StreamUtils.copyToByteArray(resource.getInputStream());
        String currentHash = DigestUtils.md5DigestAsHex(fileBytes);
        String redisHashKey = HASH_KEY_PREFIX + serviceName + ":" + fileName;
        String storedHash = redisTemplate.opsForValue().get(redisHashKey);

        if (storedHash == null || !storedHash.equals(currentHash)) {
            System.out.println("🔄 Change detected in " + fileName + ". Updating Redis...");

            List<T> data = objectMapper.readValue(fileBytes,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));

            initData(data, fileName);

            redisTemplate.opsForValue().set(redisHashKey, currentHash);
            System.out.println("✅ " + fileName + " synchronized successfully.");
        } else {
            System.out.println("ℹ️ No changes in " + fileName + ". Skipping initialization.");
        }
    }

    private void generateAndStoreNewKeys() throws NoSuchAlgorithmException {
        KeyPair pair = CryptoUtil.generateRsaKeyPair();
        String publicKeyBase64 = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

        redisTemplate.opsForValue().set(CommonConstant.PUBLIC_KEY, publicKeyBase64);
        redisTemplate.opsForValue().set(CommonConstant.PRIVATE_KEY, privateKeyBase64);
        System.out.println("RSA KeyPair generated and cached By Public Key: " + publicKeyBase64);
    }

    private void clearExistingKeys() {
        redisTemplate.delete(CommonConstant.PUBLIC_KEY);
        redisTemplate.delete(CommonConstant.PRIVATE_KEY);
    }

    @SuppressWarnings("unchecked")
    private <T> void initData(List<T> data, String fileName) {
        switch (fileName) {
            case CODE_TYPE_FILE_NAME -> codeTypeService.createList((Iterable<CodeTypeDocument>) data);
            case CODE_TYPE_ITEM_FILE_NAME -> codeTypeItemService.createList((Iterable<CodeTypeItemDocument>) data);
            default -> System.out.println("init service not found!");
        }
    }
}