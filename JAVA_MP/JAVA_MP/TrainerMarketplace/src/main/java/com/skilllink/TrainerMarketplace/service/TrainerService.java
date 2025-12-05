package com.skilllink.TrainerMarketplace.service;

import com.skilllink.TrainerMarketplace.entity.TrainerProfile;
import com.skilllink.TrainerMarketplace.repository.TrainerProfileRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TrainerService {

    private final TrainerProfileRepository trainerProfileRepository;

    public TrainerService(TrainerProfileRepository trainerProfileRepository) {
        this.trainerProfileRepository = trainerProfileRepository;
    }

    /**
     * Finds trainers based on skill, optional location, and verification status.
     * This method contains the logic to fall back to skill-only search if location is empty.
     */
    public List<TrainerProfile> findTrainers(
            String skill,
            String location,
            boolean requiredVerification) {

        // Determine if location input was provided (even if it's an empty string from the frontend)
        boolean hasLocationInput = location != null && !location.trim().isEmpty();

        if (hasLocationInput) {
            // Case A: Location is specified in the search bar. Use combined query.
            // Note: This requires the database value to exactly match the input.
            if (requiredVerification) {
                return trainerProfileRepository.findByMainSkillAndLocationAndIsCertificationVerifiedTrue(skill, location);
            } else {
                return trainerProfileRepository.findByMainSkillAndLocation(skill, location);
            }
        } else {
            // Case B: Location is NOT specified (the successful test case). Fall back to skill-only query.
            if (requiredVerification) {
                return trainerProfileRepository.findByMainSkillAndIsCertificationVerifiedTrue(skill);
            } else {
                // This is the simplest query, which should return the card if the skill matches.
                return trainerProfileRepository.findByMainSkill(skill);
            }
        }
    }

    /**
     * Gets a single trainer profile by ID.
     */
    public TrainerProfile getTrainerById(Long id) {
        return trainerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
    }
}