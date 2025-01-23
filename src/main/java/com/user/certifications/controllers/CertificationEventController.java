package com.user.certifications.controllers;

import com.user.certifications.entities.CertificationEvents;
import com.user.certifications.servicesImp.CertificationEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CertificationEventController {

    @Autowired
    private CertificationEventService certificationEventService;

    @GetMapping("/events")
    public String showEvents(Model model,
                             @RequestParam(name = "month", defaultValue = "1") int month,
                             @RequestParam(name = "year", defaultValue = "2025") int year) {
        if (month < 1 || month > 12) {
            month = 1;
        }
        if (year < 2000 || year > 2100) {
            year = 2025;
        }
        List<CertificationEvents> events = certificationEventService.getEventsForMonth(year, month);
        Map<String, List<CertificationEvents>> eventsByMonth = events.stream()
                .collect(Collectors.groupingBy(event -> event.getStartDate().getMonth().name()));
        model.addAttribute("eventsByMonth", eventsByMonth);
        model.addAttribute("month", month);
        model.addAttribute("year", year);

        return "events";
    }

    @GetMapping("/add-event")
    public String showAddEventForm(Model model) {
        model.addAttribute("certificationEvent", new CertificationEvents());
        return "add-event";
    }

    @PostMapping("/add-event")
    public String addEvent(@ModelAttribute CertificationEvents certificationEvent) {
        certificationEventService.save(certificationEvent);
        return "add-event";
    }
}
