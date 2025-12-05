package com.skilllink.TrainerMarketplace.service;

// Simple custom exception extending RuntimeException
public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
}