package com.vertex.backendcore.config;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@AutoConfiguration
@EnableRedisDocumentRepositories(
        basePackages = {
                "com.vertex.backendcore.repository.redis",
                "com.vertex.backendcore.entity.redis"
        }
)
@ComponentScan(basePackages = "com.vertex.backendcore.service.redis")
public class RedisCacheAutoConfiguration {

    private static final long DURATION_TTL = 7;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        RedisSerializer<Object> serializer = RedisSerializer.json();
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(DURATION_TTL))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}