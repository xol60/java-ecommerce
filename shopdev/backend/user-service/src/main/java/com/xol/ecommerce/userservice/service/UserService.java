package com.xol.userservice.service;

import com.xol.userservice.dto.UserRequestDTO;
import com.xol.userservice.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    // Get all users
    List<UserResponseDTO> getAllUsers();

    // Get user by ID
    UserResponseDTO getUserById(Long id);

    // Update user by ID
    UserResponseDTO updateUser(Long id, UserRequestDTO dto);
}