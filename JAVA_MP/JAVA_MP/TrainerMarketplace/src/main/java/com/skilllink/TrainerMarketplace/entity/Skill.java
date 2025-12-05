package com.skilllink.TrainerMarketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The name of the skill (e.g., "Yoga", "Python Coding", "Public Speaking")
    @Column(unique = true, nullable = false)
    private String name;

    // Optional category (e.g., "Fitness", "Tech", "Arts")
    private String category;
}