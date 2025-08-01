package com.xol.productservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCreateResponse {
    private boolean success;
    private String message;
}