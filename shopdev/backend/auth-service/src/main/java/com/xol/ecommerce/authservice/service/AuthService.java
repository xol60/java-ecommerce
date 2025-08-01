package com.xol.ecommerce.authservice.service;

import com.xol.ecommerce.authservice.dto.LoginRequest;
import com.xol.ecommerce.authservice.dto.LoginResponse;
import com.xol.ecommerce.authservice.model.RefreshToken;
import com.xol.ecommerce.authservice.model.User;
import com.xol.ecommerce.authservice.repository.RefreshTokenRepository;
import com.xol.ecommerce.authservice.repository.UserRepository;
import com.xol.ecommerce.authservice.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshTokenStr = jwtUtil.generateRefreshToken(user);

        // Save public key to user
        user.setPublicKey(jwtUtil.getEncodedPublicKey());
        userRepository.save(user);

        // Save refresh token
        refreshTokenRepository.deleteByUser(user); // clear old one
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .build();
        refreshTokenRepository.save(refreshToken);

        // Save to Redis
        Map<String, Object> cachedUser = Map.of(
                "username", user.getUsername(),
                "roles", user.getRoles(),
                "publicKey", user.getPublicKey());
        redisService.cacheUser(user.getUsername(), cachedUser, Duration.of(accessTokenExpiration)); 


        return new LoginResponse(accessToken, refreshTokenStr, user.getUsername());

    }
    
    @Transactional
    public LoginResponse refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = refreshToken.getUser();

        // Verify refresh token using stored public key
        if (!jwtUtil.verifyToken(refreshTokenStr, user.getPublicKey())) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // Generate new tokens
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshTokenStr = jwtUtil.generateRefreshToken(user);

        // Update refresh token
        refreshToken.setToken(newRefreshTokenStr);
        refreshTokenRepository.save(refreshToken);

        // Save to Redis
        Map<String, Object> cachedUser = Map.of(
                "username", user.getUsername(),
                "roles", user.getRoles(),
                "publicKey", user.getPublicKey());
        redisService.cacheUser(user.getUsername(), cachedUser, Duration.of(accessTokenExpiration)); 

        // Trả về token

        return new LoginResponse(newAccessToken, newRefreshTokenStr, username);
    }

    @Transactional
    public LoginResponse register(LoginRequest request) {
        // Check if user exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }


        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of("USER"))
                .build();
        user.setPublicKey(jwtUtil.getEncodedPublicKey());


        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .username(user.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role("USER")
                .build();

        boolean isUserCreated = userKafkaClient.sendUserRegistration(event);

        if (!isUserCreated) {
            throw new RuntimeException("User creation failed in user-service");
        }

        // Lưu user local sau khi user-service thành công
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshTokenStr = jwtUtil.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .build();
        refreshTokenRepository.save(refreshToken);

        // Cache
        Map<String, Object> cachedUser = Map.of(
                "username", user.getUsername(),
                "roles", user.getRoles(),
                "publicKey", user.getPublicKey());
        redisService.cacheUser(user.getUsername(), cachedUser, Duration.ofHours(1));

        return new LoginResponse(accessToken, refreshTokenStr, user.getUsername());
    }
}