package com.xol.ecommerce.productservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InventoryRedisKafkaFallbackClient {

    private final StringRedisTemplate redisTemplate;
    private final ReplyingKafkaTemplate<String, Long, Integer> replyingKafkaTemplate;

    private final String requestTopic = "inventory-get-stock-request";
    private final String replyTopic = "inventory-get-stock-response"; // topic phải match bên inventory-service
    private final Duration replyTimeout = Duration.ofSeconds(3);

    public Integer getStockForProduct(Long productId) {
        String redisKey = "inventory:" + productId;
        try {
            String stockStr = redisTemplate.opsForValue().get(redisKey);
            if (stockStr != null) {
                return Integer.parseInt(stockStr);
            }
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
        }

        // fallback Kafka
        try {
            ProducerRecord<String, Long> record = new ProducerRecord<>(requestTopic, productId);
            record.headers()
                    .add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes(StandardCharsets.UTF_8)));

            var future = replyingKafkaTemplate.sendAndReceive(record);
            var result = future.get(replyTimeout.toMillis(), TimeUnit.MILLISECONDS);
            return result.value();
        } catch (Exception e) {
            System.err.println("Kafka fallback failed: " + e.getMessage());
            return 0;
        }
    }
}