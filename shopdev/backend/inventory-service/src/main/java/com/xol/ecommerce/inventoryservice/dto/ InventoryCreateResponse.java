package com.xol.ecommerce.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryCreateResponse {
    private boolean success;
    private String message;
}