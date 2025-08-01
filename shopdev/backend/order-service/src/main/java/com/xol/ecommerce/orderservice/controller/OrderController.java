
package com.xol.ecommerce.orderservice.controller;

import com.xol.ecommerce.orderservice.model.Category;
import com.xol.ecommerce.orderservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest request) {
        String result = orderService.placeOrder(request);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{orderId}")
    public OrderResponse getOrderDetails(@PathVariable Long orderId) throws Exception {
        return orderService.getOrderWithProductDetails(orderId);
    }
}