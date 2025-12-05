package com.skilllink.TrainerMarketplace.service;

import com.skilllink.TrainerMarketplace.entity.User;
import com.skilllink.TrainerMarketplace.entity.Role;
import com.skilllink.TrainerMarketplace.entity.TrainerProfile;
import com.skilllink.TrainerMarketplace.entity.TraineeProfile;
import com.skilllink.TrainerMarketplace.repository.UserRepository;
import com.skilllink.TrainerMarketplace.repository.TrainerProfileRepository;
import com.skilllink.TrainerMarketplace.repository.TraineeProfileRepository;
import com.skilllink.TrainerMarketplace.dto.UserRegistrationDTO;
import org.springframework.security.crypto.password.PasswordEncoder; // New Import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final PasswordEncoder passwordEncoder; // New Field

    public UserService(UserRepository userRepository,
                       TrainerProfileRepository trainerProfileRepository,
                       TraineeProfileRepository traineeProfileRepository,
                       PasswordEncoder passwordEncoder) { // New parameter
        this.userRepository = userRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.traineeProfileRepository = traineeProfileRepository;
        this.passwordEncoder = passwordEncoder; // Initialize PasswordEncoder
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User registerNewUser(UserRegistrationDTO registrationDTO) {

        if (findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new RegistrationException("User with this email already exists.");
        }

        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        // --- CRITICAL CHANGE: HASH THE PASSWORD ---
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        // ------------------------------------------
        user.setRole(registrationDTO.getRole());
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.TRAINER) {
            TrainerProfile profile = new TrainerProfile();
            profile.setUser(savedUser);
            profile.setFirstName(registrationDTO.getFirstName());
            profile.setLastName(registrationDTO.getLastName());
            profile.setMainSkill(registrationDTO.getMainSkill());
            profile.setHourlyRate(new BigDecimal("1000.00"));
            trainerProfileRepository.save(profile);

        } else if (savedUser.getRole() == Role.TRAINEE) {
            TraineeProfile profile = new TraineeProfile();
            profile.setUser(savedUser);
            profile.setFirstName(registrationDTO.getFirstName());
            profile.setLastName(registrationDTO.getLastName());
            traineeProfileRepository.save(profile);
        }

        return savedUser;
    }
}