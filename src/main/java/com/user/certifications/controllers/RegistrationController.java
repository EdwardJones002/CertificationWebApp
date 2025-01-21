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
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private HttpSession session;



    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String password, RedirectAttributes redirectAttributes) {
        // Check if user already exists
        System.out.println("You are in the function");
        if (userRepository.findByUsername(email).isPresent()) {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/register";
        }
        //Send User Verification Code
        //Gernerate One Time Code
        String verificationCode = generateVerificationCode();
        //Save Code
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("email", email);  // Save email as username
        session.setAttribute("name", name);
        session.setAttribute("password", password);
        //Send Email
        try {
            emailService.sendCertificationRequestEmail(email, "Verification Code", verificationCode);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send Verification Code");
            return "redirect:/register";
        }
        System.out.println("You are being redirected to verify page");
        return "redirect:/verify";
    }

    @GetMapping("/verify")
    public String showVerificationPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        String name = (String) session.getAttribute("name");
        String password = (String) session.getAttribute("password");

        // Add them to the model for Thymeleaf rendering
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        model.addAttribute("password", password);
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyUser (@RequestParam String email, @RequestParam String verificationCode, @RequestParam String name, @RequestParam String password, RedirectAttributes redirectAttributes){

        session.setAttribute("email", email);
        session.setAttribute("name", name);
        session.setAttribute("password", password);
        //Validate Verification Code
        String storedCode = (String) session.getAttribute("verificationCode");
        if (storedCode ==null || !storedCode.equals(verificationCode)) {
            redirectAttributes.addFlashAttribute("error", "Invalid Code");
            return "redirect:/verify?email=" + email;
        }
        session.removeAttribute("verificationCode");

        //Create and save the new user
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


    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
