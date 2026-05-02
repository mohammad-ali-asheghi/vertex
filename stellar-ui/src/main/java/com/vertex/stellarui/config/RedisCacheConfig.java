package com.vertex.stellarui.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {

    private static final long DURATION_TTL = 7;

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheConfiguration cacheConfiguration() {
        RedisSerializer<Object> serializer = RedisSerializer.json();
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(DURATION_TTL))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}