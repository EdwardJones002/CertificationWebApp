package com.user.certifications.controllers;

import com.user.certifications.config.Utility;
import com.user.certifications.entities.RequestedCertification;
import com.user.certifications.entities.User;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.RequestedCertificationRepository;
import com.user.certifications.repositories.UserRepository;
import com.user.certifications.servicesImp.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Transactional
public class RequestController {

    @Autowired
    private RequestedCertificationRepository requestedCertificationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CertificationRepository certificationRepository;
    @Autowired
    private EmailService emailService;

    @GetMapping("/request")
    public String requestForm(Authentication authentication, Model model) {
        // Load requested certifications for the logged-in user
        User user = userRepository.findByUsername(authentication.getName()).get();
        List<RequestedCertification> requestedCertifications = requestedCertificationRepository.findByUser(user);
        model.addAttribute("requestedCertifications", requestedCertifications);
        String userRole = Utility.getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        return "request";
    }

    @PostMapping("/request")
    public String submitRequest(Authentication authentication, @RequestParam String name, @RequestParam String examBoard, @RequestParam String skill, @RequestParam String learningTime, @RequestParam String about, @RequestParam String hyperlink, RedirectAttributes redirectAttributes) {
        User user = null;
        RequestedCertification requestedCertification = null;
        try {
            if (certificationRepository.existsByHyperlink(hyperlink)) {
                redirectAttributes.addAttribute("error", true);
                return "redirect:/request";
            }

            requestedCertification = new RequestedCertification();
            requestedCertification.setName(name);
            requestedCertification.setExamBoard(examBoard);
            requestedCertification.setSkills(skill);
            requestedCertification.setLearningTime(learningTime);
            requestedCertification.setAbout(about);
            requestedCertification.setHyperlink(hyperlink);
            user = userRepository.findByUsername(authentication.getName()).get();
            if (requestedCertificationRepository.existsByUserAndHyperlink(user, hyperlink)) {
                redirectAttributes.addAttribute("error", true);
                return "redirect:/request";
            }
            requestedCertification.setUser(user);  // Associate the request with the logged-in user

            // Send email notification
            String toEmail = "ejukesjones@gmail.com";
            String subject = "Certification Requested by " + user.getUsername();
            String body = String.format("User %s has requested a certification to be added:" +
                            "\n\nCertification Name: %s" +
                            "\nExam Board: %s" +
                            "\nSkills: %s" +
                            "\nLearning Time: %s" +
                            "\nAbout: %s" +
                            "\nHyperlink: %s" +
                            "\nPlease consider this request and respond promptly.",
                    user.getUsername(), name, examBoard, skill, learningTime, about, hyperlink
            );

            emailService.sendCertificationRequestEmail(toEmail, subject, body);

            requestedCertificationRepository.save(requestedCertification);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        redirectAttributes.addAttribute("registerSuccess", true);
        return "redirect:/request";
    }
}
