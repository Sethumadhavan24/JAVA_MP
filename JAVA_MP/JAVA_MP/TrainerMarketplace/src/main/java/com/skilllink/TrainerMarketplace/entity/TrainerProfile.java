package com.skilllink.TrainerMarketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal; // Use BigDecimal for currency (hourlyRate)

@Data
@NoArgsConstructor
@Entity
@Table(name = "trainer_profiles")
public class TrainerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relationship to the core User ---
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    // --- Profile Details (Maya's persona) ---
    private String firstName;
    private String lastName;
    private String bio; // Trainer's description of their services

    // Core Business Metrics
    private BigDecimal hourlyRate; // Use BigDecimal for accuracy (Rupees)
    private BigDecimal dailyRate; // Daily rate if applicable
    private String rateType; // "HOUR" or "DAY"
    private String location; // The city (e.g., Chennai, Bengaluru)
    private String videoIntroUrl; // For Vetted Trainer Profiles (Activity 3.1)

    // --- Verification Status (Trust Signals) ---
    @Column(columnDefinition = "boolean default false") // Defaults to false
    private boolean isIdVerified;

    @Column(columnDefinition = "boolean default false")
    private boolean isCertificationVerified;

    // --- Specializations (Linking to skills will be done separately) ---
    private String mainSkill; // e.g., "Yoga", "Piano", "Coding"
}