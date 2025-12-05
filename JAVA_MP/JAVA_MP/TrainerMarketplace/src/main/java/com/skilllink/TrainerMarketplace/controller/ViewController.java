package com.skilllink.TrainerMarketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    // 1. Home Page
    // GET /
    @GetMapping("/")
    public String index() {
        return "index"; // Maps to templates/index.html
    }

    // 2. Search Form Page
    // GET /search
    @GetMapping("/search")
    public String searchPage() {
        return "search_form"; // Maps to templates/search_form.html
    }

    // 3. Trainer Dashboard (Placeholder for Maya)
    // GET /trainer/dashboard
    @GetMapping("/trainer/dashboard")
    public String trainerDashboard() {
        return "trainer_dashboard";
    }

    // 4. Booking Page (Next Step)
    // GET /booking/trainer/{trainerId}
    @GetMapping("/booking/trainer/{trainerId}")
    public String bookingPage(@PathVariable Long trainerId) {
        // This is where we would pass the trainerId to the model if needed, but for now, just return the view name
        return "booking_view";
    }
}