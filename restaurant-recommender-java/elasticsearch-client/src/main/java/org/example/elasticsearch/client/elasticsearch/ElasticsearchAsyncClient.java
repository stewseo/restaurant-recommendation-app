package org.example.elasticsearch.client.elasticsearch;

import org.example.elasticsearch.client.*;
import org.example.elasticsearch.client.transport.*;
public class ElasticsearchAsyncClient extends ApiClient<ElasticsearchTransport, ElasticsearchAsyncClient> {

    public ElasticsearchAsyncClient(ElasticsearchTransport transport) {
        super(transport, null);
    }

    public ElasticsearchAsyncClient(ElasticsearchTransport transport, TransportOptions transportOptions) {
        super(transport, transportOptions);
    }

    @Override
    public ElasticsearchAsyncClient withTransportOptions( TransportOptions transportOptions) {
        return new ElasticsearchAsyncClient(this.transport, transportOptions);
    }

    
}
