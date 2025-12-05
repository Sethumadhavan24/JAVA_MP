package com.skilllink.TrainerMarketplace.controller;

import com.skilllink.TrainerMarketplace.entity.TrainerProfile;
import com.skilllink.TrainerMarketplace.entity.Skill; // Required for suggestions
import com.skilllink.TrainerMarketplace.service.TrainerService;
import com.skilllink.TrainerMarketplace.service.SkillService; // Required for suggestions
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final TrainerService trainerService;
    private final SkillService skillService; // New Field: SkillService

    // Constructor Injection updated to include SkillService
    public SearchController(TrainerService trainerService, SkillService skillService) {
        this.trainerService = trainerService;
        this.skillService = skillService;
    }

    /**
     * Existing Endpoint for advanced trainer search.
     * Example: GET /api/search/trainers?skill=Yoga&location=Chennai
     */
    @GetMapping("/trainers")
    public List<TrainerProfile> searchTrainers(
            @RequestParam String skill,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "false") boolean verified) {

        return trainerService.findTrainers(skill, location, verified);
    }

    /**
     * NEW Endpoint to get a single trainer profile by ID.
     * Example: GET /api/search/trainers/1
     */
    @GetMapping("/trainers/{id}")
    public TrainerProfile getTrainerById(@PathVariable Long id) {
        return trainerService.getTrainerById(id);
    }

    /**
     * NEW Endpoint for skill auto-suggestion (to fix the 404 error).
     * Example: GET /api/search/skills/suggest?query=yo
     */
    @GetMapping("/skills/suggest")
    public List<Skill> getSkillSuggestions(@RequestParam String query) {
        // Calls the service method to fetch matching skills
        return skillService.getSuggestions(query);
    }
}