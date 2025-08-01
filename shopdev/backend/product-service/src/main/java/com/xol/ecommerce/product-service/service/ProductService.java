package com.xol.ecommerce.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xol.ecommerce.productservice.dto.InventoryCreateRequest;
import com.xol.ecommerce.productservice.dto.InventoryCreateResponse;
import com.xol.ecommerce.productservice.dto.ProductCreateRequest;
import com.xol.ecommerce.productservice.model.Product;
import com.xol.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;

    private final Map<String, CompletableFuture<InventoryCreateResponse>> responseFutures = new ConcurrentHashMap<>();


    public Product createProduct(ProductCreateRequest request) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        InventoryCreateRequest inventoryRequest = new InventoryCreateRequest(null, request.getInitialStock());
        String payload = objectMapper.writeValueAsString(inventoryRequest);

        ProducerRecord<String, Object> record = new ProducerRecord<>("inventory-create-request", payload);
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)));

        CompletableFuture<InventoryCreateResponse> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send(record);
        InventoryCreateResponse response = future.get(5, TimeUnit.SECONDS);

        if (!response.isSuccess()) {
            throw new IllegalStateException("Inventory creation failed: " + response.getMessage());
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        return productRepository.save(product);
    }

    @KafkaListener(topics = "inventory-create-response", groupId = "product-service")
    public void handleInventoryResponse(String message,
            @org.springframework.messaging.handler.annotation.Header("correlationId") String correlationId)
            throws Exception {
        InventoryCreateResponse response = objectMapper.readValue(message, InventoryCreateResponse.class);
        CompletableFuture<InventoryCreateResponse> future = responseFutures.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @KafkaListener(topics = "product-fetch-request", groupId = "product-service", containerFactory = "batchFactory")
    public void handleProductFetch(List<String> messages,
            @org.springframework.messaging.handler.annotation.Header("correlationId") String correlationId)
            throws Exception {

        // Parse danh sách ID
        List<Long> ids = new ArrayList<>();
        for (String msg : messages) {
            ProductFetchRequest req = objectMapper.readValue(msg, ProductFetchRequest.class);
            ids.addAll(req.getProductIds());
        }

        List<Product> products = productRepository.findAllById(ids);
        List<ProductDto> productDtos = products.stream().map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice()))
                .toList();

        ProductFetchResponse response = new ProductFetchResponse(productDtos);
        String payload = objectMapper.writeValueAsString(response);

        ProducerRecord<String, Object> record = new ProducerRecord<>("product-fetch-response", payload);
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)));
        kafkaTemplate.send(record);
    }
}