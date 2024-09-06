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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Transactional
public class CatalogueController {

    @Autowired
    private CertificationRepository certificationRepository;

    @GetMapping("/catalogue")
    public String viewCatalogue(Authentication authentication, Model model) {

        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        List<Certification> allCertifications = certificationRepository.findAll();
        model.addAttribute("certifications", allCertifications);
        return "catalogue";
    }

    @GetMapping("/catalogue/search")
    public String searchCatalogue(@RequestParam("query") String query, Model model) {
        List<Certification> searchResults = certificationRepository.findByNameContainingIgnoreCaseOrSkillsContainingIgnoreCase(query, query);
        model.addAttribute("certifications", searchResults);
        return "catalogue";
    }
}
