package com.xol.userservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public String getCachedUser(String username) {
        return redisTemplate.opsForValue().get("auth:user:" + username);
    }

    public void cacheUser(String username, String json, long ttlSeconds) {
        redisTemplate.opsForValue().set("auth:user:" + username, json, Duration.ofSeconds(ttlSeconds));
    }

    public void deleteUserCache(String username) {
        redisTemplate.delete("auth:user:" + username);
    }
}