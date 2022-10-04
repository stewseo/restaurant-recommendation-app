package com.example.client.yelp_fusion.business;

import com.example.client.yelp_fusion.category.*;

import javax.swing.plaf.synth.*;

public class GetBusinessResponse {
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Categories[] categories;
    public Businesses[] businesses;
    public Object region;

    int total;

    public GetBusinessResponse(){

    }

    public Categories[] getCategories() {
        return categories;
    }

    public void setCategories(Categories[] categories) {
        this.categories = categories;
    }

    public Businesses[] getBusinesses() {
        return businesses;
    }

    public void setBusinesses(Businesses[] businesses) {
        this.businesses = businesses;
    }

    public Object getRegion() {
        return region;
    }

    public void setRegion(Object region) {
        this.region = region;
    }

}
