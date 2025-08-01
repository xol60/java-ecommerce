package com.xol.ecommerce.orderservice.repository;

import com.xol.ecommerce.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}