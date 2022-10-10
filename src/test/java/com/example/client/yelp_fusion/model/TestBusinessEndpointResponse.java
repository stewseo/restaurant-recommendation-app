package com.example.client.yelp_fusion.model;

import co.elastic.clients.json.jackson.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.businesses.*;
import com.fasterxml.jackson.core.*;
import jakarta.json.*;

import java.util.*;

// model for yelp fusion Response body
public class TestBusinessEndpointResponse {

    public List<Business> businesses;
    public int total;
    public Object region;

    public TestBusinessEndpointResponse(List<Business> businesses, int total, Object region) {
        this.businesses = businesses;
        this.total = total;
        this.region = region;
    }

    public TestBusinessEndpointResponse() {}

    public List<Business> getBusiness() {
        return businesses;
    }

    public void setBusinesses(List<Business> business) {
        this.businesses = business;
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

    //deserialize JSON content from given JSON content String.
    private void deserialize(JsonObject jsonObject) throws JsonProcessingException {

        JsonValue businesses =  PrintUtils.println(jsonObject.get("businesses"));

        PrintUtils.titleCyan(businesses.asJsonArray().size());

        TestBusinessEndpointResponse businessResponse = PrintUtils.println(new JacksonJsonpMapper().objectMapper().readValue(jsonObject.toString(), TestBusinessEndpointResponse.class));

        Business[] business = new JacksonJsonpMapper().objectMapper().readValue(businesses.toString(),  Business[].class);

    }
}
