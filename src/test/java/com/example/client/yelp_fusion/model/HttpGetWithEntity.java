package com.example.client.yelp_fusion.model;

import org.apache.http.client.methods.*;

import java.net.*;

final class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {

    static final String METHOD_NAME = HttpGet.METHOD_NAME;

    HttpGetWithEntity(final URI uri) {
        setURI(uri);
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}