package com.xol.ecommerce.orderservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class InventoryUpdateProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "inventory.update";

    /**
     * Sends a list of productId and quantity pairs to the inventory update topic.
     * 
     * @param messages list of product inventory updates to process in batch
     */
    public void sendInventoryUpdate(List<InventoryUpdateMessage> messages) {
        kafkaTemplate.send(TOPIC, messages);
    }
}