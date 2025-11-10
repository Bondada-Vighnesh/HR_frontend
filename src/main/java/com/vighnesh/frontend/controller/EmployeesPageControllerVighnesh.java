package com.vighnesh.frontend.controller;

import com.vighnesh.frontend.model.EmployeeSummaryModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Collections;
import java.util.List;

@Controller
public class EmployeesPageControllerVighnesh {

    private final WebClient http;

    public EmployeesPageControllerVighnesh(WebClient http) {
        this.http = http;
    }

    // /employees?country=Germany
    @GetMapping("/employees")
    public String employees(@RequestParam(required = false) String country, Model model) {
        model.addAttribute("country", country);

        if (country == null || country.isBlank()) {
            model.addAttribute("employees", Collections.emptyList());
            model.addAttribute("error", "Select a country from Regions page.");
            return "employees";
        }

        try {
            List<EmployeeSummaryModel> employees =
                    http.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/vighnesh/employees/country/{country}/summary")
                                    .build(country.trim()))
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<List<EmployeeSummaryModel>>() {})
                            .block();

            model.addAttribute("employees", employees != null ? employees : Collections.emptyList());
        } catch (WebClientResponseException ex) {
            // surface your backend’s single-line message body
            model.addAttribute("employees", Collections.emptyList());
            model.addAttribute("error", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            model.addAttribute("employees", Collections.emptyList());
            model.addAttribute("error", "Failed to load employees: " + ex.getMessage());
        }
        return "employees";
    }
}