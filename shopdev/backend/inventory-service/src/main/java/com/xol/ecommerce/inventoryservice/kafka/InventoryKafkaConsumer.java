package com.xol.ecommerce.inventoryservice.kafka;

import com.xol.ecommerce.inventoryservice.dto.InventoryUpdateRequest;
import com.xol.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryKafkaConsumer {

    private final InventoryService inventoryService;

    /**
     * Listen to 'inventory.update' topic to update actual inventory in database
     */
    @KafkaListener(topics = "inventory.update", groupId = "inventory-group", containerFactory = "inventoryKafkaListenerContainerFactory")
    public void consume(InventoryBatchUpdateRequest batchRequest) {
        inventoryService.updateInventoryBatch(batchRequest.getUpdates());
    }
}