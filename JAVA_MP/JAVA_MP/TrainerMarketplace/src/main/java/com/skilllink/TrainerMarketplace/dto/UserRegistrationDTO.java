package com.skilllink.TrainerMarketplace.dto;

import com.skilllink.TrainerMarketplace.entity.Role;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data // Lombok for getters/setters
public class UserRegistrationDTO {

    // Core User fields
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required (TRAINER/TRAINEE)")
    private Role role;

    // Profile initialization fields
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    // Additional field for Trainer persona
    private String mainSkill;
}