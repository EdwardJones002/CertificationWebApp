package com.user.certifications.controllers;

import com.user.certifications.config.Utility;
import com.user.certifications.entities.User;
import com.user.certifications.repositories.UserCertificationRepository;
import com.user.certifications.repositories.UserFlaggedCertificationRepository;
import com.user.certifications.repositories.UserRepository;
import com.user.certifications.servicesImp.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Transactional
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCertificationRepository userCertificationRepository;

    @Autowired
    private UserFlaggedCertificationRepository userFlaggedCertificationRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
        // Load user details
        User user = userRepository.findByUsername(authentication.getName()).get();
        model.addAttribute("user", user);
        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);

        // Load completed certifications
        model.addAttribute("completedCertifications", userCertificationRepository.findByUserId(user.getId()));

        // Load flagged certifications
        model.addAttribute("flaggedCertifications", userFlaggedCertificationRepository.findByUserId(user.getId()));

        return "profile";
    }

    @PostMapping("/update")
    public String updateUserProfile(Authentication authentication, @RequestParam String name, @RequestParam String email, @RequestParam String password,
                                    RedirectAttributes redirectAttributes) {
        // Update user details
        User user = userRepository.findByUsername(authentication.getName()).get();
        userService.updateUserProfile(user, name, email, password);
        redirectAttributes.addAttribute("registerSuccess", true);
        return "redirect:/profile";
    }
}
