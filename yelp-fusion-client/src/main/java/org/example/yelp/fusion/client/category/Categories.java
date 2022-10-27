package org.example.yelp.fusion.client.category;


import org.example.elasticsearch.client.json.*;

import java.util.*;

@JsonpDeserializable
public class Categories {

    public String alias, title;

    public String[] parents;

    public Object[] country_whitelist, country_blacklist;

    public String getAlias() {
        return alias;
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

    public String[] getParents() {
        return parents;
    }

    public void setParents(String[] parents) {
        this.parents = parents;
    }

    public Object[] getCountry_whitelist() {
        return country_whitelist;
    }

    public void setCountry_whitelist(Object[] country_whitelist) {
        this.country_whitelist = country_whitelist;
    }

    public Object[] getCountry_blacklist() {
        return country_blacklist;
    }

    public void setCountry_blacklist(Object[] country_blacklist) {
        this.country_blacklist = country_blacklist;
    }

    public Categories() {
    }

    @Override
    public String toString() {
        return "Category{" +
                "alias='" + alias + '\'' +
                ", title='" + title + '\'' +
                ", parents=" + Arrays.toString(parents) +
                ", country_whitelist=" + Arrays.toString(country_whitelist) +
                ", country_blacklist=" + Arrays.toString(country_blacklist) +
                '}';
    }
}
