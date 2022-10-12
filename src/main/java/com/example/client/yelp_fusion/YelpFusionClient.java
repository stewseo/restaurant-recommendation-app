package com.example.client.yelp_fusion;

import com.example.client.*;
import com.example.client._types.*;
import com.example.client.transport.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.businesses.*;

import java.io.*;
import java.net.*;
import java.util.function.*;

public class YelpFusionClient extends ApiClient<YelpFusionTransport, YelpFusionClient> {

    public YelpFusionClient(YelpFusionTransport transport) {
        super(transport, null);
    }

    public YelpFusionClient(YelpFusionTransport transport, TransportOptions transportOptions) {
        super(transport, transportOptions);
    }

    public YelpFusionClient(String host, YelpFusionTransport transport, TransportOptions transportOptions) {
        super(transport, transportOptions);
    }

    @Override
    public YelpFusionClient withTransportOptions(TransportOptions transportOptions) {
        return new YelpFusionClient(this.transport, transportOptions);
    }

    public <TDocument> BusinessEndpointResponse index(BusinessRequest<TDocument> request) throws IOException, URISyntaxException {
        // endpoint t=
        JsonEndpoint<
                BusinessRequest<?>,  // class that extends WriteBaseRequest.
                BusinessResponse,  // class that extends WriteBaseResponse
                ErrorResponse>  // Error code
                endpoint = // JsonEndpoint responsibilities =
                (JsonEndpoint<BusinessRequest<?>,
                        BusinessResponse,
                        ErrorResponse>)
                        BusinessRequest._ENDPOINT;

        return this.transport.performRequest(request, endpoint, this.transportOptions);
        // send request fields are specified by input parameters ,
        // endpoint fields are specified by BusinessRequest ,
        // transportOptions fields are specified in the YelpClient input parameters, and by RestClient methods addHeader, setHeader. ApiClient subclasses inherit a transportOptions field.
    }
    
    public final <TDocument> BusinessEndpointResponse index(
            Function<BusinessRequest.Builder<TDocument>, ObjectBuilder<BusinessRequest<TDocument>>> fn)
            throws IOException {
        try {
            return index(fn.apply(new BusinessRequest.Builder<TDocument>()).build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public BusinessClient business() {
        return new BusinessClient(this.transport, this.transportOptions);
    }
}

