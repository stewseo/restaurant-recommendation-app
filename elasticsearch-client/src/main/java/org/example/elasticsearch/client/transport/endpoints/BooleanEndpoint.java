package org.example.elasticsearch.client.transport.endpoints;


import org.example.elasticsearch.client.json.*;
import java.util.*;
import java.util.function.*;

public class BooleanEndpoint<RequestT> extends SimpleEndpoint<RequestT, BooleanResponse> {

    public BooleanEndpoint(
            String id,
            Function<RequestT, String> method,
            Function<RequestT, String> requestUrl,
            Function<RequestT,
                    Map<String, String>> queryParameters,
            Function<RequestT, Map<String, String>> headers,
            boolean hasRequestBody,
            JsonpDeserializer<?> responseParser // always null
    ) {
        super(id, method, requestUrl, queryParameters, headers, hasRequestBody, null);
    }

    @Override
    public boolean isError(int statusCode) {
        return statusCode >= 500;
    }

    public boolean getResult(int statusCode) {
        return statusCode < 400;
    }
}
