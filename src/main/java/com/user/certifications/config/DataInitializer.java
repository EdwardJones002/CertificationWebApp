package com.user.certifications.config;

import com.user.certifications.entities.User;
import com.user.certifications.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.findByUsername("admin@gmail.com").isEmpty()) {
            User user = new User();
            user.setName("Admin");
            user.setUsername("admin@gmail.com");
            user.setPassword(passwordEncoder.encode("abcd1234"));
            user.setRole("ADMIN");
            userRepository.save(user);
            System.out.println("Initialized user Admin with email mansoor@gmail.com and encoded password.");
        } else {
            System.out.println("User with email admin@gmail.com already exists.");
        }
    }
}
