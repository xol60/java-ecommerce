package com.xol.ecommerce.productservice.controller;

import com.xol.ecommerce.productservice.dto.ProductRequest;
import com.xol.ecommerce.productservice.dto.ProductWithStockResponse;
import com.xol.ecommerce.productservice.entity.Product;
import com.xol.ecommerce.productservice.service.InventoryRedisKafkaFallbackClient;
import com.xol.ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final InventoryRedisKafkaFallbackClient inventoryClient;

    @PostMapping("/create")
    public Product createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductWithStockResponse> getProductWithStock(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        Integer stock = inventoryClient.getStockForProduct(id);
        return ResponseEntity.ok(new ProductWithStockResponse(product, stock));
    }
}