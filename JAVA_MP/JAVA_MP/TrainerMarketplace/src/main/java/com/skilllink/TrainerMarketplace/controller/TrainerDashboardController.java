package com.skilllink.TrainerMarketplace.controller;

import com.skilllink.TrainerMarketplace.entity.Booking;
import com.skilllink.TrainerMarketplace.entity.TrainerProfile;
import com.skilllink.TrainerMarketplace.entity.Availability;
import com.skilllink.TrainerMarketplace.service.TrainerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainer/dashboard")
@PreAuthorize("hasAuthority('TRAINER')")
public class TrainerDashboardController {

    private final TrainerDashboardService trainerDashboardService;

    public TrainerDashboardController(TrainerDashboardService trainerDashboardService) {
        this.trainerDashboardService = trainerDashboardService;
    }

    /**
     * Gets the trainer's profile information.
     * @param authentication The authenticated user.
     * @return TrainerProfile
     */
    @GetMapping("/profile")
    public ResponseEntity<TrainerProfile> getProfile(Authentication authentication) {
        String email = authentication.getName();
        TrainerProfile profile = trainerDashboardService.getTrainerProfileByEmail(email);
        return ResponseEntity.ok(profile);
    }

    /**
     * Gets the trainer's client history (all bookings).
     * @param authentication The authenticated user.
     * @return List of Bookings
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getBookings(Authentication authentication) {
        String email = authentication.getName();
        TrainerProfile profile = trainerDashboardService.getTrainerProfileByEmail(email);
        List<Booking> bookings = trainerDashboardService.getClientHistory(profile.getId());
        return ResponseEntity.ok(bookings);
    }

    /**
     * Gets the current month earnings.
     * @param authentication The authenticated user.
     * @return Current month earnings as BigDecimal
     */
    @GetMapping("/earnings/current-month")
    public ResponseEntity<BigDecimal> getCurrentMonthEarnings(Authentication authentication) {
        String email = authentication.getName();
        TrainerProfile profile = trainerDashboardService.getTrainerProfileByEmail(email);
        BigDecimal earnings = trainerDashboardService.getCurrentMonthEarnings(profile.getId());
        return ResponseEntity.ok(earnings);
    }

    /**
     * Gets the total earnings.
     * @param authentication The authenticated user.
     * @return Total earnings as BigDecimal
     */
    @GetMapping("/earnings/total")
    public ResponseEntity<BigDecimal> getTotalEarnings(Authentication authentication) {
        String email = authentication.getName();
        TrainerProfile profile = trainerDashboardService.getTrainerProfileByEmail(email);
        BigDecimal earnings = trainerDashboardService.getTotalEarnings(profile.getId());
        return ResponseEntity.ok(earnings);
    }

    /**
     * Gets monthly earnings data for graphs.
     * @param authentication The authenticated user.
     * @return Map of month-year to total earnings
     */
    @GetMapping("/earnings/monthly")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlyEarnings(Authentication authentication) {
        String email = authentication.getName();
        TrainerProfile profile = trainerDashboardService.getTrainerProfileByEmail(email);
        Map<String, BigDecimal> monthlyEarnings = trainerDashboardService.getMonthlyEarnings(profile.getId());
        return ResponseEntity.ok(monthlyEarnings);
    }

    /**
     * Gets the trainer's availability slots.
     * @param authentication The authenticated user.
     * @return List of Availability slots
     */
    @GetMapping("/availability")
    public ResponseEntity<List<Availability>> getAvailability(Authentication authentication) {
        String email = authentication.getName();
        TrainerProfile profile = trainerDashboardService.getTrainerProfileByEmail(email);
        List<Availability> availability = trainerDashboardService.getAvailability(profile.getId());
        return ResponseEntity.ok(availability);
    }

    /**
     * Deletes an availability slot.
     * @param authentication The authenticated user.
     * @param availabilityId The ID of the availability slot to delete.
     * @return ResponseEntity
     */
    @DeleteMapping("/availability/{availabilityId}")
    public ResponseEntity<?> deleteAvailability(Authentication authentication, @PathVariable Long availabilityId) {
        String email = authentication.getName();
        TrainerProfile profile = trainerDashboardService.getTrainerProfileByEmail(email);
        trainerDashboardService.deleteAvailability(profile.getId(), availabilityId);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the trainer's profile (rate information).
     * @param authentication The authenticated user.
     * @param updateData The data to update (hourlyRate, dailyRate, rateType).
     * @return Updated TrainerProfile
     */
    @PutMapping("/profile")
    public ResponseEntity<TrainerProfile> updateProfile(Authentication authentication, @RequestBody Map<String, Object> updateData) {
        String email = authentication.getName();
        TrainerProfile profile = trainerDashboardService.getTrainerProfileByEmail(email);

        BigDecimal hourlyRate = updateData.containsKey("hourlyRate") ? new BigDecimal(updateData.get("hourlyRate").toString()) : profile.getHourlyRate();
        BigDecimal dailyRate = updateData.containsKey("dailyRate") ? new BigDecimal(updateData.get("dailyRate").toString()) : profile.getDailyRate();
        String rateType = updateData.containsKey("rateType") ? updateData.get("rateType").toString() : profile.getRateType();

        TrainerProfile updatedProfile = trainerDashboardService.updateTrainerProfile(profile.getId(), hourlyRate, dailyRate, rateType);
        return ResponseEntity.ok(updatedProfile);
    }
}
