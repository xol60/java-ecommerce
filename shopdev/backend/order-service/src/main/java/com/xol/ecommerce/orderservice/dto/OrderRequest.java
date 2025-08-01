package com.xol.ecommerce.orderservice.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private List<Long> productIds;
    private List<Integer> quantities;
    private String userId;
}