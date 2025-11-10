package com.vighnesh.frontend.controller;

import com.vighnesh.frontend.model.EmployeeModelAnubhaw;
import com.vighnesh.frontend.model.JobHistoryModelAnubhaw;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/anubhaw")
public class AnubhawPageController {

    private final WebClient http;

    public AnubhawPageController(WebClient http) {
        this.http = http;
    }

    // ==============================
    // 1️⃣ EMPLOYEES LIST PAGE
    // ==============================
    @GetMapping("/employees")
    public String employeesPage(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sort", required = false, defaultValue = "employeeId") String sort,
            @RequestParam(value = "dir", required = false, defaultValue = "asc") String dir,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            Model model
    ) {
        try {
            List<EmployeeModelAnubhaw> list = http.get()
                    .uri("/anubhaw/employees")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EmployeeModelAnubhaw>>() {})
                    .block();

            if (list == null) list = Collections.emptyList();

            // Filter
            String term = safe(q);
            if (!term.isEmpty()) {
                String t = term;
                list = list.stream().filter(e ->
                        safe(e.getFullName()).contains(t) ||
                        safe(e.getEmail()).contains(t) ||
                        safe(e.getPhoneNumber()).contains(t) ||
                        safe(e.getJobId()).contains(t) ||
                        safe(e.getJobTitle()).contains(t)
                ).collect(Collectors.toList());
            }

            // Sort
            Comparator<EmployeeModelAnubhaw> cmp = employeeComparator(sort, dir);
            list.sort(cmp);

            // Pagination
            PageSlice<EmployeeModelAnubhaw> slice = paginate(list, page, size);

            model.addAttribute("employees", slice.items);
            model.addAttribute("q", q);
            model.addAttribute("sort", sort);
            model.addAttribute("dir", dir);
            model.addAttribute("page", slice.page);
            model.addAttribute("size", slice.size);
            model.addAttribute("totalPages", slice.totalPages);
            model.addAttribute("totalItems", list.size());

            return "employees-anubhaw";

        } catch (WebClientResponseException ex) {
            setEmptyEmpPage(model);
            model.addAttribute("error", ex.getResponseBodyAsString());
            return "employees-anubhaw";
        } catch (Exception ex) {
            setEmptyEmpPage(model);
            model.addAttribute("error", "Failed to load employees: " + ex.getMessage());
            return "employees-anubhaw";
        }
    }

    // ==============================
    // 2️⃣ JOB HISTORY PAGE
    // ==============================
    @GetMapping("/employees/{employeeId}/history")
    public String jobHistoryByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sort", required = false, defaultValue = "startDate") String sort,
            @RequestParam(value = "dir", required = false, defaultValue = "asc") String dir,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            Model model
    ) {
        try {
            List<JobHistoryModelAnubhaw> items = http.get()
                    .uri("/anubhaw/job-history/employee/{id}", employeeId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<JobHistoryModelAnubhaw>>() {})
                    .block();

            if (items == null) items = Collections.emptyList();

            // Filter
            String term = safe(q);
            if (!term.isEmpty()) {
                String t = term;
                items = items.stream().filter(jh ->
                        safe(jh.getJobTitle()).contains(t) ||
                        safe(jh.getJobId()).contains(t) ||
                        safe(jh.getDepartmentName()).contains(t) ||
                        safe(String.valueOf(jh.getDepartmentId())).contains(t) ||
                        safe(String.valueOf(jh.getStartDate())).contains(t) ||
                        safe(String.valueOf(jh.getEndDate())).contains(t)
                ).collect(Collectors.toList());
            }

            // Sort
            Comparator<JobHistoryModelAnubhaw> cmp = jobHistoryComparator(sort, dir);
            items.sort(cmp);

            // Pagination
            PageSlice<JobHistoryModelAnubhaw> slice = paginate(items, page, size);

            // Header
            String empHeader = items.stream()
                    .map(JobHistoryModelAnubhaw::getEmployeeFullName)
                    .filter(s -> s != null && !s.isBlank())
                    .findFirst()
                    .orElse("Employee " + employeeId);

            model.addAttribute("employeeId", employeeId);
            model.addAttribute("employeeName", empHeader);
            model.addAttribute("history", slice.items);
            model.addAttribute("q", q);
            model.addAttribute("sort", sort);
            model.addAttribute("dir", dir);
            model.addAttribute("page", slice.page);
            model.addAttribute("size", slice.size);
            model.addAttribute("totalPages", slice.totalPages);
            model.addAttribute("totalItems", items.size());

            return "jobhistory-anubhaw";

        } catch (WebClientResponseException ex) {
            setEmptyHistPage(model, employeeId);
            model.addAttribute("error", ex.getResponseBodyAsString());
            return "jobhistory-anubhaw";
        } catch (Exception ex) {
            setEmptyHistPage(model, employeeId);
            model.addAttribute("error", "Failed to load job history: " + ex.getMessage());
            return "jobhistory-anubhaw";
        }
    }

    // ==============================
    // Helper Functions
    // ==============================
    private String safe(String s) {
        return (s == null) ? "" : s.toLowerCase(Locale.ROOT);
    }

    private Comparator<EmployeeModelAnubhaw> employeeComparator(String sort, String dir) {
        Comparator<EmployeeModelAnubhaw> cmp;
        switch (sort) {
            case "fullName" -> cmp = Comparator.comparing(
                    e -> Optional.ofNullable(e.getFullName()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "email" -> cmp = Comparator.comparing(
                    e -> Optional.ofNullable(e.getEmail()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "phoneNumber" -> cmp = Comparator.comparing(
                    e -> Optional.ofNullable(e.getPhoneNumber()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "jobTitle" -> cmp = Comparator.comparing(
                    e -> Optional.ofNullable(e.getJobTitle()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "jobId" -> cmp = Comparator.comparing(
                    e -> Optional.ofNullable(e.getJobId()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
            );
            default -> cmp = Comparator.comparing(
                    e -> Optional.ofNullable(e.getEmployeeId()).orElse(Long.MAX_VALUE)
            );
        }
        return "desc".equalsIgnoreCase(dir) ? cmp.reversed() : cmp;
    }

    private Comparator<JobHistoryModelAnubhaw> jobHistoryComparator(String sort, String dir) {
        Comparator<JobHistoryModelAnubhaw> cmp;
        switch (sort) {
            case "endDate" -> cmp = Comparator.comparing(
                    jh -> Optional.ofNullable(jh.getEndDate()).orElse(LocalDate.MIN)
            );
            case "jobTitle" -> cmp = Comparator.comparing(
                    jh -> Optional.ofNullable(jh.getJobTitle()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "jobId" -> cmp = Comparator.comparing(
                    jh -> Optional.ofNullable(jh.getJobId()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "departmentName" -> cmp = Comparator.comparing(
                    jh -> Optional.ofNullable(jh.getDepartmentName()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "departmentId" -> cmp = Comparator.comparing(
                    jh -> Optional.ofNullable(jh.getDepartmentId()).orElse(Long.MAX_VALUE)
            );
            default -> cmp = Comparator.comparing(
                    jh -> Optional.ofNullable(jh.getStartDate()).orElse(LocalDate.MIN)
            );
        }
        return "desc".equalsIgnoreCase(dir) ? cmp.reversed() : cmp;
    }

    private static class PageSlice<T> {
        final List<T> items;
        final int page;
        final int size;
        final int totalPages;

        PageSlice(List<T> items, int page, int size, int totalPages) {
            this.items = items;
            this.page = page;
            this.size = size;
            this.totalPages = totalPages;
        }
    }

    private <T> PageSlice<T> paginate(List<T> all, int page, int size) {
        int p = Math.max(1, page);
        int s = Math.max(1, size);
        int total = all.size();
        int totalPages = (int) Math.ceil(total / (double) s);
        if (totalPages == 0) totalPages = 1;
        if (p > totalPages) p = totalPages;

        int from = (p - 1) * s;
        int to = Math.min(from + s, total);
        List<T> slice = (from < to) ? all.subList(from, to) : Collections.emptyList();

        return new PageSlice<>(slice, p, s, totalPages);
    }

    private void setEmptyEmpPage(Model model) {
        model.addAttribute("employees", Collections.emptyList());
        model.addAttribute("q", null);
        model.addAttribute("sort", "employeeId");
        model.addAttribute("dir", "asc");
        model.addAttribute("page", 1);
        model.addAttribute("size", 10);
        model.addAttribute("totalPages", 1);
        model.addAttribute("totalItems", 0);
    }

    private void setEmptyHistPage(Model model, Long employeeId) {
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", "Employee " + employeeId);
        model.addAttribute("history", Collections.emptyList());
        model.addAttribute("q", null);
        model.addAttribute("sort", "startDate");
        model.addAttribute("dir", "asc");
        model.addAttribute("page", 1);
        model.addAttribute("size", 10);
        model.addAttribute("totalPages", 1);
        model.addAttribute("totalItems", 0);
    }
}
