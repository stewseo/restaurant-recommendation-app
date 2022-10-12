package com.example.client.yelp_fusion.endpoints;

import co.elastic.clients.elasticsearch.*;
import co.elastic.clients.json.jackson.*;
import co.elastic.clients.transport.*;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.client.json.*;
import com.example.client.transport.restclient.*;
import com.example.client.yelp_fusion.*;
import jakarta.json.spi.*;
import jakarta.json.stream.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.elasticsearch.client.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;

import static org.junit.jupiter.api.Assertions.*;

// build all requests before transporting
public abstract class AbstractRequestTestCase {

    @BeforeAll
    static void beforeAll() throws IOException {

        initYelpFusionClient();
        initElasticsearchClient();
    }
    private static final RequestOptions AUTHORIZATION_HEADER;
    static {
        // Holds parts of the request that should be shared between many requests in the same application.
        // Create a singleton instance and share it between all requests:
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Authorization", "Bearer " + System.getenv("YELP_API_KEY"));
        AUTHORIZATION_HEADER = builder.build();

    }

    static String protocol = "https";
    private static co.elastic.clients.json.jackson.JacksonJsonpMapper mapper;

    protected static TestYelpClient yelpClient;

    private static void initYelpFusionClient() throws IOException {

        String yelpFusionHost = "api.yelp.com/v3";
        int port = 9243;
        RestClient restClient = createRestClientWithDefaultHeaders(yelpFusionHost, port, protocol, "Bearer ", System.getenv("YELP_API_KEY"));
        //Build a client for the Yelp Fusion API


        TestYelpRestClientTransport yelpTransport = new TestYelpRestClientTransport(
                restClient, // Client that connects to an Elasticsearch cluster through HTTP.
                mapper); // A partial implementation of JSONP's SPI on top of Jackson.

        yelpClient = new TestYelpClient(yelpTransport, new TestYelpRestClientOptions(AUTHORIZATION_HEADER));
    }

    public static ElasticsearchClient esClient;

    private static void initElasticsearchClient() {

        String host = "my-deployment-170edf.es.us-west-1.aws.found.io";
        int port = 443;

        String apiKeyIdAndSecret = System.getenv("API_KEY_ID") + ":" + System.getenv("API_KEY_SECRET");

        String encodedApiKey = java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));


        RestClientBuilder builder = RestClient.builder(
                new HttpHost(host, port, protocol));

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        "ApiKey " + encodedApiKey)};

        builder.setDefaultHeaders(defaultHeaders);

        RestClient restClient = builder.build();

        ElasticsearchTransport esTransport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        esClient = new ElasticsearchClient(esTransport);
    }

    private static RestClient createESRestClient(String host, int port, String scheme, String authorizationType, String token) {
        //Build a client for the Yelp Fusion API
        RestClientBuilder restBuilder = RestClient.builder(
                new HttpHost(host, port, scheme)); //Create HttpHost instance with the given scheme, hostname and port.

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        authorizationType + token)};


        restBuilder.setDefaultHeaders(defaultHeaders); //Set default headers

        return restBuilder.build(); // Create a new RestClient based on the provided configuration. All http handling is delegated to this RestClient
    }

    private static RestClient createRestClientWithDefaultHeaders(String host, int port, String scheme, String authorizationType, String token) {
        //Build a client for the Yelp Fusion API
        RestClientBuilder restBuilder = RestClient.builder(
                new HttpHost(host, port, scheme)); //Create HttpHost instance with the given scheme, hostname and port.

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        authorizationType + token)};


        restBuilder.setDefaultHeaders(defaultHeaders); //Set default headers

       return restBuilder.build(); // Create a new RestClient based on the provided configuration. All http handling is delegated to this RestClient
    }

    protected <T> String toJson(T value) {
        return toJson(value, mapper);
    }

    public static <T> String toJson(T value, co.elastic.clients.json.jackson.JacksonJsonpMapper mapper) {
        StringWriter sw = new StringWriter();
        JsonProvider provider = mapper.jsonProvider();
        JsonGenerator generator = provider.createGenerator(sw);
        mapper.serialize(value, generator);
        generator.close();
        return sw.toString();
    }

    public static <T> T fromJson(String json, Class<T> clazz, co.elastic.clients.json.jackson.JacksonJsonpMapper mapper) {
        JsonParser parser = mapper.jsonProvider().createParser(new StringReader(json));
        return mapper.deserialize(parser, clazz);
    }


    protected <T> T fromJson(String json, JsonpDeserializer<T> deserializer, JsonpMapper mapper) {
        JsonParser parser = mapper.jsonProvider().createParser(new StringReader(json));
        return deserializer.deserialize(parser, mapper);
    }


    public static void assertGetterType(Class<?> expected, Class<?> clazz, String name) {
        Method method;
        try {
            method = clazz.getMethod(name);
        } catch (NoSuchMethodException e) {
            fail("Getter '" + clazz.getName() + "." + name + "' doesn't exist");
            return;
        }

        assertSame(expected, method.getReturnType());
    }

}
