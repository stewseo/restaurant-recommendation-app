package com.example.client.transport;


import com.example.client.json.*;

public interface JsonEndpoint<RequestT, ResponseT, ErrorT> extends Endpoint<RequestT, ResponseT, ErrorT> {

    /**
     * The entity parser for the response body.
     */
        JsonpDeserializer<ResponseT> responseDeserializer();
}