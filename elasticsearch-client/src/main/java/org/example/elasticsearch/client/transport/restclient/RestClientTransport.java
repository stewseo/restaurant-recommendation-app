package org.example.elasticsearch.client.transport.restclient;


import jakarta.json.stream.*;
import org.apache.http.entity.*;
import org.apache.http.message.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.transport.*;
import org.example.elasticsearch.client.util.*;
import org.example.lowlevel.restclient.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;


@SuppressWarnings({"RedundantThrows", "unused"})
public class RestClientTransport implements ElasticsearchTransport {

    static final ContentType JsonContentType;

    static {


        if (Version.VERSION == null) {
            JsonContentType = ContentType.APPLICATION_JSON;
        } else {
            JsonContentType = ContentType.create(
                    "application/vnd.elasticsearch+json",
                    new BasicNameValuePair("compatible-with", String.valueOf(Version.VERSION.major()))
            );
        }
    }

    private static class RequestFuture<T> extends CompletableFuture<T> {
        private volatile Cancellable cancellable;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancelled = super.cancel(mayInterruptIfRunning);
            if (cancelled && cancellable != null) {
                cancellable.cancel();
            }
            return cancelled;
        }
    }

    private final RestClient restClient;
    private final JsonpMapper mapper;
    private final RestClientOptions transportOptions;

    public RestClientTransport(RestClient restClient, JsonpMapper mapper, TransportOptions options) {
        this.restClient = restClient;
        this.mapper = mapper;
        this.transportOptions = options == null ? RestClientOptions.initialOptions() : RestClientOptions.of(options);
    }

    public RestClientTransport(RestClient restClient, JsonpMapper mapper) {
        this(restClient, mapper, null);
    }

    public RestClient restClient() {
        return this.restClient;
    }

    public RestClientTransport withRequestOptions(TransportOptions options) {
        return new RestClientTransport(this.restClient, this.mapper, options);
    }

    @Override
    public JsonpMapper jsonpMapper() {
        return mapper;
    }

    @Override
    public TransportOptions options() {
        return PrintUtils.println("Transport Options: " ,  transportOptions);
    }

    @Override
    public void close() throws IOException {
        this.restClient.close();
    }

    public <RequestT, ResponseT, ErrorT> ResponseT performRequest(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions options
    ) throws IOException {

        Request clientReq = prepareLowLevelRequest(request, endpoint, options); // build the client Request
        Response clientResp = performRequest(clientReq); // build the client Response
        return null; // get the high level response
    }

    private Response performRequest(Request clientReq) {
        return null;
    }


    private <RequestT> Request prepareLowLevelRequest(
            RequestT request,
            Endpoint<RequestT, ?, ?> endpoint,
            TransportOptions options
    ) {
        String method = endpoint.method(request);
        String path = endpoint.requestUrl(request);
        Map<String, String> params = endpoint.queryParameters(request);

        Request clientReq = new Request(method, path);

        clientReq.addParameters(params);

        if (endpoint.hasRequestBody()) {
            // Request has a body and must implement JsonpSerializable or NdJsonpSerializable
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (request instanceof NdJsonpSerializable) {
                writeNdJson((NdJsonpSerializable) request, baos);
            } else {
                JsonGenerator generator = mapper.jsonProvider().createGenerator(baos);
                mapper.serialize(request, generator);
                generator.close();
            }

            clientReq.setEntity(new ByteArrayEntity(baos.toByteArray(), JsonContentType));
        }
        // Request parameter intercepted by LLRC
        clientReq.addParameter("ignore", "400,401,403,404,405");
        return clientReq;
    }


    public <RequestT, ResponseT, ErrorT> CompletableFuture<ResponseT> performRequestAsync(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions options
    ) {
        Request clientReq = prepareLowLevelRequest(request, endpoint, options);

        RequestFuture<ResponseT> future = new RequestFuture<>();

        // Propagate required property checks to the thread that will decode the response
        boolean disableRequiredChecks = ApiTypeHelper.requiredPropertiesCheckDisabled();

        return future;
    }

    private void writeNdJson(NdJsonpSerializable value, ByteArrayOutputStream baos) {
        Iterator<?> values = value._serializables();
        while (values.hasNext()) {
            Object item = values.next();
            if (item instanceof NdJsonpSerializable && item != value) { // do not recurse on the item itself
                writeNdJson((NdJsonpSerializable) item, baos);
            } else {
                JsonGenerator generator = mapper.jsonProvider().createGenerator(baos);
                mapper.serialize(item, generator);
                generator.close();
                baos.write('\n');
            }
        }
    }

