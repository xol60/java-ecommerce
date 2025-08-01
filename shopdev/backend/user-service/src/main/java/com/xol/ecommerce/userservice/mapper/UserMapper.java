package com.xol.userservice.mapper;

import com.xol.userservice.dto.UserRequestDTO;
import com.xol.userservice.dto.UserResponseDTO;
import com.xol.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Convert entity to response DTO
    public UserResponseDTO toDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // Convert request DTO to entity
    public User toEntity(UserRequestDTO dto, String hashedPassword) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(hashedPassword);
        user.setRole(dto.getRole());
        user.setActive(true);
        return user;
    }
}