package com.user.certifications.controllers;

import com.user.certifications.config.Utility;
import com.user.certifications.entities.Certification;
import com.user.certifications.entities.User;
import com.user.certifications.entities.UserCertification;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.UserCertificationRepository;
import com.user.certifications.repositories.UserFlaggedCertificationRepository;
import com.user.certifications.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Optional;

@Controller
@Transactional
public class UserCertificationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private UserCertificationRepository userCertificationRepository;

    @Autowired
    private UserFlaggedCertificationRepository userFlaggedCertificationRepository;

    @GetMapping("/admin/add-user-certification")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddUserCertificationForm(Authentication authentication, Model model) {
        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        return "add-user-certification";
    }

    @PostMapping("/admin/add-user-certification")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String addUserCertification(@RequestParam Long userId, @RequestParam String username, @RequestParam Long certificationId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateObtained, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateExpired, Model model, Authentication authentication) {
        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        Optional<User> userOptional = userRepository.findByIdAndUsername(userId, username);
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User not found with the provided ID and Username.");
            return "add-user-certification";
        }
        User user = userOptional.get();

        Optional<Certification> certificationOptional = certificationRepository.findById(certificationId);
        if (certificationOptional.isEmpty()) {
            model.addAttribute("errorMessage", "Certification not found with the provided ID.");
            return "add-user-certification";
        }

        if (userCertificationRepository.existsByUserAndCertification(user, certificationOptional.get())) {
            model.addAttribute("errorMessage", "User is already registered in the course.");
            return "add-user-certification";
        }

        Certification certification = certificationOptional.get();

        UserCertification userCertification = new UserCertification();
        userCertification.setUser(user);
        userCertification.setCertification(certification);
        userCertification.setDateObtained(dateObtained);
        userCertification.setExpiryDate(dateExpired);
        userCertificationRepository.save(userCertification);
        userFlaggedCertificationRepository.deleteAllByUserAndCertification(user, certification);
        model.addAttribute("successMessage", "User certification added successfully!");
        return "add-user-certification";
    }
}
