package com.skilllink.TrainerMarketplace.repository;

import com.skilllink.TrainerMarketplace.entity.TraineeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TraineeProfileRepository extends JpaRepository<TraineeProfile, Long> {

    // Find profile linked to a specific user ID
    Optional<TraineeProfile> findByUserId(Long userId);

    // Find profile linked to a specific user email
    Optional<TraineeProfile> findByUserEmail(String email);
}
