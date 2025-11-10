package com.vighnesh.frontend.model;
public class EmployeeModelAnubhaw {

    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    // From Employee: jobId + relation job(jobId, jobTitle)
    private String jobId;
    private JobRef job;

    // ----- nested -----
    public static class JobRef {
        private String jobId;
        private String jobTitle;

        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    }

    // ----- getters/setters -----
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

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public JobRef getJob() { return job; }
    public void setJob(JobRef job) { this.job = job; }

    // convenience
    public String getFullName() {
        String fn = firstName == null ? "" : firstName;
        String ln = lastName == null ? "" : lastName;
        String full = (fn + " " + ln).trim();
        return full.isBlank() ? null : full;
    }

    public String getJobTitle() {
        return (job != null && job.getJobTitle() != null) ? job.getJobTitle() : null;
    }
}