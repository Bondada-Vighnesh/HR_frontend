package com.vighnesh.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SessionController {

    // 🏠 Landing page
    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    // 🔁 Keep old bookmarks working
    @GetMapping("/regions-vighnesh")
    public String regionsVighneshRedirect() {
        return "redirect:/regions";
    }

    @GetMapping("/employees-vighnesh")
    public String employeesVighneshRedirect() {
        return "redirect:/employees";
    }

    // Old generic "locations" → Satya’s locations route
    @GetMapping("/locations")
    public String locationsRedirect() {
        return "redirect:/locations-satya";
    }

    // If you have plain template pages for Anubhaw, keep these:
    @GetMapping("/employees-anubhaw")
    public String employeesAnubhawRedirect() {
        return "redirect:/anubhaw/employees";
    }

    @GetMapping("/job-history-anubhaw")
    public String jobHistoryAnubhawRedirect(@RequestParam(required = false) Long employeeId) {
        if (employeeId == null) return "redirect:/anubhaw/employees";
        return "redirect:/anubhaw/employees/" + employeeId + "/history";
    }
}
