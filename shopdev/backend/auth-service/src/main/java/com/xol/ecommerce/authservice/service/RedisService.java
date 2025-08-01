package com.xol.ecommerce.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void cacheUser(String username, Object userInfo, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(userInfo);
            redisTemplate.opsForValue().set("auth:user:" + username, json, ttl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to cache user", e);
        }
    }

    public void deleteUser(String username) {
        redisTemplate.delete("auth:user:" + username);
    }

    public String getCachedUser(String username) {
        return redisTemplate.opsForValue().get("auth:user:" + username);
    }
}