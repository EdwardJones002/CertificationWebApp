package com.user.certifications.servicesImp;

import com.user.certifications.entities.CertificationEvents;
import com.user.certifications.repositories.CertificationEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class CertificationEventService {

    @Autowired
    private CertificationEventRepository certificationEventRepository;

    // Fetch events within a given month and year
    public List<CertificationEvents> getEventsForMonth(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        return certificationEventRepository.findByStartDateBetween(startOfMonth, endOfMonth);
    }

    // You could also create other methods to add or update events as needed
    // Method to save CertificationEvents
    public void save(CertificationEvents certificationEvent) {
        certificationEventRepository.save(certificationEvent);
    }
}
