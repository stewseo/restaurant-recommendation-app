package com.example.client.transport;

import com.example.client.yelp_fusion.businesses.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public interface Transport extends Closeable {

    static final Logger logger = LoggerFactory.getLogger(Transport.class);

    <RequestT, ResponseT, ErrorT> BusinessEndpointResponse performRequest(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions options
    ) throws IOException, URISyntaxException;

    <RequestT, ResponseT, ErrorT> CompletableFuture<ResponseT> performRequestAsync(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions options
    );

    co.elastic.clients.json.JsonpMapper jsonpMapper();

    TransportOptions options();
}