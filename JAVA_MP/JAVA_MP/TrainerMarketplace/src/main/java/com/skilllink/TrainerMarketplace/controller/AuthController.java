package com.skilllink.TrainerMarketplace.controller;

import com.skilllink.TrainerMarketplace.dto.UserRegistrationDTO;
import com.skilllink.TrainerMarketplace.service.UserService;
import com.skilllink.TrainerMarketplace.service.RegistrationException;
import com.skilllink.TrainerMarketplace.service.AuthService; // New Import
import com.skilllink.TrainerMarketplace.service.LoginRequest; // New Import
import com.skilllink.TrainerMarketplace.service.AuthResponse; // New Import
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException; // New Import
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService; // New Field

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // Endpoint for user registration (POST /api/auth/register)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            userService.registerNewUser(registrationDTO);
            return new ResponseEntity<>("User registered successfully. Role: " + registrationDTO.getRole() +
                    ". Name: " + registrationDTO.getFirstName() + " " + registrationDTO.getLastName(),
                    HttpStatus.CREATED);
        } catch (RegistrationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * NEW Endpoint for user login (POST /api/auth/login).
     * Returns a token and role on success.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.authenticateUser(loginRequest);
            // On success, return the mock token and role to the frontend
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            // Catches bad credentials (wrong email or password)
            return new ResponseEntity<>("Invalid username or password.", HttpStatus.UNAUTHORIZED); // 401
        }
    }
}