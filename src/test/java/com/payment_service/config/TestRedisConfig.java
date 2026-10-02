package com.payment_service.config;

import com.payment_service.service.RedisService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@TestConfiguration
public class TestRedisConfig {

    @Bean
    @Primary
    public RedisService testRedisService() {
        return Mockito.mock(RedisService.class);
    }

    @Bean
    @Primary
    public CacheManager testCacheManager() {
        return new NoOpCacheManager();
    }

    @Bean
    @Primary
    public RedisConnectionFactory testRedisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }
}