package com.user.certifications.controllers;

import com.user.certifications.config.Utility;
import com.user.certifications.entities.Certification;
import com.user.certifications.entities.User;
import com.user.certifications.entities.UserFlaggedCertification;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.RequestedCertificationRepository;
import com.user.certifications.repositories.UserFlaggedCertificationRepository;
import com.user.certifications.repositories.UserRepository;
import com.user.certifications.servicesImp.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
@Transactional
public class CertificationController {

    @Autowired
    private CertificationRepository certificationRepository;
    @Autowired
    private UserFlaggedCertificationRepository userFlaggedCertificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestedCertificationRepository requestedCertificationRepository;

    private EmailService emailService;

    public CertificationController(
            CertificationRepository certificationRepository,
            UserFlaggedCertificationRepository userFlaggedCertificationRepository,
            UserRepository userRepository,
            RequestedCertificationRepository requestedCertificationRepository,
            EmailService emailService
    ) {
        this.certificationRepository = certificationRepository;
        this.userFlaggedCertificationRepository = userFlaggedCertificationRepository;
        this.userRepository = userRepository;
        this.requestedCertificationRepository = requestedCertificationRepository;
        this.emailService = emailService;
    }

    @GetMapping("/certification/{id}")
    public String viewCertification(Authentication authentication, @PathVariable Long id, Model model) {

        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        Certification certification = certificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid certification ID:" + id));
        model.addAttribute("certification", certification);

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean alreadyFlagged = userFlaggedCertificationRepository.existsByUserAndCertification(user, certification);
        model.addAttribute("alreadyFlagged", alreadyFlagged);
        return "certification";
    }

    @PostMapping("/certification/{id}/flag")
    public String flagCertification(Authentication authentication, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        Certification certification = certificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid certification ID:" + id));
        UserFlaggedCertification flagged = new UserFlaggedCertification();
        User user1 = userRepository.findByUsername(authentication.getName()).get();
        if(userFlaggedCertificationRepository.existsByUserAndCertification(user1,certification)){
            redirectAttributes.addAttribute("error", true);
            return "redirect:/catalogue";
        }
        flagged.setUser(user1);
        flagged.setCertification(certification);
        flagged.setDateFlagged(new Date());
        userFlaggedCertificationRepository.save(flagged);

        // Send email notification
        String toEmail = "ejukesjones@gmail.com";
        String subject = "Certification Flagged by " + user1.getUsername();
        String body = String.format("User %s has flagged the certification: %s", user1.getUsername(), certification.getName());
        emailService.sendCertificationRequestEmail(toEmail, subject, body);

        redirectAttributes.addAttribute("registerSuccess", true);
        return "redirect:/catalogue";
    }

    @PostMapping("/profile/flagged/delete/{id}")
    public String deleteFlaggedCertification(Authentication authentication, @PathVariable Long id, RedirectAttributes redirectAttributes){
        User user2 = userRepository.findByUsername(authentication.getName()).get();
        UserFlaggedCertification flaggedCert = userFlaggedCertificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid flagged certification ID: " + id));

        if(!flaggedCert.getUser().equals(user2)){
            redirectAttributes.addAttribute("error", "You are not authorized to delete this flagged certification.");
            return "redirect:/profile";
        }
        userFlaggedCertificationRepository.delete(flaggedCert);
        redirectAttributes.addAttribute("User Flagged Certification Deleted", true);
        return "redirect:/profile";
    }

    @PostMapping("/profile/flagged/submit/{id}")
    public String submitCertificationForReview(Authentication authentication, @PathVariable Long id, RedirectAttributes redirectAttributes){
        User user3 = userRepository.findByUsername(authentication.getName())
               .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        UserFlaggedCertification flaggedCert1 = userFlaggedCertificationRepository.findById(id)
               .orElseThrow(() -> new IllegalArgumentException("Certification NOT Found"));
        if(!flaggedCert1.getUser().equals(user3)){
            redirectAttributes.addAttribute("error", "You are not authorized to delete this flagged certification.");
            return "redirect:/profile";
        }
        flaggedCert1.setStatus("Submitted");
        userFlaggedCertificationRepository.save(flaggedCert1);

        //Send Email
        String toEmail = "ejukesjones@gmail.com";
        String subject = "Certification Submitted for Approval by " + user3.getUsername();
        String body = String.format("User %s has requested you approve their certification: %s", user3.getUsername(), flaggedCert1.getCertification().getName(), " Please Contact them at: ", user3.getUsername());
        emailService.sendCertificationRequestEmail(toEmail, subject, body);

        redirectAttributes.addAttribute("submitSuccess", true);
        return "redirect:/profile";
    }

    @GetMapping("/admin/add-certification")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddCertificationForm(Authentication authentication, Model model) {
        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        return "add-certification";
    }

    @Transactional
    @PostMapping("/admin/add-certification")
    @PreAuthorize("hasRole('ADMIN')")
    public String addCertification(@RequestParam String name, @RequestParam String examBoard, @RequestParam String skills, @RequestParam String learningTime, @RequestParam String about, @RequestParam String hyperlink, Model model, Authentication authentication) {
        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        if(certificationRepository.existsByHyperlink(hyperlink)){
            model.addAttribute("errorMessage", "Certification already exists!");
            return "add-certification";
        }
        Certification certification = new Certification();
        certification.setName(name);
        certification.setExamBoard(examBoard);
        certification.setSkills(skills);
        certification.setLearningTime(learningTime);
        certification.setAbout(about);
        certification.setHyperlink(hyperlink);
        certification.setDateAdded(new Date());

        certificationRepository.save(certification);

        requestedCertificationRepository.deleteAllByHyperlink(hyperlink);

        model.addAttribute("successMessage", "Certification added successfully!");
        return "add-certification";
    }
}
