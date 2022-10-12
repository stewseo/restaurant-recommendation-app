package com.example.client.yelp_fusion.businesses;

import java.util.*;

public class Business {

    public Coordinates coordinates;

    public Location location; // The location of this business, including address, city, state, zip code and country.

    // optional
    public String
            display_phone, // Phone number of the business formatted nicely to be displayed to users. The format is the standard phone number format for the business's country.
            id, // Unique Yelp ID of this business.
            alias, // Unique Yelp alias of this business. Can contain unicode characters.
            image_url, // URL of photo for this business.

            name, // name of business

            phone, // Phone number of the business.
            price, // Price level of the business. Value is one of $, $$, $$$ and $$$$.
            rating,  // Rating for this business (value ranges from 1, 1.5, ... 4.5, 5).
            url; // URL for business page on Yelp.

    public int
            limit,

            offset,
            open_at,
            review_count,
            distance;

    public double
            latitude,
            longitude;
    public boolean
            is_claim,
            is_closed,
            open_now;
    public Object[]
            transactions,
            categories, // A list of category title and alias pairs associated with this business.
            photos,
            special_hours,
            attributes;

    public Hours[] hours;
    public Object messaging; // Contains Business Messaging / Request a Quote information for this business.
                            // This field only appears in the response for businesses that have messaging enabled.


    public Business(){}


    public Hours[] getHours() {
        return hours;
    }

    public void setHours(Hours[] hours) {
        this.hours = hours;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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

    public Object getMessaging() {
        return messaging;
    }

    public void setMessaging(Object messaging) {
        this.messaging = messaging;
    }

    @Override
    public String toString() {
        return "Business{" +
                "coordinates=" + coordinates +
                ", location=" + location +
                ", display_phone='" + display_phone + '\'' +
                ", id='" + id + '\'' +
                ", alias='" + alias + '\'' +
                ", image_url='" + image_url + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", price='" + price + '\'' +
                ", rating='" + rating + '\'' +
                ", url='" + url + '\'' +
                ", limit=" + limit +
                ", open_at=" + open_at +
                ", review_count=" + review_count +
                ", distance=" + distance +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", is_claim=" + is_claim +
                ", is_closed=" + is_closed +
                ", open_now=" + open_now +
                ", transactions=" + Arrays.toString(transactions) +
                ", categories=" + Arrays.toString(categories) +
                ", hours=" + Arrays.toString(hours) +
                ", photos=" + Arrays.toString(photos) +
                ", special_hours=" + Arrays.toString(special_hours) +
                ", attributes=" + Arrays.toString(attributes) +
                ", messaging=" + messaging +
                '}';
    }

    static class Hours {
        public Object[] open;
        public String hours_type;
        public boolean is_open_now;

        public Hours() {
        }

        public Object[] getOpen() {
            return open;
        }

        public void setOpen(Object[] open) {
            this.open = open;
        }

        public String getHours_type() {
            return hours_type;
        }

        public void setHours_type(String hours_type) {
            this.hours_type = hours_type;
        }

        public boolean isIs_open_now() {
            return is_open_now;
        }

        public void setIs_open_now(boolean is_open_now) {
            this.is_open_now = is_open_now;
        }

        @Override
        public String toString() {
            return "Hours{" +
                    "open=" + Arrays.toString(open) +
                    ", hours_type='" + hours_type + '\'' +
                    ", is_open_now=" + is_open_now +
                    '}';
        }
    }
}
