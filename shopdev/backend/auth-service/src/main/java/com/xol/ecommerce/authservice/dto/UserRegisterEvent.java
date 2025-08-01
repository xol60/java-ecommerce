package com.xol.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterEvent {
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private String role;
}