package com.example.client.yelp_fusion;

import com.example.client.transport.*;
import com.example.client.yelp_fusion.businesses.*;
import com.example.client.yelp_fusion.model.*;

import java.io.*;
import java.net.*;
import java.util.function.*;

// a test esClient that provides access to yelp namespace apis: businesses, categories, events
public class TestYelpClient extends com.example.client.ApiClient<YelpFusionTransport, TestYelpClient> {

    public TestYelpClient(YelpFusionTransport transport) {
        super(transport, null);
    }

    // accepts a YelpFusionTransport and TransportOptions.
    public TestYelpClient(YelpFusionTransport transport, TransportOptions transportOptions) {
        super(transport, transportOptions);
    }

    // accept transport options, returns a new test yelp client.
    @Override
    public TestYelpClient withTransportOptions(TransportOptions transportOptions) {
        return new TestYelpClient(this.transport, transportOptions);
    }
    // accepts a TestBusinessSearchRequest
    // returns a TestBusinessSearchResponse
    public BusinessEndpointResponse businessSearch(TestBusinessSearchRequest request)
            throws IOException {

        @SuppressWarnings("unchecked")
        JsonEndpoint<TestBusinessSearchRequest, TestBusinessEndpointResponse, com.example.client._types.ErrorResponse> endpoint =
                (JsonEndpoint<TestBusinessSearchRequest, TestBusinessEndpointResponse, com.example.client._types.ErrorResponse>) TestBusinessSearchRequest._ENDPOINT;

        try {

            return this.transport.performRequest(request, endpoint, this.transportOptions);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public final BusinessEndpointResponse businessSearch(
            Function<TestBusinessSearchRequest.Builder, com.example.client.util.ObjectBuilder<TestBusinessSearchRequest>> fn)
            throws IOException {
        return businessSearch(fn.apply(new TestBusinessSearchRequest.Builder()).build());
    }



    public BusinessEndpointResponse businessDetails(TestBusinessSearchRequest request)
            throws IOException {

        @SuppressWarnings("unchecked")
        JsonEndpoint<TestBusinessSearchRequest, TestBusinessEndpointResponse, com.example.client._types.ErrorResponse> endpoint =
                (JsonEndpoint<TestBusinessSearchRequest, TestBusinessEndpointResponse, com.example.client._types.ErrorResponse>) TestBusinessSearchRequest._ENDPOINT;
        try {
            return this.transport.performRequest(request, endpoint, this.transportOptions);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public final BusinessEndpointResponse businessDetails(
            Function<TestBusinessSearchRequest.Builder, com.example.client.util.ObjectBuilder<TestBusinessSearchRequest>> fn)
            throws IOException {
        return businessSearch(fn.apply(new TestBusinessSearchRequest.Builder()).build());
    }
}
