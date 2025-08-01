package com.xol.ecommerce.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String role;
}