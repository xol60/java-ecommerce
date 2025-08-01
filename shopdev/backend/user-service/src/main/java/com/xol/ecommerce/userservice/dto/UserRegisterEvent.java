package com.xol.userservice.dto;

import lombok.Data;

@Data
public class UserRegisterEvent {
    private String fullName;
    private String email;
    private String phone;
    private String role;
}