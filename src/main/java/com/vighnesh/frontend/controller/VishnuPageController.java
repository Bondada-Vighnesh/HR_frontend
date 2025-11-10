package com.vighnesh.frontend.controller;

import com.vighnesh.frontend.model.EmployeeModelVishnu;
import com.vighnesh.frontend.model.JobModelVishnu;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vishnu")
public class VishnuPageController {

    private final WebClient http;

    public VishnuPageController(WebClient http) {
        this.http = http;
    }

    // --------------------------
    // Jobs page (search/sort/paginate)
    // --------------------------
    @GetMapping("/jobs")
    public String jobsPage(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sort", required = false, defaultValue = "jobTitle") String sort,
            @RequestParam(value = "dir", required = false, defaultValue = "asc") String dir,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            Model model
    ) {
        try {
            List<JobModelVishnu> jobs = http.get()
                    .uri("/api/jobs")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<JobModelVishnu>>() {})
                    .block();

            if (jobs == null) jobs = Collections.emptyList();

            // 1) Filter
            final String term = safeStr(q);
            if (!term.isEmpty()) {
                jobs = jobs.stream()
                        .filter(j ->
                                safeStr(j.getJobId()).contains(term) ||
                                safeStr(j.getJobTitle()).contains(term) ||
                                safeStr(bigToStr(j.getMinSalary())).contains(term) ||
                                safeStr(bigToStr(j.getMaxSalary())).contains(term)
                        )
                        .collect(Collectors.toList());
            }

            // 2) Sort
            Comparator<JobModelVishnu> cmp = jobComparator(sort, dir);
            if (cmp != null) jobs.sort(cmp);

            // 3) Pagination
            PageSlice<JobModelVishnu> slice = paginate(jobs, page, size);

            // Model
            model.addAttribute("jobs", slice.items);
            model.addAttribute("q", q);
            model.addAttribute("sort", sort);
            model.addAttribute("dir", dir);
            model.addAttribute("page", slice.page);
            model.addAttribute("size", slice.size);
            model.addAttribute("totalPages", slice.totalPages);
            model.addAttribute("totalItems", jobs.size());

            return "jobs-vishnu";

        } catch (WebClientResponseException ex) {
            model.addAttribute("error", ex.getResponseBodyAsString());
            setEmptyPage(model);
            return "jobs-vishnu";
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to load jobs: " + ex.getMessage());
            setEmptyPage(model);
            return "jobs-vishnu";
        }
    }

    // --------------------------
    // Employees by job (search/sort/paginate)
    // --------------------------
    @GetMapping("/jobs/{jobId}/employees")
    public String employeesByJob(
            @PathVariable String jobId,
            @RequestParam(value = "jobTitle", required = false) String jobTitle,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sort", required = false, defaultValue = "employeeId") String sort,
            @RequestParam(value = "dir", required = false, defaultValue = "asc") String dir,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            Model model
    ) {
        try {
            List<EmployeeModelVishnu> emps = http.get()
                    .uri("/api/jobs/{jobId}/employees", jobId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EmployeeModelVishnu>>() {})
                    .block();

            if (emps == null) emps = Collections.emptyList();

            // 1) Filter
            final String term = safeStr(q);
            if (!term.isEmpty()) {
                emps = emps.stream()
                        .filter(e ->
                                safeStr(e.getFullName()).contains(term) ||
                                safeStr(e.getEmail()).contains(term) ||
                                safeStr(e.getPhoneNumber()).contains(term) ||
                                safeStr(doubleToStr(e.getSalary())).contains(term)
                        )
                        .collect(Collectors.toList());
            }

            // 2) Sort
            Comparator<EmployeeModelVishnu> cmp = employeeComparator(sort, dir);
            if (cmp != null) emps.sort(cmp);

            // 3) Pagination
            PageSlice<EmployeeModelVishnu> slice = paginate(emps, page, size);

            // If jobTitle not passed, try to fetch for header
            if (jobTitle == null || jobTitle.isBlank()) {
                List<JobModelVishnu> allJobs = http.get()
                        .uri("/api/jobs")
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<JobModelVishnu>>() {})
                        .block();
                if (allJobs != null) {
                    jobTitle = allJobs.stream()
                            .filter(j -> Objects.equals(j.getJobId(), jobId))
                            .map(JobModelVishnu::getJobTitle)
                            .findFirst()
                            .orElse("");
                }
            }

            model.addAttribute("jobId", jobId);
            model.addAttribute("jobTitle", jobTitle);
            model.addAttribute("employees", slice.items);

            model.addAttribute("q", q);
            model.addAttribute("sort", sort);
            model.addAttribute("dir", dir);
            model.addAttribute("page", slice.page);
            model.addAttribute("size", slice.size);
            model.addAttribute("totalPages", slice.totalPages);
            model.addAttribute("totalItems", emps.size());

            return "employees-vishnu";

        } catch (WebClientResponseException ex) {
            model.addAttribute("error", ex.getResponseBodyAsString());
            setEmptyEmpPage(model, jobId, jobTitle);
            return "employees-vishnu";
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to load employees: " + ex.getMessage());
            setEmptyEmpPage(model, jobId, jobTitle);
            return "employees-vishnu";
        }
    }

    // ---------- helpers ----------

    private String safeStr(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    private String bigToStr(BigDecimal bd) {
        return (bd == null) ? null : bd.toPlainString();
    }

    private String doubleToStr(Double d) {
        return (d == null) ? null : String.valueOf(d);
    }

    // ---- comparators (Java 8 safe, explicit generics) ----
    private Comparator<JobModelVishnu> jobComparator(String sort, String dir) {
        Comparator<JobModelVishnu> cmp;

        switch (sort == null ? "" : sort) {
            case "jobId":
                cmp = Comparator.comparing(
                        j -> safeStr(j.getJobId()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "minSalary":
                cmp = Comparator.comparing(
                        JobModelVishnu::getMinSalary,
                        Comparator.nullsLast(Comparator.<BigDecimal>naturalOrder())
                );
                break;

            case "maxSalary":
                cmp = Comparator.comparing(
                        JobModelVishnu::getMaxSalary,
                        Comparator.nullsLast(Comparator.<BigDecimal>naturalOrder())
                );
                break;

            case "jobTitle":
            default:
                cmp = Comparator.comparing(
                        j -> safeStr(j.getJobTitle()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;
        }

        return "desc".equalsIgnoreCase(dir) ? cmp.reversed() : cmp;
    }

    private Comparator<EmployeeModelVishnu> employeeComparator(String sort, String dir) {
        Comparator<EmployeeModelVishnu> cmp;

        switch (sort == null ? "" : sort) {
            case "fullName":
                cmp = Comparator.comparing(
                        e -> safeStr(e.getFullName()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "email":
                cmp = Comparator.comparing(
                        e -> safeStr(e.getEmail()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "phoneNumber":
                cmp = Comparator.comparing(
                        e -> safeStr(e.getPhoneNumber()),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "salary":
                cmp = Comparator.comparing(
                        EmployeeModelVishnu::getSalary,
                        Comparator.nullsLast(Comparator.<Double>naturalOrder())
                );
                break;

            case "employeeId":
            default:
                cmp = Comparator.comparing(
                        EmployeeModelVishnu::getEmployeeId,
                        Comparator.nullsLast(Comparator.<Long>naturalOrder())
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

    private void setEmptyPage(Model model) {
        model.addAttribute("jobs", Collections.emptyList());
        model.addAttribute("q", null);
        model.addAttribute("sort", "jobTitle");
        model.addAttribute("dir", "asc");
        model.addAttribute("page", 1);
        model.addAttribute("size", 10);
        model.addAttribute("totalPages", 1);
        model.addAttribute("totalItems", 0);
    }

    private void setEmptyEmpPage(Model model, String jobId, String jobTitle) {
        model.addAttribute("jobId", jobId);
        model.addAttribute("jobTitle", jobTitle);
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
