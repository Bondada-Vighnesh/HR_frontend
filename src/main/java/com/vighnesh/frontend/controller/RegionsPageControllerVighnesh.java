package com.vighnesh.frontend.controller;

import com.vighnesh.frontend.model.RegionModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Collections;
import java.util.List;

@Controller
public class RegionsPageControllerVighnesh {

    private final WebClient http;

    public RegionsPageControllerVighnesh(WebClient http) {
        this.http = http;
    }

    @GetMapping("/regions")
    public String regions(Model model) {
        try {
            List<RegionModel> regions =
                    http.get()
                            .uri("/vighnesh/regions/with-countries")
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<List<RegionModel>>() {})
                            .block();

            model.addAttribute("regions", regions != null ? regions : Collections.emptyList());
        } catch (WebClientResponseException ex) {
            model.addAttribute("regions", Collections.emptyList());
            model.addAttribute("error", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            model.addAttribute("regions", Collections.emptyList());
            model.addAttribute("error", "Failed to load regions: " + ex.getMessage());
        }
        return "regions";
    }
}