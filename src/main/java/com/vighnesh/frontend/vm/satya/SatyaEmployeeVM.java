package com.vighnesh.frontend.vm.satya;

import java.math.BigDecimal;

public class SatyaEmployeeVM {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;        // Satya's API sometimes returns username (e.g. JWHALEN)
    private String phoneNumber;
    private BigDecimal salary;   // adapt type if your API returns Integer
    private JobVM job;           // for jobTitle

    public static class JobVM {
        private String jobTitle;
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public JobVM getJob() { return job; }
    public void setJob(JobVM job) { this.job = job; }

    // convenience
    public String getFullName() {
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();
        return (fn + " " + ln).trim();
    }
}