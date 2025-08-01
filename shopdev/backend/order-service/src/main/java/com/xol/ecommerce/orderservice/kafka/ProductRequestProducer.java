package com.xol.ecommerce.orderservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xol.ecommerce.orderservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class ProductRequestProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // In-memory correlation tracking
    private final Map<String, CompletableFuture<List<ProductDto>>> responseFutures = new ConcurrentHashMap<>();


    public List<ProductDto> fetchProductsByIds(List<Long> productIds) throws Exception {
        String correlationId = UUID.randomUUID().toString();

        String payload = objectMapper.writeValueAsString(productIds);
        ProducerRecord<String, Object> record = new ProducerRecord<>("product.fetch.request", payload);
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)));

        CompletableFuture<List<ProductDto>> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send(record).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                // Optional: Log success
            }

            @Override
            public void onFailure(Throwable ex) {
                future.completeExceptionally(ex);
            }
        });

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            responseFutures.remove(correlationId);
            throw new RuntimeException("Timeout waiting for product response");
        }
    }


    @org.springframework.kafka.annotation.KafkaListener(topics = "product.fetch.response", groupId = "order-service")
    public void handleProductResponse(
            String message,
            @org.springframework.messaging.handler.annotation.Header("correlationId") String correlationId)
            throws Exception {
        List<ProductDto> products = Arrays.asList(
                objectMapper.readValue(message, ProductDto[].class));

        CompletableFuture<List<ProductDto>> future = responseFutures.remove(correlationId);
        if (future != null) {
            future.complete(products);
        }
    }
}