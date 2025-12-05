package com.skilllink.TrainerMarketplace.service;

import com.skilllink.TrainerMarketplace.entity.*;
import com.skilllink.TrainerMarketplace.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime; // Added for clarity

// Placeholder for custom exceptions (Unit I: Exception Handling)
class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) { super(message); }
}

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AvailabilityRepository availabilityRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final TraineeProfileRepository traineeProfileRepository;

    public BookingService(BookingRepository bookingRepository,
                          AvailabilityRepository availabilityRepository,
                          TrainerProfileRepository trainerProfileRepository,
                          TraineeProfileRepository traineeProfileRepository) {
        this.bookingRepository = bookingRepository;
        this.availabilityRepository = availabilityRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.traineeProfileRepository = traineeProfileRepository;
    }

    // --- Trainer Side: Setting Availability ---
    public Availability setTrainerAvailability(TrainerProfile trainer, Availability slot) {
        slot.setTrainer(trainer);
        // Ensure the slot is marked as available
        slot.setAvailable(true); // <-- FIX 1: Corrected setter name
        return availabilityRepository.save(slot);
    }

    public List<Availability> getAvailableSlots(Long trainerId) {
        // Fetch the trainer first
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new BookingConflictException("Trainer not found."));

        // Find all available slots for this trainer (simplistic version for now)
        return availabilityRepository.findByTrainerAndIsAvailableTrueAndStartTimeBetween(
                trainer,
                // Search from now until a long time in the future (replace with actual range later)
                LocalDateTime.now(),
                LocalDateTime.now().plusYears(1)
        );
    }

    // --- Trainee Side: Submitting a Booking (The CORE Transaction) ---
    @Transactional // CRITICAL for ensuring all database steps succeed or fail together
    public Booking createBooking(Long slotId, Long traineeUserId) {

        // 1. Concurrency Check (The Real-Time Engine)
        Availability slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new BookingConflictException("Slot not found."));

        // Lombok generates the getter isAvailable() for a field named isAvailable
        if (!slot.isAvailable()) {
            // This is the race condition check: if two people clicked the same slot
            throw new BookingConflictException("Slot is no longer available. Please choose another time.");
        }

        // 2. Fetch User Profiles and Data
        TraineeProfile trainee = traineeProfileRepository.findByUserId(traineeUserId)
                .orElseThrow(() -> new BookingConflictException("Trainee profile not found."));

        TrainerProfile trainer = slot.getTrainer();

        // 3. Financial Calculation (Activity 7.1)
        BigDecimal rate = "DAY".equals(trainer.getRateType()) ? trainer.getDailyRate() : trainer.getHourlyRate();

        // For simplicity, assume a 1-hour session for hourly, or full day for daily.
        BigDecimal totalAmount = rate;
        BigDecimal commissionRate = new BigDecimal("0.15"); // 15% commission
        BigDecimal commissionFee = totalAmount.multiply(commissionRate);
        BigDecimal trainerPayout = totalAmount.subtract(commissionFee);

        // 4. Update Slot Status (CRITICAL STEP - LOCK)
        // Mark the slot as unavailable immediately within this transaction.
        slot.setAvailable(false); // <-- FIX 2: Corrected setter name
        availabilityRepository.save(slot); // Persist the change

        // 5. Create Booking Record
        Booking booking = new Booking();
        booking.setSlot(slot);
        booking.setTrainer(trainer);
        booking.setTrainee(trainee);

        // FIX 3: Lombok uses the standard getter method, not direct field access
        booking.setSessionStart(slot.getStartTime());
        booking.setSessionEnd(slot.getEndTime());

        booking.setTotalAmount(totalAmount);
        booking.setCommissionFee(commissionFee);
        booking.setTrainerPayout(trainerPayout);
        booking.setStatus("CONFIRMED_PENDING_PAYMENT"); // Payment logic is next step

        return bookingRepository.save(booking);
    }
}