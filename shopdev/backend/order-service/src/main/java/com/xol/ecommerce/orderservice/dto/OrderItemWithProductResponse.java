package com.xol.ecommerce.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemWithProductResponse {
    private ProductDto product;
    private Integer quantity;
}