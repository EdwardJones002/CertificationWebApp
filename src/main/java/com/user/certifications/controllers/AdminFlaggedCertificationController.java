package com.user.certifications.controllers;
import com.user.certifications.entities.Certification;
import com.user.certifications.entities.UserCertification;
import com.user.certifications.entities.UserFlaggedCertification;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.UserCertificationRepository;
import com.user.certifications.repositories.UserFlaggedCertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.http.HttpResponse;
import java.util.Date;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Controller
public class AdminFlaggedCertificationController {

    @Autowired
    private UserFlaggedCertificationRepository userFlaggedCertificationRepository;

    @Autowired
    private CertificationRepository certRepository;

    @Autowired
    private UserCertificationRepository userCertRepo;
    @GetMapping("/admin-flagged-certifications")
    public String getAllFlaggedCertificationsGroupedByStatus(Model model) {
        List<UserFlaggedCertification> flaggedCertifications = userFlaggedCertificationRepository.findAll();
        Map<String, List<UserFlaggedCertification>> certificationsByStatus = flaggedCertifications.stream()
                .collect(Collectors.groupingBy(UserFlaggedCertification::getStatus));
        model.addAttribute("certificationsByStatus", certificationsByStatus);
        return "admin-flagged-certifications";
    }

    @PostMapping("/admin-flagged-certification/approve")
    public String approveCert(
        @RequestParam String certName,
        @RequestParam String username){

        UserFlaggedCertification flaggedCert = userFlaggedCertificationRepository.findByCertificationNameAndUserUsername(certName, username);

        if (flaggedCert != null) {
            //Find Cert
            Certification cert = certRepository
                    .findByNameIgnoreCase(certName)
                    .orElse(null);
            if(cert != null) {
                //Generate Cert
                UserCertification userCert = new UserCertification();
                userCert.setUser(flaggedCert.getUser());
                userCert.setCertification(cert);
                userCert.setDateObtained(new Date());
                userCert.setExpiryDate(null);

                //Save Cert
                userCertRepo.save(userCert);

                //Delete from FlaggedRepo
                userFlaggedCertificationRepository.delete(flaggedCert);
                return"redirect:/admin-flagged-certifications";
            }
        }
        return"redirect:/admin-flagged-certifications";
    }
    @PostMapping("/admin-flagged-certifications/deny")
    public String denyCertification(@RequestParam Long id){

        UserFlaggedCertification flaggedCert = userFlaggedCertificationRepository.findById(id).orElse(null);
        if (flaggedCert != null) {
            userFlaggedCertificationRepository.delete(flaggedCert);
            return"redirect:/admin-flagged-certifications";
        }
        return"redirect:/admin-flagged-certifications";
    }
}

