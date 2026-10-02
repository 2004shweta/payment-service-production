package com.payment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper redisObjectMapper;

    /**
     * Set value with key
     */
    public void set(String key, Object value) {
        try {
            String jsonValue = redisObjectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, jsonValue);
            log.debug("Set Redis key: {}", key);
        } catch (Exception e) {
            log.error("Error setting Redis key: {}", key, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /**
     * Set value with key and expiration time
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            String jsonValue = redisObjectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, jsonValue, timeout, unit);
            log.debug("Set Redis key: {} with TTL: {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error setting Redis key with TTL: {}", key, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /**
     * Get value by key
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            String jsonValue = stringRedisTemplate.opsForValue().get(key);
            if (jsonValue == null) {
                log.debug("Get Redis key: {} - Not found", key);
                return null;
            }
            T value = redisObjectMapper.readValue(jsonValue, clazz);
            log.debug("Get Redis key: {} - Found", key);
            return value;
        } catch (Exception e) {
            log.error("Error getting Redis key: {}", key, e);
            return null;
        }
    }

    /**
     * Get value by key (old method for backward compatibility)
     */
    public Object get(String key) {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            log.debug("Get Redis key: {} - Found: {}", key, value != null);
            return value;
        } catch (Exception e) {
            log.error("Error getting Redis key: {}", key, e);
            return null;
        }
    }

    /**
     * Check if key exists
     */
    public boolean hasKey(String key) {
        try {
            Boolean exists = stringRedisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking Redis key existence: {}", key, e);
            return false;
        }
    }

    /**
     * Delete key
     */
    public boolean delete(String key) {
        try {
            Boolean deleted = stringRedisTemplate.delete(key);
            log.debug("Delete Redis key: {} - Success: {}", key, deleted);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            log.error("Error deleting Redis key: {}", key, e);
            return false;
        }
    }

    /**
     * Set expiration time for a key
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            Boolean result = stringRedisTemplate.expire(key, timeout, unit);
            log.debug("Set expiration for Redis key: {} - {} {}", key, timeout, unit);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting expiration for Redis key: {}", key, e);
            return false;
        }
    }

    /**
     * Get time to live for a key
     */
    public Long getExpire(String key) {
        try {
            return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error getting TTL for Redis key: {}", key, e);
            return null;
        }
    }

    /**
     * Set if absent (only if key doesn't exist)
     */
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        try {
            String jsonValue = redisObjectMapper.writeValueAsString(value);
            Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, jsonValue, timeout, unit);
            log.debug("SetIfAbsent Redis key: {} - Success: {}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting Redis key if absent: {}", key, e);
            return false;
        }
    }
}