package com.example.client.yelp_fusion.category;

import jakarta.json.*;

public class Categories {

    public String alias, title;

    public String[] parents;

    public Object[] country_whitelist, country_blacklist;


    public String getAlias() {
        return alias;
    }


    public Object getCountry_blacklist() {
        return country_blacklist;
    }



    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getParents() {
        return parents;
    }



    public void setParents(String[] parents) {
        this.parents = parents;
    }


    public void setCountry_whitelist(Object[] country_whitelist) {
        this.country_whitelist = country_whitelist;
    }

    public void setCountry_blacklist(Object[] country_blacklist) {
        this.country_blacklist = country_blacklist;
    }

    public Object getCountry_whitelist() {
        return country_whitelist;
    }



    public Categories() {
    }
    public Categories(JsonValue jsonValue) {
    }
}
