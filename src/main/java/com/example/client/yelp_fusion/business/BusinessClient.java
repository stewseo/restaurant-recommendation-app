package com.example.client.yelp_fusion.business;

import co.elastic.clients.elasticsearch._types.*;
import com.example.client.*;
import com.example.client.transport.*;
import com.example.client.util.*;

import java.io.*;

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

    public GetBusinessResponse getPipeline(GetBusinessRequest request) throws IOException {
        // create instance of SimpleEndpoint
        JsonEndpoint<GetBusinessRequest, GetBusinessResponse, ErrorResponse> endpoint = (JsonEndpoint<GetBusinessRequest, GetBusinessResponse, ErrorResponse>) GetBusinessRequest._ENDPOINT;
        return this.transport.performRequest(request, endpoint, this.transportOptions);
    }

    public final GetBusinessResponse getPipeline(Function<GetBusinessRequest.Builder, ObjectBuilder<GetBusinessRequest>> fn) throws IOException {
        return getPipeline(fn.apply(new GetBusinessRequest.Builder()).build());
    }

    public GetBusinessResponse getPipeline() throws IOException {
        return this.transport.performRequest(new GetBusinessRequest.Builder().build(), GetBusinessRequest._ENDPOINT,
                this.transportOptions);
    }
}
