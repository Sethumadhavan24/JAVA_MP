package com.skilllink.TrainerMarketplace.service;

import com.skilllink.TrainerMarketplace.entity.Booking;
import com.skilllink.TrainerMarketplace.entity.TraineeProfile;
import com.skilllink.TrainerMarketplace.repository.BookingRepository;
import com.skilllink.TrainerMarketplace.repository.TraineeProfileRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TraineeDashboardService {

    private final TraineeProfileRepository traineeProfileRepository;
    private final BookingRepository bookingRepository;

    public TraineeDashboardService(TraineeProfileRepository traineeProfileRepository, BookingRepository bookingRepository) {
        this.traineeProfileRepository = traineeProfileRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Fetches the Trainee's profile based on the logged-in user's email.
     * @param email The email of the logged-in TRAINEE.
     * @return TraineeProfile
     */
    public TraineeProfile getTraineeProfileByEmail(String email) {
        return traineeProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Trainee profile not found for email: " + email));
    }

    /**
     * Fetches the session history (all bookings) for the trainee.
     * @param traineeProfileId The ID of the trainee profile.
     * @return List of Bookings ordered by session start.
     */
    public List<Booking> getSessionHistory(Long traineeProfileId) {
        return bookingRepository.findByTraineeIdOrderBySessionStartDesc(traineeProfileId);
    }

    /**
     * Calculates total amount spent by the trainee.
     * @param traineeProfileId The ID of the trainee profile.
     * @return Total spent as BigDecimal.
     */
    public BigDecimal getTotalSpent(Long traineeProfileId) {
        List<Booking> bookings = getSessionHistory(traineeProfileId);
        return bookings.stream()
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gets earnings data for graphs (monthly spending).
     * @param traineeProfileId The ID of the trainee profile.
     * @return Map of month-year to total spent.
     */
    public Map<String, BigDecimal> getMonthlySpending(Long traineeProfileId) {
        List<Booking> bookings = getSessionHistory(traineeProfileId);
        return bookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getSessionStart().getMonthValue() + "-" + b.getSessionStart().getYear(),
                        Collectors.mapping(Booking::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }
}
