package com.xol.productservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCreateRequest {
    private Long productId;
    private Integer quantity;
}