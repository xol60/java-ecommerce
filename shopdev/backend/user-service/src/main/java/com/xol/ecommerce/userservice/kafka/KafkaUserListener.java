package com.xol.userservice.kafka;

import com.xol.userservice.dto.UserRegisterEvent;
import com.xol.userservice.dto.UserRegisterResponseEvent;
import com.xol.userservice.entity.User;
import com.xol.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@KafkaListener(topics = "user-register")
public class KafkaUserListener {

    private final UserRepository userRepository;

    @KafkaHandler
    @SendTo("user-register-reply")
    public UserRegisterResponseEvent handleRegister(UserRegisterEvent event) {
        if (userRepository.existsByEmail(event.getEmail())) {
            return new UserRegisterResponseEvent(false, "Email already exists");
        }

        User user = User.builder()
                .email(event.getEmail())
                .phone(event.getPhone())
                .fullName(event.getFullName())
                .role(event.getRole())
                .active(true)
                .build();

        userRepository.save(user);

        return new UserRegisterResponseEvent(true, "User created successfully");
    }
}