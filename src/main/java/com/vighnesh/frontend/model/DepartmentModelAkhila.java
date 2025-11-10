package com.vighnesh.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class DepartmentModelAkhila {

    // Accepts either "departmentId" or legacy "id"
    @JsonAlias({ "departmentId", "id" })
    private Long departmentId;

    private String departmentName;
    private String locationCity;
    private Long managerId;

    // getters/setters
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getLocationCity() { return locationCity; }
    public void setLocationCity(String locationCity) { this.locationCity = locationCity; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    // Optional compatibility getter if any old template still uses d.id
    public Long getId() { return departmentId; }

    @Override
    public String toString() {
        return "DepartmentModelAkhila{" +
                "departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", locationCity='" + locationCity + '\'' +
                ", managerId=" + managerId +
                '}';
    }
}