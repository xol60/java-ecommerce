package com.xol.ecommerce.orderservice.kafka;

import com.xol.ecommerce.orderservice.message.OrderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderMessage> kafkaTemplate;

    public void sendOrderMessage(OrderMessage message) {
        kafkaTemplate.send("order.create", message);
    }
}