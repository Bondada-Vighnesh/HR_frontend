package com.vighnesh.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class DepartmentModelJaheer {
	
	@JsonAlias({ "departmentId", "id" })
    private Long departmentId;
    private String departmentName;
    private Long managerId;

    // optional, in case backend includes it later
    private String locationCity;
    
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }


    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public String getLocationCity() { return locationCity; }
    public void setLocationCity(String locationCity) { this.locationCity = locationCity; }
}