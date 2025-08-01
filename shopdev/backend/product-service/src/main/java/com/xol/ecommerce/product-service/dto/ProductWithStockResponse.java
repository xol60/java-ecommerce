package com.xol.ecommerce.productservice.dto;

import com.xol.ecommerce.productservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductWithStockResponse {
    private Product product;
    private Integer stock;
}