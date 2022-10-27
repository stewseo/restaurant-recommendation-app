package org.example.yelp.fusion.client;

import org.example.elasticsearch.client.*;
import org.example.elasticsearch.client.transport.*;
import org.example.yelp.fusion.client.transport.*;

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



//    public final BusinessDetailsResponse businessSearch(
//            Function<BusinessSearchRequest.Builder, ObjectBuilder<BusinessSearchRequest>> fn)
//            throws IOException {
//        return businessSearch(fn.apply(new BusinessDetailsRequest.Builder()).build());
//    }
//
//
//    public BusinessDetailsResponse businessDetails(BusinessDetailsRequest request)
//            throws IOException {

//        @SuppressWarnings("unchecked")
//        JsonEndpoint<BusinessDetailsRequest, BusinessDetailsResponse, ErrorResponse> endpoint =
//                (JsonEndpoint<BusinessDetailsRequest, BusinessDetailsRequest, ErrorResponse>) BusinessDetailsRequest._ENDPOINT;
//
//        try {
//            return this.transport.performRequest(request, endpoint, this.transportOptions);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public final BusinessDetailsResponse businessDetails(
//            Function<BusinessDetailsRequest.Builder, ObjectBuilder<BusinessDetailsRequest>> fn)
//            throws IOException {
//
//        return businessSearch(fn.apply(new BusinessDetailsRequest.Builder()).build());
    }

