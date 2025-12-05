package com.skilllink.TrainerMarketplace.controller;

import com.skilllink.TrainerMarketplace.entity.Booking;
import com.skilllink.TrainerMarketplace.entity.TrainerProfile;
import com.skilllink.TrainerMarketplace.service.TrainerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/trainer")
// NOTE: This entire controller is secured via SecurityConfig.java -> .requestMatchers("/api/trainer/**").hasAuthority("TRAINER")
public class TrainerController {

    private final TrainerDashboardService dashboardService;

    public TrainerController(TrainerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Gets the full dashboard data (profile and client history) for the authenticated trainer.
     * GET /api/trainer/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        // 1. Get the logged-in user's email from the security context (Spring Security feature)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String trainerEmail = authentication.getName(); // Email is used as the username

        // 2. Fetch the profile and history
        TrainerProfile profile = dashboardService.getTrainerProfileByEmail(trainerEmail);
        List<Booking> clientHistory = dashboardService.getClientHistory(profile.getId());

        // 3. Simple DTO/Map structure for response
        return ResponseEntity.ok(java.util.Map.of(
                "profile", profile,
                "bookings", clientHistory,
                "totalClients", clientHistory.stream().map(b -> b.getTrainee().getId()).distinct().count()
        ));
    }
}