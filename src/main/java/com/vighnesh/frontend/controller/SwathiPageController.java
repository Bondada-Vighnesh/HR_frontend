package com.vighnesh.frontend.controller;

import com.vighnesh.frontend.model.*;
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
public class SwathiPageController {

    private final WebClient http;

    public SwathiPageController(WebClient http) {
        this.http = http;
    }

    // ✅ List all countries + cities
    @GetMapping("/swathi/countries")
    public String countriesPage(Model model) {
        try {
            List<CountryModelSwathi> countries = http.get()
                    .uri("/swathi/countries/with-cities")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CountryModelSwathi>>() {})
                    .block();

            model.addAttribute("countries", countries != null ? countries : Collections.emptyList());
        } catch (WebClientResponseException ex) {
            model.addAttribute("countries", Collections.emptyList());
            model.addAttribute("error", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            model.addAttribute("countries", Collections.emptyList());
            model.addAttribute("error", "Failed to load countries: " + ex.getMessage());
        }
        return "countries-swathi";
    }

    // ✅ Show city details → departments + employees
    @GetMapping("/swathi/city-summary")
    public String citySummaryPage(@RequestParam("city") String cityName, Model model) {
        model.addAttribute("cityName", cityName);

        try {
            LocationModelSwathi location = http.get()
                    .uri("/swathi/employees/city/{cityName}/summary", cityName)
                    .retrieve()
                    .bodyToMono(LocationModelSwathi.class)
                    .block();

            model.addAttribute("location", location);
        } catch (WebClientResponseException ex) {
            model.addAttribute("error", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to load city details: " + ex.getMessage());
        }
        return "city-summary-swathi";
    }
}