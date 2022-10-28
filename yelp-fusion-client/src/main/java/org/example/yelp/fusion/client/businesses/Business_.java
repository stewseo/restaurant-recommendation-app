package org.example.yelp.fusion.client.businesses;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

@JsonIgnoreProperties(value = { "limit" ,"error" ,"timestamp", "messaging" ,"special_hours"})
public class Business_ {

    public String id, alias, name, phone, display_phone, price, rating, url;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String image_url;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Object region;

    public Coordinates coordinates;

    public Object location;

    public int open_at, review_count;

    public boolean is_closed, open_now;

    public Object[] transactions, categories;

    public Hours[] hours;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    int distance;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public boolean is_claimed;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Object[] photos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Attributes attributes;

    public String display_phone() {
        return display_phone;
    }

    public void setDisplay_phone(String display_phone) {
        this.display_phone = display_phone;
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String alias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String phone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String price() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String rating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String url() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String image_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Object region() {
        return region;
    }

    public void setRegion(Object region) {
        this.region = region;
    }

    public Coordinates coordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Object location() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public int open_at() {
        return open_at;
    }

    public void setOpen_at(int open_at) {
        this.open_at = open_at;
    }

    public int review_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public boolean is_closed() {
        return is_closed;
    }

    public void setIs_closed(boolean is_closed) {
        this.is_closed = is_closed;
    }

    public boolean open_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public Object[] transactions() {
        return transactions;
    }

    public void setTransactions(Object[] transactions) {
        this.transactions = transactions;
    }

    public Object[] categories() {
        return categories;
    }

    public void setCategories(Object[] categories) {
        this.categories = categories;
    }

    public Hours[] hours() {
        return hours;
    }

    public void setHours(Hours[] hours) {
        this.hours = hours;
    }

    public int distance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean is_claimed() {
        return is_claimed;
    }

    public void setIs_claimed(boolean is_claimed) {
        this.is_claimed = is_claimed;
    }

    public Object[] photos() {
        return photos;
    }

    public void setPhotos(Object[] photos) {
        this.photos = photos;
    }

    public Attributes attributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Business_() {}

    public static class Coordinates {

        public Double latitude;

        public GeoLocation geo_location;
        public Double longitude;

        public Coordinates() {}


        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public GeoLocation geo_location() {
            return geo_location;
        }

        public void setGeo_location(GeoLocation geo_location) {
            this.geo_location = geo_location;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double latitude() {
            return latitude;
        }

        public Double longitude() {
            return longitude;
        }

        public static class GeoLocation {
            public Double latitude;
            public Double longitude;
            public GeoLocation(){}

            public Double latitude() {
                return latitude;
            }

            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }

            public Double longitude() {
                return longitude;
            }

            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }
        }
    }

    @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Business business_)) return false;

            if (!Objects.equals(id, business_.getId())) return false;
            return Objects.equals(alias, business_.getName());
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (alias != null ? alias.hashCode() : 0);
            return result;
        }
}


