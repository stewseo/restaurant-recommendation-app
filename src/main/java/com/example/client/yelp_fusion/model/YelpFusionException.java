package com.example.client.yelp_fusion.model;

import com.example.client._types.*;

public class YelpFusionException extends RuntimeException {
    private final ErrorResponse response;
    private final String endpointId;

    public YelpFusionException(String endpointId, ErrorResponse response) {
        super("[" + endpointId + "] failed: [" + response.error().type() + "] " + response.error().reason());
        this.response = response;
        this.endpointId = endpointId;
    }

    public String endpointId() {
        return this.endpointId;
    }


    public ErrorResponse response() {
        return this.response;
    }


    public ErrorCause error() {
        return this.response.error();
    }


    public int status() {return this.response.status();}
}
