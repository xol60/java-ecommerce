package com.xol.ecommerce.authservice.repository;

import com.xol.ecommerce.authservice.model.RefreshToken;
import com.xol.ecommerce.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    int deleteByUser(User user);
}