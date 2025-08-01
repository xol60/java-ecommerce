package com.xol.ecommerce.inventoryservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryGetStockResponse {
    private Long productId;
    private Integer stock;
    private boolean success;
    private String message;
}