package com.skilllink.TrainerMarketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lombok annotations for clean code
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a constructor with no arguments (required by JPA)
@Entity // Marks this class as a JPA entity, mapped to a database table
@Table(name = "users") // Explicitly names the database table
public class User {

    // Primary Key (ID) configuration
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments the ID
    private Long id;

    // Core identification fields
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // In a real app, this MUST be hashed!

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Stores the enum name (TRAINER or TRAINEE)
    private Role role; // Defines the user's function in the marketplace
}