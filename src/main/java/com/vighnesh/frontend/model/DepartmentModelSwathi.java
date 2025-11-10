package com.vighnesh.frontend.model;

import java.util.List;

public class DepartmentModelSwathi {
    private String departmentName;
    private List<EmployeeModelSwathi> employees;

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public List<EmployeeModelSwathi> getEmployees() { return employees; }
    public void setEmployees(List<EmployeeModelSwathi> employees) { this.employees = employees; }
}