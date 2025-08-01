package com.xol.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterResponseEvent {
    private boolean success;
    private String message;
}