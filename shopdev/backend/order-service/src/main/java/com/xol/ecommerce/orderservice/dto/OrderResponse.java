package com.xol.ecommerce.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private String status;
    private List<OrderItemWithProductResponse> items;
}