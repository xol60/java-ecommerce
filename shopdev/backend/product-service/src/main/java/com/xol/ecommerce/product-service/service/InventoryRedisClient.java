package com.xol.ecommerce.productservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryRedisClient {

    private final StringRedisTemplate redisTemplate;

    public Integer getStockForProduct(Long productId) {
        String key = "inventory:" + productId;
        String stockStr = redisTemplate.opsForValue().get(key);
        if (stockStr == null) {
            return 0; // default to 0 if no value found
        }
        try {
            return Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            return 0; // or throw exception based on policy
        }
    }
}