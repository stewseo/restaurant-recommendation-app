package com.example.client.yelp_fusion;

import com.example.client.*;
import com.example.client.transport.*;
import com.example.client.yelp_fusion.business.*;

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

    public BusinessClient business() {
        return new BusinessClient(this.transport, this.transportOptions);
    }
}

