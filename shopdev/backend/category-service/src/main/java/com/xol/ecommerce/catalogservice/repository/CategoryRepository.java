package com.xol.ecommerce.categoryservice.repository;

import com.xol.ecommerce.categoryservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}