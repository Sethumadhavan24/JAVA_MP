package com.skilllink.TrainerMarketplace.repository;

import com.skilllink.TrainerMarketplace.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Existing method signature update to use TrainerProfile's ID instead of User ID
    List<Booking> findByTrainerIdOrderBySessionStartDesc(Long trainerId);

    // New method for trainee bookings
    List<Booking> findByTraineeIdOrderBySessionStartDesc(Long traineeId);

    // We already defined the logic findByTrainerIdOrderBySessionStartDesc (Trainer Profile ID)
    // The implementation should use the TrainerProfile's ID.
}
