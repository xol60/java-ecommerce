package com.xol.ecommerce.categoryservice.controller;

import com.xol.ecommerce.categoryservice.model.Category;
import com.xol.ecommerce.categoryservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }
}