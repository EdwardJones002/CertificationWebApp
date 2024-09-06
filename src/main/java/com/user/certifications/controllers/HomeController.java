package com.user.certifications.controllers;

import com.user.certifications.config.Utility;
import com.user.certifications.entities.Certification;
import com.user.certifications.repositories.CertificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Random;

@Controller
@Transactional
public class HomeController {

    @Autowired
    private CertificationRepository certificationRepository;

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {

        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        // List of latest certifications
        List<Certification> latestCertifications = certificationRepository.findTop5ByOrderByDateAddedDesc();
        model.addAttribute("latestCertifications", latestCertifications);

        // Group by Exam Board
        List<Certification> certificationsByExamBoard = certificationRepository.findAllByOrderByExamBoardAscDateAddedDesc();
        model.addAttribute("certificationsByExamBoard", certificationsByExamBoard);

        // Group by Random Skill
        List<String> skills = certificationRepository.findDistinctSkills();
        String randomSkill = "No Data available";
        if (!skills.isEmpty()) {
            randomSkill = skills.get(new Random().nextInt(skills.size()));
        }
        List<Certification> certificationsBySkill = certificationRepository.findBySkillsOrderByDateAddedDesc(randomSkill);
        model.addAttribute("certificationsBySkill", certificationsBySkill);
        model.addAttribute("randomSkill", randomSkill);

        return "home";
    }
}
