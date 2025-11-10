package com.vighnesh.frontend.controller;

import com.vighnesh.frontend.model.DepartmentModelAkhila;
import com.vighnesh.frontend.model.EmployeeModelAkhila;
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
public class AkhilaPageController {

    private final WebClient http;

    public AkhilaPageController(WebClient http) {
        this.http = http;
    }

    // List departments
    @GetMapping("/akhila/departments")
    public String departmentsPage(Model model) {
        try {
            List<DepartmentModelAkhila> departments = http.get()
                    .uri("/akhila/departments")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<DepartmentModelAkhila>>() {})
                    .block();

            model.addAttribute("departments", departments != null ? departments : Collections.emptyList());
        } catch (WebClientResponseException ex) {
            model.addAttribute("departments", Collections.emptyList());
            model.addAttribute("error", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            model.addAttribute("departments", Collections.emptyList());
            model.addAttribute("error", "Failed to load departments: " + ex.getMessage());
        }
        return "departments-akhila";
    }

    // Employees by department (expects query params: /akhila/employees?deptId=..&deptName=..)
    @GetMapping("/akhila/employees")
    public String employeesByDept(@RequestParam("deptId") Long deptId,
                                  @RequestParam(value = "deptName", required = false) String deptName,
                                  Model model) {

        model.addAttribute("deptId", deptId);
        model.addAttribute("deptName", deptName);

        try {
            List<EmployeeModelAkhila> employees = http.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/akhila/employees")
                            .queryParam("departmentId", deptId) // server does the filtering
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EmployeeModelAkhila>>() {})
                    .block();

            model.addAttribute("employees", employees != null ? employees : Collections.emptyList());
        } catch (WebClientResponseException ex) {
            model.addAttribute("employees", Collections.emptyList());
            model.addAttribute("error", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            model.addAttribute("employees", Collections.emptyList());
            model.addAttribute("error", "Failed to load employees: " + ex.getMessage());
        }

        return "employees-akhila";
    }
}