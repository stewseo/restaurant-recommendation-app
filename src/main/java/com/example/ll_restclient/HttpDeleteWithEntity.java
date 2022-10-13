package com.example.ll_restclient;

import org.apache.http.client.methods.*;

import java.net.*;

final class HttpDeleteWithEntity extends HttpEntityEnclosingRequestBase {

    static final String METHOD_NAME = HttpDelete.METHOD_NAME;

    HttpDeleteWithEntity(final URI uri) {
        setURI(uri);
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}