package com.xol.ecommerce.inventoryservice.controller;

import com.xol.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public Integer getStockByProductId(@PathVariable Long productId) {
        return inventoryService.getStockByProductId(productId);
    }
}