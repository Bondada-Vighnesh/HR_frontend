package com.vighnesh.frontend.vm.satya;

public class SatyaLocationVM {
    private Long id;
    private String streetAddress;
    private String postalCode;
    private String city;
    private String stateProvince;
    private String countryName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
}