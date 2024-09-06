package com.user.certifications.controllers;

import com.user.certifications.config.Utility;
import com.user.certifications.entities.Certification;
import com.user.certifications.entities.User;
import com.user.certifications.entities.UserFlaggedCertification;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.RequestedCertificationRepository;
import com.user.certifications.repositories.UserFlaggedCertificationRepository;
import com.user.certifications.repositories.UserRepository;
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

    @GetMapping("/certification/{id}")
    public String viewCertification(Authentication authentication, @PathVariable Long id, Model model) {

        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        Certification certification = certificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid certification ID:" + id));
        model.addAttribute("certification", certification);
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
        redirectAttributes.addAttribute("registerSuccess", true);
        return "redirect:/catalogue";
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
