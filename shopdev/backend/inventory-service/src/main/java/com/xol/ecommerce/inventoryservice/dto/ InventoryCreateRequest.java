package com.xol.ecommerce.inventoryservice.dto;

import lombok.Data;

@Data
public class InventoryCreateRequest {
    private Long productId;
    private Integer quantity;
}