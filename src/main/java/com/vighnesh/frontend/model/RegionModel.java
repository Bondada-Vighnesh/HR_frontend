package com.vighnesh.frontend.model;

import java.util.List;

public class RegionModel {
    private Long regionId;
    private String regionName;
    private List<String> countryNames;

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public List<String> getCountryNames() { return countryNames; }
    public void setCountryNames(List<String> countryNames) { this.countryNames = countryNames; }
}