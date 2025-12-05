package com.skilllink.TrainerMarketplace.repository;

import com.skilllink.TrainerMarketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<[Entity Class], [Primary Key Type]>
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom Query Method (Spring automatically writes the SQL for this based on the method name)
    Optional<User> findByEmail(String email);

    // Optional is used because the user might not be found.
}