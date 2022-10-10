package com.example.client.yelp_fusion.business;

public class Location {

    String address1, address2, address3, city, county, cross_streets,state, zip_code, country;

    Object[] display_address;
    public Location(){
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCross_streets() {
        return cross_streets;
    }

    public void setCross_streets(String cross_streets) {
        this.cross_streets = cross_streets;
    }

    public Object[] getDisplay_address() {
        return display_address;
    }

    public void setDisplay_address(Object[] display_address) {
        this.display_address = display_address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }
}
