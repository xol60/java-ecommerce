package com.xol.ecommerce.orderservice.service;

import com.xol.ecommerce.orderservice.dto.OrderRequest;
import com.xol.ecommerce.orderservice.dto.ProductOrder;
import com.xol.ecommerce.orderservice.kafka.OrderProducer;
import com.xol.ecommerce.orderservice.message.OrderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;


import com.xol.ecommerce.orderservice.kafka.ProductRequestProducer;
import com.xol.ecommerce.orderservice.repository.OrderRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final StringRedisTemplate redisTemplate;
    private final OrderProducer orderProducer;
    private final ProductRequestProducer productRequestProducer;

    // Lua script ensures atomic stock check and update
    private static final String LUA_SCRIPT = """
                for i = 1, #KEYS do
                  local stock = tonumber(redis.call("GET", KEYS[i]))
                  local qty = tonumber(ARGV[i])
                  if not stock or stock < qty then
                    return 0
                  end
                end
                for i = 1, #KEYS do
                  redis.call("DECRBY", KEYS[i], ARGV[i])
                end
                return 1
            """;

    public boolean placeOrder(OrderRequest request) {
        List<String> keys = new ArrayList<>();
        List<String> args = new ArrayList<>();

        // Prepare Redis keys and values
        for (ProductOrder po : request.getProducts()) {
            keys.add("inventory:" + po.getProductId());
            args.add(String.valueOf(po.getQuantity()));
        }

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, keys, args.toArray());

        // Return immediately after Redis stock update
        if (result == null || result == 0L) {
            return false; // Out of stock
        }

        // Send message to Kafka for DB processing
        OrderMessage message = new OrderMessage();
        message.setUserId(request.getUserId());
        message.setProducts(request.getProducts());
        orderProducer.sendOrderMessage(message);
        
        return true;
    }

    public OrderResponse getOrderWithProductDetails(Long orderId) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        List<Long> productIds = order.getItems().stream()
                .map(OrderItem::getProductId)
                .distinct()
                .toList();

        List<ProductDto> products = productRequestProducer.fetchProductsByIds(productIds);

        Map<Long, ProductDto> productMap = products.stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

        List<OrderItemWithProductResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    ProductDto product = productMap.get(item.getProductId());
                    if (product == null) {
                        throw new RuntimeException("Missing product info for productId: " + item.getProductId());
                    }
                    return new OrderItemWithProductResponse(product, item.getQuantity());
                })
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                itemResponses);
    }
}