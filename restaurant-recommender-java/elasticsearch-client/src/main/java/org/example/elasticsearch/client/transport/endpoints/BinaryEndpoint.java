package org.example.elasticsearch.client.transport.endpoints;

import org.example.elasticsearch.client.transport.*;

import java.util.*;
import java.util.function.*;

public class BinaryEndpoint<RequestT> extends EndpointBase<RequestT, BinaryResponse> {

    public BinaryEndpoint(
            String id,
            Function<RequestT, String> method,
            Function<RequestT, String> requestUrl,
            Function<RequestT,
                    Map<String, String>> queryParameters,
            Function<RequestT, Map<String, String>> headers,
            boolean hasRequestBody,
            Object ignored // same number of arguments as SimpleEndpoint
    ) {
        super(id, method, requestUrl, queryParameters, headers, hasRequestBody);
    }

    @Override
    public boolean isError(int statusCode) {
        return statusCode >= 400;
    }
}