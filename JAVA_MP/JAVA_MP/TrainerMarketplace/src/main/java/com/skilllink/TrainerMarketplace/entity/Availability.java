package com.skilllink.TrainerMarketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "availability")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the trainer
    @ManyToOne(fetch = FetchType.EAGER) // Many availability slots belong to one Trainer
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerProfile trainer;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(columnDefinition = "boolean default true") // True if available, False if booked
    private boolean isAvailable = true;

    // Note: In a real app, this should sync with Google Calendar (API Integration - Unit V)
}