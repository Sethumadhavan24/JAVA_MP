package com.skilllink.TrainerMarketplace.repository;

import com.skilllink.TrainerMarketplace.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * Finds skills whose names start with the given string, ignoring case.
     * This is highly optimized for auto-suggest (autocomplete).
     */
    List<Skill> findByNameStartingWithIgnoreCase(String name);
}