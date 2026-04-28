package com.vertex.backendcore.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;

    public void setCache(String key, String value, long timeoutInMinutes) {
        redisTemplate.opsForValue().set(key, value, timeoutInMinutes, TimeUnit.MINUTES);
    }

    public void setCache(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getCacheByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }
}