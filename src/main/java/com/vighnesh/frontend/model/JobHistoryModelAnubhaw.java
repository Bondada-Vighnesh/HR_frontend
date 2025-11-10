package com.vighnesh.frontend.model;

import java.time.LocalDate;

public class JobHistoryModelAnubhaw {

    // Embedded ID: employeeId + startDate
    private Id id;
    private LocalDate endDate;

    // Relations
    private JobRef job;           // jobId, jobTitle
    private DeptRef department;   // departmentId, departmentName
    private EmpRef employee;      // firstName, lastName (optional)

    // ----- nested -----
    public static class Id {
        private Long employeeId;
        private LocalDate startDate;

        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    }

    public static class JobRef {
        private String jobId;
        private String jobTitle;

        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    }

    public static class DeptRef {
        private Long id;              // department_id
        private String departmentName;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    }

    public static class EmpRef {
        private String firstName;
        private String lastName;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    // ----- getters/setters -----
    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public JobRef getJob() { return job; }
    public void setJob(JobRef job) { this.job = job; }

    public DeptRef getDepartment() { return department; }
    public void setDepartment(DeptRef department) { this.department = department; }

    public EmpRef getEmployee() { return employee; }
    public void setEmployee(EmpRef employee) { this.employee = employee; }

    // convenience for templates
    public LocalDate getStartDate() { return id != null ? id.getStartDate() : null; }
    public String getJobId() { return job != null ? job.getJobId() : null; }
    public String getJobTitle() { return job != null ? job.getJobTitle() : null; }
    public Long getDepartmentId() { return (department != null) ? department.getId() : null; }
    public String getDepartmentName() { return (department != null) ? department.getDepartmentName() : null; }
    public String getEmployeeFullName() {
        if (employee == null) return null;
        String fn = employee.getFirstName() == null ? "" : employee.getFirstName();
        String ln = employee.getLastName() == null ? "" : employee.getLastName();
        String f = (fn + " " + ln).trim();
        return f.isBlank() ? null : f;
    }
}