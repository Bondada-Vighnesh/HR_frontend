package com.vighnesh.frontend.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CountryModelSwathi {
    @JsonProperty("countryId")   // ensure JSON → field binding
    private String countryId;

    @JsonProperty("countryName")
    private String countryName;

    @JsonProperty("cities")
    private List<String> cities;

    public String getCountryId() { return countryId; }
    public void setCountryId(String countryId) { this.countryId = countryId; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public List<String> getCities() { return cities; }
    public void setCities(List<String> cities) { this.cities = cities; }
}
