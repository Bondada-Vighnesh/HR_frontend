package com.vighnesh.frontend.model;

public class EmployeeSummaryModel {
    private String countryName;
    private String city;
    private String stateProvince;
    private String departmentName;
    private Long employeeId;
    private String employeeName;
    private String email;
    private String phoneNumber;

    public String getCountryName() { 
    	return countryName;
    	}
    public void setCountryName(String countryName) {
    	this.countryName = countryName;
    	}
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}