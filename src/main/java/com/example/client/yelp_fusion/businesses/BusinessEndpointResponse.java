package com.example.client.yelp_fusion.businesses;

import co.elastic.clients.json.jackson.*;
import com.fasterxml.jackson.core.*;
import jakarta.json.*;

import java.util.*;

// TODO: create abstract BusinessResponseBody<TDocument> implements JsonpSerializable. Implement custom deserializers
//  create Builders and Endpoints for BusinessDetailsRequest, BusinessSearchRequest, BusinessReviewsRequest
@SuppressWarnings("unused")
public class BusinessEndpointResponse {

    public Business business;

    public List<Business> businesses;

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

    public int total;

    public Coordinates coordinates;

    public Location location; // The location of this business, including address, city, state, zip code and country.

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
            is_claimed,
            is_closed,
            open_now;
    public Object region;

    public Object[]
            transactions,
            categories, // A list of category title and alias pairs associated with this business.
            photos,
            special_hours,
            attributes;

    public Business.Hours[] hours;

    public Object messaging; // Contains Business Messaging / Request a Quote information for this business.
    // This field only appears in the response for businesses that have messaging enabled.

    public BusinessEndpointResponse(Business business, List<Business> businesses, int total, Object region) {
        this.business = business;
        this.businesses = businesses;
        this.total = total;
        this.region = region;
    }

    public BusinessEndpointResponse() {}



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


    public boolean isIs_claimed() {
        return is_claimed;
    }

    public void setIs_claimed(boolean is_claimed) {
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

    public String getDisplay_phone() {
        return display_phone;
    }

    public void setDisplay_phone(String display_phone) {
        this.display_phone = display_phone;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Object getRegion() {
        return region;
    }

    public void setRegion(Object region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "BusinessEndpointResponse{" +
                "businesses=" + businesses +
                ", total=" + total +
                ", region=" + region +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessEndpointResponse that)) return false;

        if (total != that.total) return false;
        if (!Objects.equals(businesses, that.businesses)) return false;
        return Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        int result = businesses != null ? businesses.hashCode() : 0;
        result = 31 * result + total;
        result = 31 * result + (region != null ? region.hashCode() : 0);
        return result;
    }

    //deserialize JSON content from given JSON content String.
    private void deserialize(JsonObject jsonObject) throws JsonProcessingException {
        JsonValue businesses =  jsonObject.get("businesses");
        BusinessEndpointResponse businessResponse = new JacksonJsonpMapper().objectMapper().readValue(jsonObject.toString(), BusinessEndpointResponse.class);
        Business[] business = new JacksonJsonpMapper().objectMapper().readValue(businesses.toString(),  Business[].class);
    }
}
