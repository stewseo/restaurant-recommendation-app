package org.example.yelp.fusion.client;



import org.example.elasticsearch.client.*;
import org.example.elasticsearch.client._types.*;
import org.example.elasticsearch.client.transport.*;
import org.example.elasticsearch.client.transport.endpoints.*;
import org.example.elasticsearch.client.util.*;
import org.example.yelp.fusion.client.business.*;
import org.example.yelp.fusion.client.business.search.*;
import org.example.yelp.fusion.client.exception.*;
import org.example.yelp.fusion.client.transport.*;

import java.io.*;
import java.net.*;
import java.util.function.*;

import static com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer.getDeserializer;


public class YelpFusionClient extends ApiClient<YelpFusionTransport, YelpFusionClient> {

    public YelpFusionClient(YelpFusionTransport transport) {
        super(transport, null);
    }

    public YelpFusionClient(YelpFusionTransport transport, TransportOptions transportOptions) {
        super(transport, transportOptions);
    }

    @Override
    public YelpFusionClient withTransportOptions(TransportOptions transportOptions) {
        return new YelpFusionClient(this.transport, transportOptions);

    }


    public <TDocument> BusinessSearchResponse<TDocument> search(BusinessSearchRequest request, Class<TDocument> tDocumentClass)
            throws IOException, YelpFusionException, URISyntaxException {
        @SuppressWarnings("unchecked")
        JsonEndpoint<BusinessSearchRequest, BusinessSearchResponse<TDocument>, ErrorResponse> endpoint =
                (JsonEndpoint<BusinessSearchRequest, BusinessSearchResponse<TDocument>, ErrorResponse>) BusinessSearchRequest._ENDPOINT;

        endpoint = new EndpointWithResponseMapperAttr<>(endpoint,
                "org.example.yelp.fusion.client:Deserializer:_global.search.TDocument", getDeserializer(tDocumentClass));

        return this.transport.performRequest(request, endpoint, this.transportOptions);
    }

    public final <TDocument> BusinessSearchResponse<TDocument> search(
            Function<BusinessSearchRequest.Builder, ObjectBuilder<BusinessSearchRequest>> fn, Class<TDocument> tDocumentClass)
            throws IOException, YelpFusionException, URISyntaxException {

        return search(fn.apply(new BusinessSearchRequest.Builder()).build(), tDocumentClass);
    }

    public <TDocument> BusinessDetailsResponse<TDocument> businessDetails(BusinessDetailsRequest request, Class<TDocument> tDocumentClass)
            throws IOException, YelpFusionException, URISyntaxException {
        @SuppressWarnings("unchecked")
        JsonEndpoint<BusinessDetailsRequest, BusinessDetailsResponse<TDocument>, ErrorResponse> endpoint =
                (JsonEndpoint<BusinessDetailsRequest, BusinessDetailsResponse<TDocument>, ErrorResponse>) BusinessDetailsRequest._ENDPOINT;

        endpoint = new EndpointWithResponseMapperAttr<>(endpoint,
                "org.example.yelp.fusion.client:Deserializer:_global.search.TDocument", getDeserializer(tDocumentClass));

        return this.transport.performRequest(request, endpoint, this.transportOptions);
    }

    public final <TDocument> BusinessDetailsResponse<TDocument> businessDetails(
            Function<BusinessDetailsRequest.Builder, ObjectBuilder<BusinessDetailsRequest>> fn, Class<TDocument> tDocumentClass)
            throws IOException, YelpFusionException, URISyntaxException {

        return businessDetails(fn.apply(new BusinessDetailsRequest.Builder()).build(), tDocumentClass);
    }
}

