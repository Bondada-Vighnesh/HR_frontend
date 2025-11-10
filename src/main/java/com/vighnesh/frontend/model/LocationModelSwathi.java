package com.vighnesh.frontend.model;

import java.util.List;

public class LocationModelSwathi {
    private String city;
    private List<DepartmentModelSwathi> departments;

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public List<DepartmentModelSwathi> getDepartments() { return departments; }
    public void setDepartments(List<DepartmentModelSwathi> departments) { this.departments = departments; }
}