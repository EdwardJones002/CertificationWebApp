package com.user.certifications.controllers;

import com.user.certifications.config.Utility;
import com.user.certifications.entities.User;
import com.user.certifications.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Transactional
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String password, RedirectAttributes redirectAttributes) {
        // Check if user already exists
        if (userRepository.findByUsername(email).isPresent()) {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/register";
        }

        // Create and save the new user
        User user = new User();
        user.setName(name);
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);

        // Redirect to login with success message
        redirectAttributes.addAttribute("registerSuccess", true);
        return "redirect:/login";
    }
}
