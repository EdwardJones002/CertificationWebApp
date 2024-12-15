package com.user.certifications.controllers;
import com.user.certifications.config.Utility;
import com.user.certifications.entities.User;
import com.user.certifications.repositories.UserRepository;
import com.user.certifications.servicesImp.EmailService;
import jakarta.servlet.http.HttpSession;
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

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


@Controller
@Transactional
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        //Ensure User Existings on the DB
        User user = userRepository.findByUsername(email).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Email not found");
            return "redirect:/forgot-password";
        }
        //Generate Temp Password
        String tempPassword = generateTempPassword();
        System.out.println(email + " " + tempPassword);
        //Send User Temp Passwords
        try {
            emailService.sendCertificationRequestEmail(email, "Tempoary Pass", tempPassword);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send password reset email");
            return "redirect:/forgot-password";
        }

        //Set User's Password to Temp
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "Temporary password sent to your email");
        return "redirect:/login";
    }

    private String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }


}
