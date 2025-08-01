package com.xol.ecommerce.orderservice.message;

import com.xol.ecommerce.orderservice.dto.ProductOrder;
import lombok.Data;

import java.util.List;

@Data
public class OrderMessage {
    private String userId;
    private List<ProductOrder> products;
}