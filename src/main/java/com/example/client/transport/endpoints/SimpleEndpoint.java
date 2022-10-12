package com.example.client.transport.endpoints;

import com.example.client._types.*;
import com.example.client.json.*;
import com.example.client.transport.*;
import org.apache.http.client.utils.*;

import java.util.*;
import java.util.function.*;


public class SimpleEndpoint<RequestT, ResponseT> implements JsonEndpoint<RequestT, ResponseT, ErrorResponse> {

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
    private final JsonpDeserializer<ResponseT> responseParser;

    public SimpleEndpoint(
            String id,
            Function<RequestT, String> method,
            Function<RequestT, String> requestUrl,
            Function<RequestT, Map<String, String>> queryParameters,
            Function<RequestT, Map<String, String>> headers,
            boolean hasRequestBody,
            JsonpDeserializer<ResponseT> responseParser
    ) {
        this.id = id;
        this.method = method;
        this.requestUrl = requestUrl;
        this.queryParameters = queryParameters;
        this.headers = headers;
        this.hasRequestBody = hasRequestBody;
        this.responseParser = responseParser;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String method(RequestT request) { // an instance of GetPipelineRequest's toString method prints it's pipeline-id class field
        String requestMethod = this.method.apply(request);

        return requestMethod; // returns GET, POST, PUT, DELETE
    }

    @Override
    public String requestUrl(RequestT request) {
        String requestUrl = this.requestUrl.apply(request);

        return requestUrl; // returns /_ingest/pipeline
    }

    @Override
    public Map<String, String> queryParameters(RequestT request) {
        Map<String, String> queryParameters = this.queryParameters.apply(request);

        return queryParameters; // returns a Map<String, String> of query parameters, initialized to empty
    }

    @Override
    public Map<String, String> headers(RequestT request) {
        Map<String, String> headers = this.headers.apply(request);

        return headers; // returns a Collections.Map.Entry<String, String>> of query parameters, initialized to empty
    }

    @Override
    public boolean hasRequestBody() {
        return this.hasRequestBody;
    }

    @Override
    public JsonpDeserializer<ResponseT> responseDeserializer() {
        return this.responseParser;
    }

    // ES-specific
    @Override
    public boolean isError(int statusCode) {
        return statusCode >= 400;
    }

    @Override
    public JsonpDeserializer<ErrorResponse> errorDeserializer(int statusCode) {
        return null;
    }


    public <NewResponseT> SimpleEndpoint<RequestT, NewResponseT> withResponseDeserializer(
            JsonpDeserializer<NewResponseT> newResponseParser
    ) {
        return new SimpleEndpoint<>(
                id,
                method,
                requestUrl,
                queryParameters,
                headers,
                hasRequestBody,
                newResponseParser
        );
    }

    public static RuntimeException noPathTemplateFound(String what) {
        return new RuntimeException("Could not find a request " + what + " with this set of properties. " +
                "Please check the API documentation, or raise an issue if this should be a valid request.");
    }

    public static void pathEncode(String src, StringBuilder dest) {
        dest.append(URLEncodedUtils.formatSegments(src).substring(1));
    }
}