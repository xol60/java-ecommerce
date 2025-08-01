package com.xol.ecommerce.categoryservice.service;

import com.xol.ecommerce.categoryservice.model.Category;
import com.xol.ecommerce.categoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}