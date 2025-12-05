package com.skilllink.TrainerMarketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The Trainer (Maya)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerProfile trainer;

    // The Trainee (Alex)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private TraineeProfile trainee;

    // The specific time slot
    @OneToOne
    @JoinColumn(name = "availability_id", nullable = false, unique = true)
    private Availability slot;

    // Booking Details
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;

    // Financial Details
    private BigDecimal totalAmount;
    private BigDecimal commissionFee; // The 15% marketplace fee (Activity 7.1)
    private BigDecimal trainerPayout;

    // Status tracking
    @Column(nullable = false)
    private String status = "PENDING_PAYMENT"; // e.g., CONFIRMED, CANCELLED, COMPLETED

    private String paymentTransactionId; // The ID from the payment gateway
}