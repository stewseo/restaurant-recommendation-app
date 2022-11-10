package org.example.elasticsearch.client.transport.endpoints;

import org.example.elasticsearch.client._types.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.transport.*;

import java.util.*;
import java.util.function.*;

public class SimpleJsonEndpoint<RequestT, ResponseT> extends SimpleEndpoint<RequestT, ResponseT>
        implements JsonEndpoint<RequestT, ResponseT, ErrorResponse> {

    public SimpleJsonEndpoint(
            String id,
            Function<RequestT, String> method,
            Function<RequestT, String> requestUrl,
            Function<RequestT,
                    Map<String, String>> queryParameters,
            Function<RequestT, Map<String, String>> headers,
            boolean hasRequestBody,
            JsonpDeserializer<ResponseT> responseParser
    ) {
        super(id, method, requestUrl, queryParameters, headers, hasRequestBody, responseParser);
    }
}