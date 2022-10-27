package org.example.elasticsearch.client.transport;


import org.example.elasticsearch.client.json.*;


public interface JsonEndpoint<RequestT, ResponseT, ErrorT> extends Endpoint<RequestT, ResponseT, ErrorT> {

        JsonpDeserializer<ResponseT> responseDeserializer();
}