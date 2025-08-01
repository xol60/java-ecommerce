package com.xol.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterResponseEvent {
    private boolean success;
    private String message;
}