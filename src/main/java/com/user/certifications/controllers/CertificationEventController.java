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

        // Ensure valid month and year
        if (month < 1 || month > 12) {
            month = 1;
        }
        if (year < 2000 || year > 2100) {
            year = 2025;
        }

        // Get events for the selected month and year
        List<CertificationEvents> events = certificationEventService.getEventsForMonth(year, month);

        // Log events to confirm if we're getting any
        System.out.println("Retrieved events: " + events);

        // Group events by month (although we're listing them per month, this step is redundant for now)
        Map<String, List<CertificationEvents>> eventsByMonth = events.stream()
                .collect(Collectors.groupingBy(event -> event.getStartDate().getMonth().name()));

        // Pass events and month data to the view
        model.addAttribute("eventsByMonth", eventsByMonth);
        model.addAttribute("month", month);
        model.addAttribute("year", year);

        return "events";
    }


    // Form to add new certification events
    @GetMapping("/add-event")
    public String showAddEventForm(Model model) {
        model.addAttribute("certificationEvent", new CertificationEvents());
        return "add-event";
    }

    // Handle form submission to add an event
    @PostMapping("/add-event")
    public String addEvent(@ModelAttribute CertificationEvents certificationEvent) {
        // Log the submitted data for debugging
        System.out.println("Form Submitted: " + certificationEvent);

        // Save the event to the database
        certificationEventService.save(certificationEvent);

        // Redirect to a success page or event list page
        return "add-event";
    }
}
