package com.skilllink.TrainerMarketplace.controller;

import com.skilllink.TrainerMarketplace.entity.Booking;
import com.skilllink.TrainerMarketplace.entity.TraineeProfile;
import com.skilllink.TrainerMarketplace.service.TraineeDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainee/dashboard")
@PreAuthorize("hasAuthority('TRAINEE')")
public class TraineeDashboardController {

    private final TraineeDashboardService traineeDashboardService;

    public TraineeDashboardController(TraineeDashboardService traineeDashboardService) {
        this.traineeDashboardService = traineeDashboardService;
    }

    /**
     * Gets the trainee's profile information.
     * @param authentication The authenticated user.
     * @return TraineeProfile
     */
    @GetMapping("/profile")
    public ResponseEntity<TraineeProfile> getProfile(Authentication authentication) {
        String email = authentication.getName();
        TraineeProfile profile = traineeDashboardService.getTraineeProfileByEmail(email);
        return ResponseEntity.ok(profile);
    }

    /**
     * Gets the trainee's session history (upcoming and past bookings).
     * @param authentication The authenticated user.
     * @return List of Bookings
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<Booking>> getSessionHistory(Authentication authentication) {
        String email = authentication.getName();
        TraineeProfile profile = traineeDashboardService.getTraineeProfileByEmail(email);
        List<Booking> sessions = traineeDashboardService.getSessionHistory(profile.getId());
        return ResponseEntity.ok(sessions);
    }

    /**
     * Gets the total amount spent by the trainee.
     * @param authentication The authenticated user.
     * @return Total spent as BigDecimal
     */
    @GetMapping("/total-spent")
    public ResponseEntity<BigDecimal> getTotalSpent(Authentication authentication) {
        String email = authentication.getName();
        TraineeProfile profile = traineeDashboardService.getTraineeProfileByEmail(email);
        BigDecimal totalSpent = traineeDashboardService.getTotalSpent(profile.getId());
        return ResponseEntity.ok(totalSpent);
    }

    /**
     * Gets monthly spending data for graphs.
     * @param authentication The authenticated user.
     * @return Map of month-year to total spent
     */
    @GetMapping("/monthly-spending")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlySpending(Authentication authentication) {
        String email = authentication.getName();
        TraineeProfile profile = traineeDashboardService.getTraineeProfileByEmail(email);
        Map<String, BigDecimal> monthlySpending = traineeDashboardService.getMonthlySpending(profile.getId());
        return ResponseEntity.ok(monthlySpending);
    }
}
