package com.example.client.transport.endpoints;


import com.example.client._types.*;
import com.example.client.json.*;
import com.example.client.transport.*;
import org.apache.http.client.utils.*;

import java.util.*;
import java.util.function.*;

// field names to Map params
public class TestBusinessSearchEndpoint <RequestT, ResponseT> implements JsonEndpoint<RequestT, ResponseT, ErrorResponse> {
    // field names to Map paramsSimpleEndpoint<RequestT, ResponseT> implements JsonEndpoint<RequestT, ResponseT, ErrorResponse>
    private static final Function<?, Map<String, String>> EMPTY_MAP = x -> Collections.emptyMap();

    @SuppressWarnings("unchecked")
    public static <T> Function<T, Map<String, String>> emptyMap() {
        return (Function<T, Map<String, String>>) EMPTY_MAP;
    }

    private final String id;
    private final Function<RequestT, String> method;
    private final Function<RequestT, String> requestUrl;
    private final Function<RequestT, Map<String, String>> queryParameters;
    private final Function<RequestT, Map<String, String>> headers;
    private final boolean hasRequestBody;

    public TestBusinessSearchEndpoint(
            String id,
            Function<RequestT, String> method,
            Function<RequestT, String> requestUrl,
            Function<RequestT, Map<String, String>> queryParameters,
            Function<RequestT, Map<String, String>> headers,
            boolean hasRequestBody
    ) {
        this.id = id;
        this.method = method;
        this.requestUrl = requestUrl;
        this.queryParameters = queryParameters;
        this.headers = headers;
        this.hasRequestBody = hasRequestBody;
    }
    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String method(RequestT request) {
        return this.method.apply(request);
    }

    @Override
    public String requestUrl(RequestT request) {
        return this.requestUrl.apply(request);
    }

    @Override
    public Map<String, String> queryParameters(RequestT request) {
        return this.queryParameters.apply(request);
    }

    @Override
    public Map<String, String> headers(RequestT request) {
        return this.headers.apply(request);
    }

    @Override
    public boolean hasRequestBody() {
        return this.hasRequestBody;
    }

    @Override
    public boolean isError(int statusCode) {
        return statusCode >= 400;
    }

    @Override
    public JsonpDeserializer<ErrorResponse> errorDeserializer(int statusCode) {
        return ErrorResponse._DESERIALIZER;
    }

    public <NewResponseT> TestBusinessSearchEndpoint<RequestT, NewResponseT> withResponseDeserializer(
            JsonpDeserializer<NewResponseT> newResponseParser
    ) {
        return new TestBusinessSearchEndpoint<>(
                id,
                method,
                requestUrl,
                queryParameters,
                headers,
                hasRequestBody
        );
    }

    public static RuntimeException noPathTemplateFound(String what) {
        return new RuntimeException("Could not find a request " + what + " with this set of properties. " +
                "Please check the API documentation, or raise an issue if this should be a valid request.");
    }

    public static void pathEncode(String src, StringBuilder dest) {
        dest.append(URLEncodedUtils.formatSegments(src).substring(1));
    }

    @Override
    public JsonpDeserializer<ResponseT> responseDeserializer() {
        return null;
    }
}
