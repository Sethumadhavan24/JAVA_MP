package com.skilllink.TrainerMarketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin; // New Import
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.skilllink.config", "com.skilllink.TrainerMarketplace"})
@SpringBootApplication
@CrossOrigin(origins = "http://localhost:3000") // CRITICAL: Allows React dev server to talk to Spring
public class TrainerMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainerMarketplaceApplication.class, args);
    }
}