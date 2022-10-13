package com.example.client.transport;

import com.example.client.json.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public interface Transport extends Closeable {

    static final Logger logger = LoggerFactory.getLogger(Transport.class);

    <RequestT, ResponseT, ErrorT> ResponseT performRequest(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions options
    ) throws IOException, URISyntaxException;

    <RequestT, ResponseT, ErrorT> CompletableFuture<ResponseT> performRequestAsync(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions options
    );

    JsonpMapper jsonpMapper();

    TransportOptions options();
}