//        private <ResponseT, ErrorT> JsonObject getHighLevelResponse(
//                Response clientResp,
//                Endpoint<?, ResponseT, ErrorT> endpoint
//        ) throws IOException {
//
//            try {
//                int statusCode = clientResp.getStatusLine().getStatusCode();
//
//                if (statusCode == 200) {
//                    checkProductHeader(clientResp, endpoint);
//                }
//
//                if (endpoint.isError(statusCode)) {
//                    JsonpDeserializer<ErrorT> errorDeserializer = endpoint.errorDeserializer(statusCode);
//                    if (errorDeserializer == null) {
//                        throw new TransportException(
//                                "Request failed with status code '" + statusCode + "'",
//                                endpoint.id(), new ResponseException(clientResp)
//                        );
//                    }
//
//                    HttpEntity entity = clientResp.getEntity();
//                    if (entity == null) {
//                        throw new TransportException(
//                                "Expecting a response body, but none was sent",
//                                endpoint.id(), new ResponseException(clientResp)
//                        );
//                    }
//
//                    // We may have to replay it.
//                    entity = new BufferedHttpEntity(entity);
//
//                    try {
//                        InputStream content = entity.getContent();
//                        try (JsonParser parser = mapper.jsonProvider().createParser(content)) {
//                            ErrorT error = errorDeserializer.deserialize(parser, mapper);
//                            // TODO: have the endpoint provide the exception constructor
//                            throw new _Exception(endpoint.id(), (ErrorResponse) error);
//                        }
//                    } catch(MissingRequiredPropertyException errorEx) {
//                        // Could not decode exception, try the response type
//                        try {
//                            ResponseT response = decodeResponse(statusCode, entity, clientResp, endpoint);
//                            return response;
//                        } catch(Exception respEx) {
//                            // No better luck: throw the original error decoding exception
//                            throw new TransportException("Failed to decode error response", endpoint.id(), new ResponseException(clientResp));
//                        }
//                    }
//                } else {
//                    return decodeResponse(statusCode, clientResp.getEntity(), clientResp, endpoint);
//                }
//            } finally {
//                EntityUtils.consume(clientResp.getEntity());
//            }
//        }
//
//        private <ResponseT> ResponseT decodeResponse(
//        int statusCode,HttpEntity entity, Response clientResp, Endpoint<?, ResponseT, ?> endpoint
//    ) throws IOException {
//
//            if (endpoint instanceof BooleanEndpoint) {
//                BooleanEndpoint<?> bep = (BooleanEndpoint<?>) endpoint;
//
//                @SuppressWarnings("unchecked")
//                ResponseT response = (ResponseT) new BooleanResponse(bep.getResult(statusCode));
//                return response;
//
//            } else if (endpoint instanceof JsonEndpoint){
//                @SuppressWarnings("unchecked")
//                JsonEndpoint<?, ResponseT, ?> jsonEndpoint = (JsonEndpoint<?, ResponseT, ?>)endpoint;
//                // Successful response
//                ResponseT response = null;
//                JsonpDeserializer<ResponseT> responseParser = jsonEndpoint.responseDeserializer();
//                if (responseParser != null) {
//                    // Expecting a body
//                    if (entity == null) {
//                        throw new TransportException(
//                                "Expecting a response body, but none was sent",
//                                endpoint.id(), new ResponseException(clientResp)
//                        );
//                    }
//                    InputStream content = entity.getContent();
//                    try (JsonParser parser = mapper.jsonProvider().createParser(content)) {
//                        response = responseParser.deserialize(parser, mapper);
//                    };
//                }
//                return response;
//            } else {
//                throw new TransportException("Unhandled endpoint type: '" + endpoint.getClass().getName() + "'", endpoint.id());
//            }
//        }

    // Endpoints that (incorrectly) do not return the Elastic product header
    private static final Set<String> endpointsMissingProductHeader = new HashSet<>(Arrays.asList(
            "es/snapshot.create" // #74 / elastic/elasticsearch#82358
    ));

    private void checkProductHeader(Response clientResp, Endpoint<?, ?, ?> endpoint) throws IOException {
        String header = clientResp.getHeader("X-Elastic-Product");
        if (header == null) {
            if (endpointsMissingProductHeader.contains(endpoint.id())) {
                return;
            }
            throw new TransportException(
                    "Missing [X-Elastic-Product] header. Please check that you are connecting to an Elasticsearch "
                            + "instance, and that any networking filters are preserving that header.",
                    endpoint.id(),
                    new ResponseException(clientResp)
            );
        }

        if (!"Elasticsearch".equals(header)) {
            throw new TransportException("Invalid value '" + header + "' for 'X-Elastic-Product' header.",
                    endpoint.id(),
                    new ResponseException(clientResp)
            );
        }
    }
    }