package com.xol.ecommerce.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
}