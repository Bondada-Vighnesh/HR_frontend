package com.vighnesh.frontend.controller;

import com.vighnesh.frontend.vm.satya.SatyaEmployeeVM;
import com.vighnesh.frontend.vm.satya.SatyaLocationVM;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

@Controller
public class SatyaPageController {

    private final WebClient http;

    public SatyaPageController(WebClient http) {
        this.http = http;
    }

    // ---------------- Locations ----------------
    @GetMapping("/locations-satya")
    public String locations(Model model) {
        try {
            List<SatyaLocationVM> locations = http.get()
                    .uri("/satya/locations")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SatyaLocationVM>>() {})
                    .block();

            model.addAttribute("locations", locations != null ? locations : Collections.emptyList());
        } catch (WebClientResponseException ex) {
            model.addAttribute("locations", Collections.emptyList());
            model.addAttribute("error", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            model.addAttribute("locations", Collections.emptyList());
            model.addAttribute("error", "Failed to load locations: " + ex.getMessage());
        }
        return "locations-satya";
    }

    // ---------------- Employees by Location ----------------
    @GetMapping("/employees-satya")
    public String employees(@RequestParam(required = false) Long locationId, Model model) {
        if (locationId == null) {
            model.addAttribute("employees", Collections.emptyList());
            model.addAttribute("locationId", null);
            model.addAttribute("locationCity", null);
            model.addAttribute("error", "Select a location from Locations page.");
            return "employees-satya";
        }

        try {
            // 1) employees for location
            List<SatyaEmployeeVM> employees = http.get()
                    .uri(uri -> uri.path("/satya/employees/location/{id}").build(locationId))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SatyaEmployeeVM>>() {})
                    .block();

            model.addAttribute("employees", employees != null ? employees : Collections.emptyList());

            // 2) find city name for this location (for the City column header/values)
            List<SatyaLocationVM> locations = http.get()
                    .uri("/satya/locations")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SatyaLocationVM>>() {})
                    .block();

            String city = null;
            if (locations != null) {
                for (SatyaLocationVM loc : locations) {
                    if (locationId.equals(loc.getId())) {
                        city = loc.getCity();
                        break;
                    }
                }
            }
            model.addAttribute("locationCity", city);
            model.addAttribute("locationId", locationId);

        } catch (WebClientResponseException ex) {
            model.addAttribute("employees", Collections.emptyList());
            model.addAttribute("locationCity", null);
            model.addAttribute("locationId", locationId);
            model.addAttribute("error", ex.getStatusCode().value() + " " + ex.getStatusText());
        } catch (Exception ex) {
            model.addAttribute("employees", Collections.emptyList());
            model.addAttribute("locationCity", null);
            model.addAttribute("locationId", locationId);
            model.addAttribute("error", "Failed to load employees: " + ex.getMessage());
        }
        return "employees-satya";
    }
}