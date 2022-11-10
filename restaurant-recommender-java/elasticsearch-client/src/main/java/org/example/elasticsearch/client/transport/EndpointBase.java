package org.example.elasticsearch.client.transport;

import org.apache.http.client.utils.*;
import org.example.elasticsearch.client._types.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.transport.endpoints.*;

import java.util.*;
import java.util.function.*;

public class EndpointBase<RequestT, ResponseT> implements Endpoint<RequestT, ResponseT, ErrorResponse> {

    private static final Function<?, Map<String, String>> EMPTY_MAP = x -> Collections.emptyMap();

    /**
     * Returns a function that always returns an empty String to String map. Useful to avoid creating lots of
     * duplicate lambdas in endpoints that don't have headers or parameters.
     */
    @SuppressWarnings("unchecked")
    public static <T> Function<T, Map<String, String>> emptyMap() {
        return (Function<T, Map<String, String>>) EMPTY_MAP;
    }

    protected final String id;
    protected final Function<RequestT, String> method;
    protected final Function<RequestT, String> requestUrl;
    protected final Function<RequestT, Map<String, String>> queryParameters;
    protected final Function<RequestT, Map<String, String>> headers;
    protected final boolean hasRequestBody;

    public EndpointBase(
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

    // ES-specific
    @Override
    public boolean isError(int statusCode) {
        return statusCode >= 400;
    }

    @Override
    public JsonpDeserializer<ErrorResponse> errorDeserializer(int statusCode) {
        return ErrorResponse._DESERIALIZER;
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
        // TODO: avoid dependency on HttpClient here (and use something more efficient)
        dest.append(URLEncodedUtils.formatSegments(src).substring(1));
    }
}
