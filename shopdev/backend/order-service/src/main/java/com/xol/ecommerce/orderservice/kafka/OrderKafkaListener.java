package com.xol.ecommerce.orderservice.kafka;

import com.xol.ecommerce.orderservice.entity.Order;
import com.xol.ecommerce.orderservice.entity.OrderItem;
import com.xol.ecommerce.orderservice.message.OrderMessage;
import com.xol.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderKafkaListener {

    private final OrderRepository orderRepository;
    private final InventoryUpdateProducer inventoryUpdateProducer;

    /**
     * Kafka listener that consumes a batch of OrderMessage and stores them as Order
     * entities using saveAll.
     * Improves performance under high traffic.
     */
    @KafkaListener(topics = "order.create", groupId = "order-service", containerFactory = "kafkaListenerContainerFactory" // must
                                                                                                                          // be
                                                                                                                          // batch-enabled
    )
    public void consumeOrdersBatch(@Payload List<OrderMessage> messages, Acknowledgment ack) {
        log.info("📦 Received batch of {} order messages", messages.size());

        List<Order> orders = new ArrayList<>();

        for (OrderMessage message : messages) {
            Order order = Order.builder()
                    .userId(message.getUserId())
                    .status("PENDING")
                    .build();

            List<OrderItem> items = message.getProducts().stream()
                    .map(p -> OrderItem.builder()
                            .productId(p.getProductId())
                            .quantity(p.getQuantity())
                            .order(order)
                            .build())
                    .toList();

            order.setItems(items);
            orders.add(order);
        }

        orderRepository.saveAll(orders); // batch insert
        ack.acknowledge(); // commit Kafka offset manually

        log.info("✅ Batch saved: {} orders", orders.size());
        List<InventoryUpdateMessage> inventoryUpdates = orders.getItems().stream()
                .map(item -> new InventoryUpdateMessage(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        inventoryUpdateProducer.sendInventoryUpdate(inventoryUpdates);
    }
}