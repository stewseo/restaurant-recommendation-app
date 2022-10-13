package com.example.client.yelp_fusion.businesses;


import com.example.client.*;
import com.example.client._types.*;
import com.example.client.transport.*;
import com.example.client.util.*;

import java.io.*;
import java.net.*;
import java.util.function.*;

@SuppressWarnings("unused")
public class BusinessClient extends ApiClient<YelpFusionTransport, BusinessClient> {

    String ingestPipelineApiPath = "/_ingest/pipeline";

    public BusinessClient(YelpFusionTransport transport) {
        super(transport, null);
    }

    public BusinessClient(YelpFusionTransport transport, TransportOptions transportOptions) {
        super(transport, transportOptions);
    }

    @Override
    public BusinessClient withTransportOptions(TransportOptions transportOptions) {
        return new BusinessClient(this.transport, transportOptions);
    }

    public BusinessSearchResponse getBusinesses(BusinessSearchRequest request) throws IOException, URISyntaxException {
        // create instance of SimpleEndpoint
        JsonEndpoint<BusinessSearchRequest, BusinessSearchResponse, ErrorResponse> endpoint
                = (JsonEndpoint<BusinessSearchRequest, BusinessSearchResponse, ErrorResponse>) BusinessSearchRequest._ENDPOINT;
        return this.transport.performRequest(request, endpoint, this.transportOptions);
    }

    public final BusinessSearchResponse getBusinesses(Function<BusinessSearchRequest.Builder, ObjectBuilder<BusinessSearchRequest>> fn) throws IOException, URISyntaxException {
        return getBusinesses(fn.apply(new BusinessSearchRequest.Builder()).build());
    }

    public BusinessSearchResponse getBusinesses() throws IOException, URISyntaxException {
        return this.transport.performRequest(new BusinessSearchRequest.Builder().build(), BusinessSearchRequest._ENDPOINT,
                this.transportOptions);
    }
}
