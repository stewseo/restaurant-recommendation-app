package com.example.client.transport;

import co.elastic.clients.transport.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public interface Transport extends Closeable {

    static final Logger logger = LoggerFactory.getLogger(Transport.class);

    <RequestT, ResponseT, ErrorT> ResponseT performRequest(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions options
    ) throws IOException;

    <RequestT, ResponseT, ErrorT> CompletableFuture<ResponseT> performRequestAsync(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions options
    );

    Map<Object, Object> jsonpMapper();

    TransportOptions options();
}