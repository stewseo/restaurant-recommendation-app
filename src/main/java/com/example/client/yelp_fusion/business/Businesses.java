package com.example.client.yelp_fusion.business;

import com.example.client.yelp_fusion.category.*;

public class Businesses {

    public String id, alias, name, image_url, url,phone,display_phone, price;
    public int review_count, distance;
    public double rating;
    public boolean is_claim, is_closed;
    public Coordinates coordinates;
    public String[] transactions;
    public Object[] hours, open;
    public Object messaging;
    public Location location;

    public Categories[] categories;

    public Businesses(){

    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String[] getTransactions() {
        return transactions;
    }

    public void setTransactions(String[] transactions) {
        this.transactions = transactions;
    }

    public Object[] getHours() {
        return hours;
    }

    public void setHours(Object[] hours) {
        this.hours = hours;
    }

    public Object[] getOpen() {
        return open;
    }

    public void setOpen(Object[] open) {
        this.open = open;
    }

    public Object getMessaging() {
        return messaging;
    }

    public void setMessaging(Object messaging) {
        this.messaging = messaging;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplay_phone() {
        return display_phone;
    }

    public void setDisplay_phone(String display_phone) {
        this.display_phone = display_phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public boolean isIs_claim() {
        return is_claim;
    }

    public void setIs_claim(boolean is_claim) {
        this.is_claim = is_claim;
    }

    public boolean isIs_closed() {
        return is_closed;
    }

    public void setIs_closed(boolean is_closed) {
        this.is_closed = is_closed;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Categories[] getCategories() {
        return categories;
    }

    public void setCategories(Categories[] categories) {
        this.categories = categories;
    }
}
