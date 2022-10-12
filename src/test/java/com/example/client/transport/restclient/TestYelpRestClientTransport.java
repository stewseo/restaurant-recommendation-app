package com.example.client.transport.restclient;

import co.elastic.clients.json.jackson.*;
import com.example.client.json.*;
import com.example.client.transport.Version;
import com.example.client.transport.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.businesses.*;
import com.example.client.yelp_fusion.model.*;
import com.fasterxml.jackson.core.*;
import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.message.*;
import org.apache.http.nio.client.*;
import org.apache.http.util.*;
import org.elasticsearch.client.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;


// Test class implementing a subclass of the Transport interface
// prepares an object of type RequestT
@SuppressWarnings({"unused", "RedundantThrows", "UnnecessaryLocalVariable", "UnnecessaryToStringCall"})
public class TestYelpRestClientTransport implements YelpFusionTransport {
    static final ContentType JsonContentType;

    static CloseableHttpAsyncClient client;

    static {

        if (Version.VERSION == null) {
            JsonContentType = ContentType.APPLICATION_JSON;
        } else {

            JsonContentType = ContentType.create(  //Creates a new instance of ContentType with the given parameters.
                    "application/vnd.elasticsearch+json",    //mimeType – MIME type. It may not be null or empty. It may not contain characters <">, <;>, <,> reserved by the HTTP specification. params – parameters.
                    new BasicNameValuePair("compatible-with",  // A name-value pair parameter used as an element of HTTP messages.
                            String.valueOf(Version.VERSION.major())) // Returns: content type
            );
        }
    }


