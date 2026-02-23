package com.example.mantenimiento.config;

import com.example.mantenimiento.adapter.outgoing.persistence.UserRepository;
import com.example.mantenimiento.adapter.outgoing.persistence.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public DataInitializer(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists, if not create it
        if (userRepository.findByUsername("admin").isEmpty()) {
            UUID id = UUID.randomUUID();
            String hashed = passwordEncoder.encode("password");
            UserEntity admin = new UserEntity(id, "admin", hashed, "ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("Admin user created");
        } else {
            System.out.println("Admin user already exists");
        }
    }
}
