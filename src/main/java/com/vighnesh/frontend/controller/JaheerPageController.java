package com.vighnesh.frontend.controller;

import com.vighnesh.frontend.model.DepartmentModelJaheer;
import com.vighnesh.frontend.model.EmployeeModelJaheer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/jaheer")
public class JaheerPageController {

    private final WebClient http;

    public JaheerPageController(WebClient http) {
        this.http = http;
    }

    // --------------------------
    // Employees page (search/sort/paginate)
    // --------------------------
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
            List<EmployeeModelJaheer> all = http.get()
                    .uri("/jaheer/employees")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EmployeeModelJaheer>>() {})
                    .block();

            if (all == null) all = Collections.emptyList();

            // 1) Filter
            String term = (q == null) ? "" : q.trim().toLowerCase(Locale.ROOT);
            if (!term.isEmpty()) {
                final String t = term;
                all = all.stream()
                        .filter(e ->
                                safe(e.getFullName()).contains(t) ||
                                safe(e.getEmail()).contains(t) ||
                                safe(e.getPhoneNumber()).contains(t) ||
                                safe(e.getJobId()).contains(t) ||
                                String.valueOf(Optional.ofNullable(e.getEmployeeId()).orElse(0L)).contains(t)
                        )
                        .collect(Collectors.toList());
            }

            // 2) Sort
            Comparator<EmployeeModelJaheer> cmp = employeeComparator(sort, dir);
            if (cmp != null) all.sort(cmp);

            // 3) Pagination
            PageSlice<EmployeeModelJaheer> slice = paginate(all, page, size);

            // model
            model.addAttribute("employees", slice.items);
            model.addAttribute("q", q);
            model.addAttribute("sort", sort);
            model.addAttribute("dir", dir);
            model.addAttribute("page", slice.page);
            model.addAttribute("size", slice.size);
            model.addAttribute("totalPages", slice.totalPages);
            model.addAttribute("totalItems", all.size());

            return "employees-jaheer";

        } catch (WebClientResponseException ex) {
            setEmptyEmpPage(model);
            model.addAttribute("error", ex.getResponseBodyAsString());
            return "employees-jaheer";
        } catch (Exception ex) {
            setEmptyEmpPage(model);
            model.addAttribute("error", "Failed to load employees: " + ex.getMessage());
            return "employees-jaheer";
        }
    }

    // --------------------------
    // Department by employee
    // --------------------------
    @GetMapping("/employees/{employeeId}/department")
    public String departmentByEmployee(
            @PathVariable Long employeeId,
            Model model
    ) {
        try {
            DepartmentModelJaheer dept = http.get()
                    .uri("/jaheer/departments/by-employee/{id}", employeeId)
                    .retrieve()
                    .bodyToMono(DepartmentModelJaheer.class)
                    .block();

            model.addAttribute("employeeId", employeeId);
            model.addAttribute("department", dept);

            return "department-jaheer";

        } catch (WebClientResponseException ex) {
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("department", null);
            model.addAttribute("error", ex.getResponseBodyAsString());
            return "department-jaheer";
        } catch (Exception ex) {
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("department", null);
            model.addAttribute("error", "Failed to load department: " + ex.getMessage());
            return "department-jaheer";
        }
    }

    // ---------- helpers ----------

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    // ✅ FIXED: type-safe comparators
    private Comparator<EmployeeModelJaheer> employeeComparator(String sort, String dir) {
        Comparator<EmployeeModelJaheer> cmp;

        switch (sort == null ? "" : sort) {
            case "fullName":
                cmp = Comparator.comparing(
                        e -> safe(e.getFullName()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "email":
                cmp = Comparator.comparing(
                        e -> safe(e.getEmail()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "phoneNumber":
                cmp = Comparator.comparing(
                        e -> safe(e.getPhoneNumber()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "jobId":
                cmp = Comparator.comparing(
                        e -> safe(e.getJobId()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "employeeId":
            default:
                cmp = Comparator.comparing(
                        EmployeeModelJaheer::getEmployeeId,
                        Comparator.nullsLast(Comparator.naturalOrder())
                );
                break;
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
}
