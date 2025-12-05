package com.skilllink.TrainerMarketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints (was "/api/**")
                .allowedOrigins("http://localhost:3000") // Allow React application origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow necessary HTTP methods
                .allowedHeaders("*") // Allow all headers
                .exposedHeaders("Authorization") // Expose auth header if you use it
                .allowCredentials(true) // Allow cookies/credentials
                .maxAge(3600); // Cache preflight for 1 hour
    }
}