package com.example.client.yelp_fusion;

import com.example.client._types.*;
import com.example.client.transport.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.businesses.*;

import java.io.*;
import java.net.*;
import java.util.function.*;

public class YelpFusionClient extends com.example.client.ApiClient<YelpFusionTransport, YelpFusionClient> {

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

    public BusinessSearchResponse businessSearch(BusinessSearchRequest request)
            throws IOException {

        @SuppressWarnings("unchecked")
        JsonEndpoint<BusinessSearchRequest, BusinessSearchResponse, ErrorResponse> endpoint =
                (JsonEndpoint<BusinessSearchRequest, BusinessSearchResponse, ErrorResponse>) BusinessSearchRequest._ENDPOINT;

        try {
            return this.transport.performRequest(request, endpoint, this.transportOptions);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public final BusinessSearchResponse businessSearch(
            Function<BusinessSearchRequest.Builder, com.example.client.util.ObjectBuilder<BusinessSearchRequest>> fn)
            throws IOException {
        return businessSearch(fn.apply(new BusinessSearchRequest.Builder()).build());
    }



    public BusinessDetailsResponse businessDetails(BusinessDetailsRequest request)
            throws IOException {

        @SuppressWarnings("unchecked")
        JsonEndpoint<BusinessDetailsRequest, BusinessDetailsResponse, ErrorResponse> endpoint =
                (JsonEndpoint<BusinessDetailsRequest, BusinessDetailsResponse, ErrorResponse>) BusinessDetailsRequest._ENDPOINT;
        try {
            return this.transport.performRequest(request, endpoint, this.transportOptions);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public final BusinessDetailsResponse businessDetails(
            Function<BusinessDetailsRequest.Builder, ObjectBuilder<BusinessDetailsRequest>> fn)
            throws IOException {
        return businessDetails(fn.apply(new BusinessDetailsRequest.Builder()).build());
    }
}
