package com.xol.ecommerce.authservice.controller;

import com.xol.ecommerce.authservice.dto.LoginRequest;
import com.xol.ecommerce.authservice.dto.LoginResponse;
import com.xol.ecommerce.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @CircuitBreaker(name = "authService", fallbackMethod = "fallbackLogin")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @CircuitBreaker(name = "authService", fallbackMethod = "fallbackLogin")
    public ResponseEntity<TokenResponse> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}