package com.skilllink.TrainerMarketplace.service;

import com.skilllink.TrainerMarketplace.entity.Booking;
import com.skilllink.TrainerMarketplace.entity.TrainerProfile;
import com.skilllink.TrainerMarketplace.entity.Availability;
import com.skilllink.TrainerMarketplace.repository.BookingRepository;
import com.skilllink.TrainerMarketplace.repository.TrainerProfileRepository;
import com.skilllink.TrainerMarketplace.repository.AvailabilityRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainerDashboardService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilityRepository availabilityRepository;

    public TrainerDashboardService(TrainerProfileRepository trainerProfileRepository,
                                   BookingRepository bookingRepository,
                                   AvailabilityRepository availabilityRepository) {
        this.trainerProfileRepository = trainerProfileRepository;
        this.bookingRepository = bookingRepository;
        this.availabilityRepository = availabilityRepository;
    }

    /**
     * Fetches the Trainer's profile based on the logged-in user's email.
     * @param email The email of the logged-in TRAINER.
     * @return TrainerProfile
     */
    public TrainerProfile getTrainerProfileByEmail(String email) {
        // Since the user is authenticated, we expect the profile to exist.
        return trainerProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Trainer profile not found for email: " + email));
    }

    /**
     * Fetches the client history (all bookings) for the trainer. (CRM Feature)
     * @param trainerProfileId The ID of the trainer profile.
     * @return List of Bookings ordered by session start.
     */
    public List<Booking> getClientHistory(Long trainerProfileId) {
        // Uses the custom JpaRepository method defined earlier
        return bookingRepository.findByTrainerIdOrderBySessionStartDesc(trainerProfileId);
    }

    /**
     * Calculates earnings for the current month.
     * @param trainerProfileId The ID of the trainer profile.
     * @return Total earnings for current month as BigDecimal.
     */
    public BigDecimal getCurrentMonthEarnings(Long trainerProfileId) {
        YearMonth currentMonth = YearMonth.now();
        List<Booking> bookings = getClientHistory(trainerProfileId);
        return bookings.stream()
                .filter(b -> YearMonth.from(b.getSessionStart()).equals(currentMonth))
                .map(Booking::getTrainerPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates total earnings for the trainer.
     * @param trainerProfileId The ID of the trainer profile.
     * @return Total earnings as BigDecimal.
     */
    public BigDecimal getTotalEarnings(Long trainerProfileId) {
        List<Booking> bookings = getClientHistory(trainerProfileId);
        return bookings.stream()
                .map(Booking::getTrainerPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gets earnings data for graphs (monthly earnings).
     * @param trainerProfileId The ID of the trainer profile.
     * @return Map of month-year to total earnings.
     */
    public Map<String, BigDecimal> getMonthlyEarnings(Long trainerProfileId) {
        List<Booking> bookings = getClientHistory(trainerProfileId);
        return bookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getSessionStart().getMonthValue() + "-" + b.getSessionStart().getYear(),
                        Collectors.mapping(Booking::getTrainerPayout, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    /**
     * Updates the trainer's profile with new rate information.
     * @param trainerProfileId The ID of the trainer profile.
     * @param hourlyRate The new hourly rate.
     * @param dailyRate The new daily rate.
     * @param rateType The rate type ("HOUR" or "DAY").
     * @return Updated TrainerProfile.
     */
    public TrainerProfile updateTrainerProfile(Long trainerProfileId, BigDecimal hourlyRate, BigDecimal dailyRate, String rateType) {
        TrainerProfile profile = trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() -> new RuntimeException("Trainer profile not found"));
        profile.setHourlyRate(hourlyRate);
        profile.setDailyRate(dailyRate);
        profile.setRateType(rateType);
        return trainerProfileRepository.save(profile);
    }

    /**
     * Gets all availability slots for the trainer.
     * @param trainerProfileId The ID of the trainer profile.
     * @return List of Availability slots.
     */
    public List<Availability> getAvailability(Long trainerProfileId) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() -> new RuntimeException("Trainer profile not found"));
        return availabilityRepository.findByTrainer(trainer);
    }

    /**
     * Deletes an availability slot.
     * @param trainerProfileId The ID of the trainer profile.
     * @param availabilityId The ID of the availability slot to delete.
     */
    public void deleteAvailability(Long trainerProfileId, Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        // Ensure the availability belongs to the trainer
        if (!availability.getTrainer().getId().equals(trainerProfileId)) {
            throw new RuntimeException("Availability slot does not belong to this trainer");
        }

        availabilityRepository.delete(availability);
    }
}
