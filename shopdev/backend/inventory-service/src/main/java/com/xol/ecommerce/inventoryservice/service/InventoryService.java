package com.xol.ecommerce.inventoryservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xol.ecommerce.inventoryservice.dto.InventoryCreateRequest;
import com.xol.ecommerce.inventoryservice.dto.InventoryCreateResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-create-request", groupId = "inventory-service")
    public void handleInventoryCreateRequest(String message,
            @Header("correlationId") String correlationId) {
        try {
            InventoryCreateRequest request = objectMapper.readValue(message, InventoryCreateRequest.class);

            if (request.getProductId() == null || request.getQuantity() == null || request.getQuantity() < 0) {
                sendResponse(correlationId, false, "Invalid inventory request");
                return;
            }

            String key = "product:" + request.getProductId();
            redisTemplate.opsForValue().set(key, String.valueOf(request.getQuantity()));

            sendResponse(correlationId, true, "Inventory created");
        } catch (Exception e) {
            sendResponse(correlationId, false, "Error: " + e.getMessage());
        }
    }

    private void sendResponse(String correlationId, boolean success, String message) {
        try {
            InventoryCreateResponse response = new InventoryCreateResponse(success, message);
            String responseJson = objectMapper.writeValueAsString(response);

            ProducerRecord<String, String> record = new ProducerRecord<>("inventory-create-response", responseJson);
            record.headers().add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)));

            kafkaTemplate.send(record);
        } catch (Exception ignored) {
        }
    }

    public Integer getStockByProductId(Long productId) {
        String key = "inventory:" + productId;

        // Try get from Redis
        try {
            String cachedStock = redisTemplate.opsForValue().get(key);
            if (cachedStock != null) {
                return Integer.parseInt(cachedStock);
            }
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
        }

        // Fallback to DB
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (inventory == null) {
            throw new RuntimeException("Inventory not found for productId = " + productId);
        }

        // Cache back to Redis
        try {
            redisTemplate.opsForValue().set(
                    key,
                    String.valueOf(inventory.getStock()),
                    Duration.ofMinutes(5));
        } catch (Exception e) {
            System.err.println("Redis cache write failed: " + e.getMessage());
        }

        return inventory.getStock();
    }
    
    @KafkaListener(topics = "inventory-get-stock-request", groupId = "inventory-service")
    public void handleGetStockRequest(String message,
            @Header("correlationId") String correlationId) {
        try {
            InventoryGetStockRequest request = objectMapper.readValue(message, InventoryGetStockRequest.class);
            Inventory inventory = inventoryRepository.findByProductId(request.getProductId());

            InventoryGetStockResponse response;
            if (inventory == null) {
                response = new InventoryGetStockResponse(request.getProductId(), null, false, "Not found");
            } else {
                String key = "inventory:" + request.getProductId();
                redisTemplate.opsForValue().set(key, String.valueOf(inventory.getStock()), Duration.ofMinutes(5));

                response = new InventoryGetStockResponse(
                        request.getProductId(),
                        inventory.getStock(),
                        true,
                        "Found in DB");
            }

            String responseJson = objectMapper.writeValueAsString(response);
            ProducerRecord<String, String> record = new ProducerRecord<>("inventory-get-stock-response", responseJson);
            record.headers().add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)));

            kafkaTemplate.send(record);
        } catch (Exception e) {

            try {
                InventoryGetStockResponse response = new InventoryGetStockResponse(null, null, false,
                        "Error: " + e.getMessage());
                String responseJson = objectMapper.writeValueAsString(response);

                ProducerRecord<String, String> record = new ProducerRecord<>("inventory-get-stock-response",
                        responseJson);
                record.headers().add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)));

                kafkaTemplate.send(record);
            } catch (Exception ignored) {
            }
        }
    }
    
    
    public void updateInventoryBatch(List<InventoryUpdateRequest> updates) {
        for (InventoryUpdateRequest update : updates) {
            Inventory inventory = inventoryRepository.findByProductId(update.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found: " + update.getProductId()));

            if (inventory.getQuantity() < update.getQuantity()) {
                throw new RuntimeException("Not enough inventory for product " + update.getProductId());
            }

            inventory.setQuantity(inventory.getQuantity() - update.getQuantity());
        }

        inventoryRepository.saveAll(
                updates.stream()
                        .map(update -> inventoryRepository.findByProductId(update.getProductId()).get())
                        .toList());
    }
}