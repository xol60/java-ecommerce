package com.xol.ecommerce.inventoryservice.dto;

import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryGetStockRequest {
    private Long productId;
}