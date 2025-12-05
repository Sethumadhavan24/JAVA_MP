package com.skilllink.TrainerMarketplace.controller;

import com.skilllink.TrainerMarketplace.entity.Availability;
import com.skilllink.TrainerMarketplace.entity.Booking;
import com.skilllink.TrainerMarketplace.entity.TrainerProfile; // Added import
import com.skilllink.TrainerMarketplace.repository.TrainerProfileRepository; // Added import
import com.skilllink.TrainerMarketplace.service.BookingService;
import com.skilllink.TrainerMarketplace.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/booking") // Base URL
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final TrainerProfileRepository trainerProfileRepository; // <--- 1. NEW FIELD ADDED

    // --- 2. CONSTRUCTOR UPDATED FOR INJECTION ---
    public BookingController(BookingService bookingService,
                             UserService userService,
                             TrainerProfileRepository trainerProfileRepository) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.trainerProfileRepository = trainerProfileRepository; // <--- 3. FIELD INITIALIZED
    }

    // --- Trainee Side: Get Available Slots (GET /api/booking/trainer/{trainerId}/slots) ---
    @GetMapping("/trainer/{trainerId}/slots")
    public List<Availability> getSlots(@PathVariable Long trainerId) {
        // Assuming bookingService.getAvailableSlots(trainerId) handles finding the TrainerProfile internally
        return bookingService.getAvailableSlots(trainerId);
    }

    // --- Trainee Side: Submit a Booking (POST /api/booking/submit) ---
    @PostMapping("/submit")
    public ResponseEntity<?> submitBooking(
            @RequestParam Long traineeUserId,
            @RequestParam Long slotId) {
        try {
            // Note: BookingConflictException must be accessible (either in a sub-package or top level)
            Booking booking = bookingService.createBooking(slotId, traineeUserId);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);
        } catch (RuntimeException e) { // Catching RuntimeException for simplicity (includes BookingConflictException)
            if (e.getMessage().contains("Conflict")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // HTTP 409
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create booking: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Trainer Side: Add a Slot (POST /api/booking/trainer/{trainerId}/availability) ---
    @PostMapping("/trainer/{trainerId}/availability")
    public ResponseEntity<Availability> addAvailability(
            @PathVariable Long trainerId,
            @RequestBody Availability slot) {

        // Lookup TrainerProfile using the injected repository (REQUIRED)
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Trainer not found")); // Using RuntimeException for simplicity

        Availability savedSlot = bookingService.setTrainerAvailability(trainer, slot);
        return new ResponseEntity<>(savedSlot, HttpStatus.CREATED);
    }
}