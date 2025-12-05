package com.skilllink.TrainerMarketplace.service;

import com.skilllink.TrainerMarketplace.config.JwtUtil;
import com.skilllink.TrainerMarketplace.entity.User;
import com.skilllink.TrainerMarketplace.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates the user credentials and returns a JWT token and role.
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // 1. Attempt to authenticate user using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        // 2. Set the authenticated user in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Retrieve the full User object to get the Role
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.email());

        // --- DEFENSIVE CHECK ---
        if (userOptional.isEmpty()) {
            throw new RegistrationException("Login failed: User record corrupted or database issue.");
        }

        // 4. Generate JWT token with email and role
        String email = loginRequest.email();
        String role = userOptional.get().getRole().name();
        String token = jwtUtil.generateToken(email, role);
        Long userId = userOptional.get().getId();

        return new AuthResponse(token, role, userId);
    }
}
