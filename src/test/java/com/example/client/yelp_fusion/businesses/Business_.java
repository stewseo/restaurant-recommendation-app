package com.example.client.yelp_fusion.businesses;

import java.util.*;

public class Business_ {
    // ------------------------------------------------------- Fields
    public  Coordinates coordinates;

    public  Location location; // The location of this business, including address, city, state, zip code and country.

    public  String
            display_phone, // Phone number of the business formatted nicely to be displayed to users. The format is the standard phone number format for the business's country.
            id, // Unique Yelp ID of this business.
            alias, // Unique Yelp alias of this business. Can contain unicode characters.
            image_url, // URL of photo for this business.

    name, // name of business

    phone, // Phone number of the business.
            price, // Price level of the business. Value is one of $, $$, $$$ and $$$$.
            url; // URL for business page on Yelp.
    public  int
            limit,
            offset,
            open_at,
            review_count,
            distance;
    public  double
            latitude,
            longitude,
            rating;
    public  boolean
            is_claimed,
            is_closed,
            open_now;
    public  Object[]
            transactions,
            categories, // A list of category title and alias pairs associated with this business.
            photos,
            special_hours,
            attributes;

    public  Object[] hours;
    public  Object messaging; // Contains Business Messaging / Request a Quote information for this business.
    // This field only appears in the response for businesses that have messaging enabled.


    public Business_() {
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDisplay_phone() {
        return display_phone;
    }

    public void setDisplay_phone(String display_phone) {
        this.display_phone = display_phone;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOpen_at() {
        return open_at;
    }

    public void setOpen_at(int open_at) {
        this.open_at = open_at;
    }

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isis_claimed() {
        return is_claimed;
    }

    public void setis_claimed(boolean is_claimed) {
        this.is_claimed = is_claimed;
    }

    public boolean isIs_closed() {
        return is_closed;
    }

    public void setIs_closed(boolean is_closed) {
        this.is_closed = is_closed;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public Object[] getTransactions() {
        return transactions;
    }

    public void setTransactions(Object[] transactions) {
        this.transactions = transactions;
    }

    public Object[] getCategories() {
        return categories;
    }

    public void setCategories(Object[] categories) {
        this.categories = categories;
    }

    public Object[] getPhotos() {
        return photos;
    }

    public void setPhotos(Object[] photos) {
        this.photos = photos;
    }

    public Object[] getSpecial_hours() {
        return special_hours;
    }

    public void setSpecial_hours(Object[] special_hours) {
        this.special_hours = special_hours;
    }

    public Object[] getAttributes() {
        return attributes;
    }

    public void setAttributes(Object[] attributes) {
        this.attributes = attributes;
    }

    public Object[] getHours() {
        return hours;
    }

    public void setHours(Object[] hours) {
        this.hours = hours;
    }

    public Object getMessaging() {
        return messaging;
    }

    public void setMessaging(Object messaging) {
        this.messaging = messaging;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Business_ business_)) return false;

        if (limit != business_.limit) return false;
        if (offset != business_.offset) return false;
        if (open_at != business_.open_at) return false;
        if (review_count != business_.review_count) return false;
        if (distance != business_.distance) return false;
        if (Double.compare(business_.latitude, latitude) != 0) return false;
        if (Double.compare(business_.longitude, longitude) != 0) return false;
        if (Double.compare(business_.rating, rating) != 0) return false;
        if (is_claimed != business_.is_claimed) return false;
        if (is_closed != business_.is_closed) return false;
        if (open_now != business_.open_now) return false;
        if (!Objects.equals(coordinates, business_.coordinates))
            return false;
        if (!Objects.equals(location, business_.location)) return false;
        if (!Objects.equals(display_phone, business_.display_phone))
            return false;
        if (!Objects.equals(id, business_.id)) return false;
        if (!Objects.equals(alias, business_.alias)) return false;
        if (!Objects.equals(image_url, business_.image_url)) return false;
        if (!Objects.equals(name, business_.name)) return false;
        if (!Objects.equals(phone, business_.phone)) return false;
        if (!Objects.equals(price, business_.price)) return false;
        if (!Objects.equals(url, business_.url)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(transactions, business_.transactions)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(categories, business_.categories)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(photos, business_.photos)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(special_hours, business_.special_hours)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(attributes, business_.attributes)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(hours, business_.hours)) return false;
        return Objects.equals(messaging, business_.messaging);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = coordinates != null ? coordinates.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (display_phone != null ? display_phone.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (image_url != null ? image_url.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + limit;
        result = 31 * result + offset;
        result = 31 * result + open_at;
        result = 31 * result + review_count;
        result = 31 * result + distance;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rating);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (is_claimed ? 1 : 0);
        result = 31 * result + (is_closed ? 1 : 0);
        result = 31 * result + (open_now ? 1 : 0);
        result = 31 * result + Arrays.hashCode(transactions);
        result = 31 * result + Arrays.hashCode(categories);
        result = 31 * result + Arrays.hashCode(photos);
        result = 31 * result + Arrays.hashCode(special_hours);
        result = 31 * result + Arrays.hashCode(attributes);
        result = 31 * result + Arrays.hashCode(hours);
        result = 31 * result + (messaging != null ? messaging.hashCode() : 0);
        return result;
    }

}
