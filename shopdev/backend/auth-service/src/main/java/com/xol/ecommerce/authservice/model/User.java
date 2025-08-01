package com.xol.ecommerce.authservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Lob
    @Column(nullable = false)
    private String publicKey; // PEM format string

    // Optionally: created_at, updated_at

    // Getters, setters, constructors
}