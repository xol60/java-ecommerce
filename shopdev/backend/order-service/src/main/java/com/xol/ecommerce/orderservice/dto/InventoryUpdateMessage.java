package com.xol.ecommerce.orderservice.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryUpdateMessage {
    private Long orderId;
    private List<ProductQuantity> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductQuantity {
        private Long productId;
        private Integer quantity;
    }
}