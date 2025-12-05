package com.skilllink.TrainerMarketplace.service;

/**
 * Data Transfer Object for user login response.
 * Returns the mock token, the user's role, and user ID.
 */
public record AuthResponse(String token, String role, Long userId) {
}
