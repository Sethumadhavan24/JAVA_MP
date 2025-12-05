package com.skilllink.TrainerMarketplace.repository;

import com.skilllink.TrainerMarketplace.entity.Availability;
import com.skilllink.TrainerMarketplace.entity.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    // Custom query to find all available slots for a specific trainer within a time range
    List<Availability> findByTrainerAndIsAvailableTrueAndStartTimeBetween(
            TrainerProfile trainer,
            LocalDateTime start,
            LocalDateTime end
    );

    // Find all availability slots for a trainer (for dashboard management)
    List<Availability> findByTrainer(TrainerProfile trainer);
}
