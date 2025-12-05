package com.skilllink.TrainerMarketplace.repository;

import com.skilllink.TrainerMarketplace.entity.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {

    // Existing search methods
    List<TrainerProfile> findByMainSkill(String mainSkill);
    List<TrainerProfile> findByMainSkillAndIsCertificationVerifiedTrue(String mainSkill);
    List<TrainerProfile> findByMainSkillAndLocation(String mainSkill, String location);
    List<TrainerProfile> findByMainSkillAndLocationAndIsCertificationVerifiedTrue(String mainSkill, String location);

    // NEW: Find profile by the email of the associated User (Used for Security/CRM)
    Optional<TrainerProfile> findByUserEmail(String email);
}