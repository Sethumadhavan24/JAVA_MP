package com.skilllink.TrainerMarketplace.service;

import com.skilllink.TrainerMarketplace.entity.Skill;
import com.skilllink.TrainerMarketplace.repository.SkillRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Fetches a list of skills for the auto-suggest feature.
     * @param query The partial name entered by the user.
     * @return A list of matching Skill objects.
     */
    public List<Skill> getSuggestions(String query) {
        // Limit results for performance on a live search
        // We rely on the JpaRepository method to handle the filtering
        return skillRepository.findByNameStartingWithIgnoreCase(query);
    }
}