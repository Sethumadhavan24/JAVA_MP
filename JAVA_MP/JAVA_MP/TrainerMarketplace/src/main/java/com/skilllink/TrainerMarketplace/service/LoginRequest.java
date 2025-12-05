package com.skilllink.TrainerMarketplace.service;

/**
 * Data Transfer Object for user login request.
 * Record syntax is concise for simple data containers (Unit I concept).
 */
public record LoginRequest(String email, String password) {
}