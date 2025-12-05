package com.skilllink.TrainerMarketplace.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "trainee_profiles")
public class TraineeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relationship to the core User ---
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    // --- Trainee Details (Alex's persona) ---
    private String firstName;
    private String lastName;

    // --- Learning Goals (Crucial for search/matching) ---
    private String currentGoal; // e.g., "Learn Piano Basics," "Public Speaking Confidence"
    private String learningStyle; // e.g., "Flexible Evenings," "Weekend Intensive"
}