    @Override
    public void close() throws IOException {

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

    private final org.elasticsearch.client.RestClient restClient;
    private final co.elastic.clients.json.JsonpMapper mapper;
    private final TestYelpRestClientOptions transportOptions;

    public TestYelpRestClientTransport(
            org.elasticsearch.client.RestClient restClient,
            co.elastic.clients.json.JsonpMapper mapper,
            TransportOptions options) throws IOException { // TransportOptions
        this.restClient = restClient;
        this.mapper = mapper;
        this.transportOptions = options == null ?
                TestYelpRestClientOptions.initialOptions() : TestYelpRestClientOptions.of(options);
    }
    public TestYelpRestClientTransport(
            org.elasticsearch.client.RestClient restClient,
            co.elastic.clients.json.JsonpMapper mapper) throws IOException {
        this(restClient, mapper, null);
    }


    public org.elasticsearch.client.RestClient restClient() {
        return restClient;
    }


    public <RequestT, ResponseT, ErrorT> BusinessEndpointResponse performRequest(
            RequestT request,
            Endpoint<RequestT, ResponseT, ErrorT> endpoint,
            TransportOptions transportOptions) throws IOException, URISyntaxException {

        BusinessEndpointResponse businessEndpointResponse = prepareYelpFusionRequest(request, endpoint, transportOptions);

//        Request clientReq = prepareRequest(request, endpoint, transportOptions); // initialize a Request clientReq
//
//        com.example.client._types.Response clientResponse = performRequest(clientReq);

//      context = restClient.createRequestContext(tuple.nodes.next(), tuple.authCache);
//      httpResponse =  client.execute(context.requestProducer, context.asyncResponseConsumer, context.context, null).get();
//      ResponseOrResponseException responseOrResponseException = convertResponse(request, context.node, httpResponse);

        return businessEndpointResponse;
    }


    public <RequestT> BusinessEndpointResponse prepareYelpFusionRequest(
            RequestT request,
            Endpoint<RequestT, ?, ?> endpoint,
            com.example.client.transport.TransportOptions options
    ) throws IOException {

        RequestOptions restOptions = options == null ?
                this.transportOptions.restClientRequestOptions() :
                TestYelpRestClientOptions.of(options).restClientRequestOptions();

        StringBuilder sb = new StringBuilder("curl ");

        restOptions.getHeaders().forEach(k -> {

                    if (k.getName().contains("Authorization")) {
                        sb
                                .append("-H \"")
                                .append(k.getName())
                                .append(": ")
                                .append(k.getValue())
                                .append("\" ");
                    }
                }
        );

        HttpHost httpHost = restClient.getNodes().get(0).getHost();
        String hostName = httpHost.getHostName();
        String schemeName = httpHost.getSchemeName();

        sb.append(schemeName) // https
                .append("://")
                .append(hostName); // api.yelp.com

        // get path parameters
        String method = endpoint.method(request);

        String path = endpoint.requestUrl(request);
        sb.append(path);

        Map<String, String> params = endpoint.queryParameters(request);

        String delim = "?";
        for (Map.Entry<String, String> param : params.entrySet()) {
            sb.append(delim);

            delim = "&";
            String key = param.getKey();
            String value = param.getValue();

            if(value.startsWith("[")) {
                String formatted = value.replace("[", "").replace("]", "");
                sb.append(key).append("=").append(formatted);
            }
            else {
                sb.append(key).append("=").append(value);
            }
        }

        return executeRequestUsingCurl(sb.toString());
    }

    public <RequestT> Request prepareRequest(
            RequestT request,
            Endpoint<RequestT, ?, ?> endpoint,
            com.example.client.transport.TransportOptions options
    ) throws IOException {

        // curl method scheme://host:port/path
        String method = endpoint.method(request);

        String path = endpoint.requestUrl(request);

        Request clientReq = new Request(method, path);

        RequestOptions restOptions = options == null ?
                this.transportOptions.restClientRequestOptions() :
                TestYelpRestClientOptions.of(options).restClientRequestOptions();

        if (restOptions != null) {
            clientReq.setOptions(restOptions);
        }

        Map<String, String> params = endpoint.queryParameters(request);

        clientReq.addParameters(params);

        return clientReq;
    }

    // TODO: use Apache HttpComponents: HttpClient, HttpResponse
    //  return ResponseT
    public BusinessEndpointResponse executeRequestUsingCurl(String request) throws IOException {
        System.out.println(request);
        Process process = Runtime.getRuntime().exec(request);

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        BusinessEndpointResponse businessSearchResponse =  new JacksonJsonpMapper().objectMapper().readValue(response, BusinessEndpointResponse.class);

        return businessSearchResponse;
    }


    private com.example.client._types.Response performRequest(Request clientReq) throws IOException {
        String hostString = restClient.getNodes().get(0).getHost().getHostName();
        String pathString = "v3/businesses/search";
        String queryString = "location=sf&term=restaurants";
//        HttpRequest request = new HttpGet(q);
//        request.setHeader("Authentication", "Authorization: Bearer " + System.getenv("YELP_API_KEY"));

        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpAsyncClient httpAsyncClient = restClient.getHttpClient();

        HttpHost host = restClient.getNodes().get(0).getHost();

        HttpOptions httpoptions = new HttpOptions(queryString);

        httpoptions.addHeader("Authentication", "Authorization: Bearer " + System.getenv("YELP_API_KEY"));
        HttpResponse response = httpclient.execute(httpoptions);

        assertEquals(200, response.getStatusLine().getStatusCode());

        RequestLine requestLine = httpoptions.getRequestLine();    // The Request-Line begins with a method token, followed by the Request-URI and the protocol version, and ending with CRLF

        com.example.client._types.Response clientRes = new com.example.client._types.Response(requestLine, host, response);

        String stringResponse = EntityUtils.toString(response.getEntity());  // now you have the response as String, which you can convert to a JSONObject or do other stuff

        return clientRes;
    }

    // decode response, use the Response object:
    // RequestLine: field RequestLine
    // HttpHost: field HttpHost
    // HttpResponse: class InternalRequest
    private <ResponseT, ErrorT> ResponseT getHighLevelResponse(
            com.example.client._types.Response clientResp,
            Endpoint<?, ResponseT, ErrorT> endpoint
    ) throws IOException {
        ResponseT response;
        try {

            int statusCode = clientResp.getStatusLine().getStatusCode();

            HttpEntity entity = clientResp.getEntity();
            if (entity == null) {
                throw new co.elastic.clients.transport.TransportException(
                        "Expecting a response body, but none was sent",
                        endpoint.id(), new com.example.client._types.ResponseException(clientResp)
                );
            }

            co.elastic.clients.transport.JsonEndpoint<?, ResponseT, ?> jsonEndpoint =
                    (co.elastic.clients.transport.JsonEndpoint<?, ResponseT, ?>) endpoint; // parameter endpoint

            com.example.client.transport.JsonEndpoint<?, ResponseT, ?> jsonEndpoint_ =
                    (com.example.client.transport.JsonEndpoint<?, ResponseT, ?>) endpoint; // parameter endpoint

            entity = new BufferedHttpEntity(entity);

            InputStream content = entity.getContent(); // Returns a content stream of the entity.

            co.elastic.clients.json.JsonpDeserializer<ResponseT> responseParser = jsonEndpoint.responseDeserializer(); // The entity parser for the response body.

            try (JsonParser parser = mapper.jsonProvider().createParser(content)) { // use JSON-P provider to create json parser. parse the stream of the entity. Repeatable entities are expected to create a new instance of
                response = responseParser.deserialize(parser, mapper); // Deserialize a value. The value starts at the next state in the JSON stream.
            }


        } catch (IOException | UnsupportedOperationException e) {
            throw new RuntimeException(e);
        }
        EntityUtils.consume(clientResp.getEntity()); // Ensures that the entity content is fully consumed and the content stream, if exists, is closed.
        return response;
    }


    private void writeNdJson(NdJsonpSerializable value, ByteArrayOutputStream baos) {
        Iterator<?> values = value._serializables();
        while(values.hasNext()) {
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

    @Override
    public co.elastic.clients.json.JsonpMapper jsonpMapper() {
        return this.mapper;
    }

    @Override
    public TransportOptions options() {
        return transportOptions;
    }


    @Override
    public <RequestT, ResponseT, ErrorT> CompletableFuture<ResponseT> performRequestAsync(RequestT request, Endpoint<RequestT, ResponseT, ErrorT> endpoint, TransportOptions options) {
        return null;
    }

    private <ResponseT> ResponseT decodeResponse(HttpEntity entity, com.example.client._types.Response clientResp, Endpoint<?,ResponseT,?> endpoint) throws IOException {

        co.elastic.clients.transport.
                JsonEndpoint<?, ResponseT, ?> jsonEndpoint =
                (co.elastic.clients.transport.JsonEndpoint<?, ResponseT, ?>) endpoint;

        ResponseT response = null;
        co.elastic.clients.json.JsonpDeserializer<ResponseT> responseParser = //An endpoint with a JSON response body.
                jsonEndpoint.responseDeserializer(); // The entity parser for the response body.

        if (responseParser != null) {

            InputStream content = entity.getContent();

            try (JsonParser parser = mapper.jsonProvider().createParser(content)) { // Creates a JSON parser from the specified byte stream.
                response = responseParser.deserialize(parser, mapper); //Deserialize a value. The value starts at the next state in the JSON stream.
            };
        }
        return response;
    }

    public String toString() {
        System.out.println("test");
        return "test";
    }

    //deserialize JSON content from given JSON content String.
    private TestBusinessEndpointResponse deserialize(JsonObject jsonObject) throws JsonProcessingException {

        JsonValue businesses =  jsonObject.get("businesses");


        TestBusinessEndpointResponse businessResponse = PrintUtils.println(new JacksonJsonpMapper().objectMapper().readValue(jsonObject.toString(), TestBusinessEndpointResponse.class));

        Business[] business = new JacksonJsonpMapper().objectMapper().readValue(businesses.toString(),  Business[].class);

        return new TestBusinessEndpointResponse();
    }

}