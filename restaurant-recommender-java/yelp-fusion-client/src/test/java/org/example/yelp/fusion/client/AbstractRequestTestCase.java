package org.example.yelp.fusion.client;

import co.elastic.clients.elasticsearch.*;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.transport.*;

import jakarta.json.spi.*;
import jakarta.json.stream.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.json.jackson.*;
import org.example.elasticsearch.client.json.jsonb.*;
import org.example.elasticsearch.client.util.*;
import org.example.lowlevel.restclient.*;

import org.example.yelp.fusion.client.category.*;
import org.example.yelp.fusion.client.transport.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;

// build all requests before transporting
public abstract class AbstractRequestTestCase extends Assertions {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRequestTestCase.class);

    @BeforeAll
    static void beforeAll() throws IOException {

        setOfValidBusinessIds = new HashSet<>();
        restaurantsPerCategory = new HashMap<>();
        mapper = new JacksonJsonpMapper();

        initElasticsearchClient();

//        initElasticsearchClient();
        initYelpFusionClient();

    }


    private static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Authorization", "Bearer " + System.getenv("YELP_API_KEY"));
        COMMON_OPTIONS = builder.build();
    }

    static String scheme = "https";
    protected static JacksonJsonpMapper mapper;

    private static JsonbJsonpMapper jsonbJsonpMapper;
    protected static YelpFusionClient yelpClient;

    private static void initYelpFusionClient() throws IOException {

        String yelpFusionHost = "api.yelp.com";
        int port = 9243;
        String requestHeader = "Authorization";
        String bearerAndToken = String.format("Bearer %s", System.getenv("YELP_API_KEY"));

        // create rest client
        RestClientBuilder restClientBuilder = restClientBuilder(yelpFusionHost, port, scheme, requestHeader, bearerAndToken);

        try (RestClient restClient = restClientBuilder.build()) {

            // initialize Transport with a rest client and object mapper
            YelpRestTransport yelpTransport = new YelpRestTransport(
                    restClient,
                    mapper);

            yelpClient = new YelpFusionClient(yelpTransport);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static ElasticsearchClient esClient;

    private static void initElasticsearchClient() {

        String host = "my-deployment.es.us-west-1.aws.found.io";
        int port = 443;

        String apiKeyIdAndSecret = System.getenv("API_KEY_ID") + ":" + System.getenv("API_KEY_SECRET");


        String encodedApiKey = java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));

        PrintUtils.println(encodedApiKey);

        org.elasticsearch.client.RestClientBuilder builder = org.elasticsearch.client.RestClient.builder(
                new HttpHost(host, port, scheme));

        Header[] defaultHeaders = {new BasicHeader("Authorization", "ApiKey " + encodedApiKey)};

        builder.setDefaultHeaders(defaultHeaders);

        org.elasticsearch.client.RestClient restClient = builder.build();

        ElasticsearchTransport esTransport = new co.elastic.clients.transport.rest_client.RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());

        esClient = new ElasticsearchClient(esTransport);
    }


    private static RestClientBuilder restClientBuilder(String host, int port, String scheme) {
        return restClientBuilder(host, port, scheme, null, null);
    }

    private static RestClientBuilder restClientBuilder(String host, int port, String scheme, String requestHeader, String value) {

        // set the base URL: scheme, hostname and port.
        RestClientBuilder restBuilder = RestClient.builder(
                new HttpHost(host, port, scheme)); //Create HttpHost instance with the given scheme, hostname and port.

        // set a default header
        if (requestHeader != null && requestHeader.equalsIgnoreCase("authorization")) {
            Header[] defaultHeaders ={new BasicHeader(requestHeader, value)};

            restBuilder.setDefaultHeaders(defaultHeaders); //Set default headers
        }

        return restBuilder; // Create a new RestClient based on the provided configuration. All http handling is delegated to this RestClient
    }

    private static RestClient createESRestClient(String host, int port, String scheme, String authorizationType, String token) {
        //Build a client for the Yelp Fusion API
        RestClientBuilder restBuilder = RestClient.builder(
                new HttpHost(host, port, scheme)); //Create HttpHost instance with the given scheme, hostname and port.

        Header[] defaultHeaders = {new BasicHeader("Authorization",
                authorizationType + token)};


        restBuilder.setDefaultHeaders(defaultHeaders); //Set default headers

        return restBuilder.build(); // Create a new RestClient based on the provided configuration. All http handling is delegated to this RestClient
    }

    public static Set<String> setOfValidBusinessIds;

    public static Map<String, List<String>> restaurantsPerCategory;

    public static Set<String> setOfNewBusinessIds;


    private static Map<String, List<Categories>> parentCategoryMapping; // groups categories by parent names. restaurants, fitness

    private static Map<Long, Set<String>> restaurantsByPrice;



    public static <T, B extends ObjectBuilder<T>>B withJson (B builder, Reader json, ElasticsearchClient client){

        return withJson(builder, json, client._transport().jsonpMapper());
    }


    public static <T, B extends ObjectBuilder<T>>B withJson (B builder, Reader
            json, co.elastic.clients.json.JsonpMapper mapper){

        JsonpDeserializer<?> classDeser = JsonpMapperBase.findDeserializer(builder.getClass().getEnclosingClass());

        @SuppressWarnings("unchecked")
        co.elastic.clients.json.ObjectDeserializer<B> builderDeser = (co.elastic.clients.json.ObjectDeserializer<B>) DelegatingDeserializer.unwrap(classDeser);

        JsonParser parser = mapper.jsonProvider().createParser(json);
        builderDeser.deserialize(builder, parser, mapper, parser.next());
        return builder;
    }


    @Test
    public void testBuilderDeserializerHack () {

        CreateIndexRequest.Builder b = new CreateIndexRequest.Builder();

        // Required request parameter
        b.index("foo");

        // Read body from JSON
        withJson(b, new StringReader("{\"aliases\": {\"foo\": {\"is_hidden\": true}}}"), new JacksonJsonpMapper());

        CreateIndexRequest createIndexRequest = b.build();
    }

    private void withJson (CreateIndexRequest.Builder b, StringReader stringReader, JacksonJsonpMapper
            jacksonJsonpMapper){

    }

    protected <T > String toJson(T value) {
        return toJson(value, mapper);
    }

    public static <T > String toJson(T value, JsonpMapper mapper) {
        StringWriter sw = new StringWriter();
        JsonProvider provider = mapper.jsonProvider();
        JsonGenerator generator = provider.createGenerator(sw);
        mapper.serialize(value, generator);
        generator.close();
        return sw.toString();
    }

    public static <T > T fromJson(String json, Class < T > clazz, JsonpMapper mapper) {
        JsonParser parser = mapper.jsonProvider().createParser(new StringReader(json));
        return mapper.deserialize(parser, clazz);
    }

    protected <T > T fromJson(String json, Class < T > clazz) {
        return fromJson(json, clazz, mapper);
    }

    @SuppressWarnings("unchecked")
    protected <T > T checkJsonRoundtrip(T value, String expectedJson) {
        assertEquals(expectedJson, toJson(value));
        return fromJson(expectedJson, (Class<T>) value.getClass());
    }

    protected <T > T fromJson(String json, JsonpDeserializer < T > deserializer) {
        return fromJson(json, deserializer, mapper);
    }

    protected <T > T fromJson(String json, JsonpDeserializer < T > deserializer, JsonpMapper mapper) {
        JsonParser parser = mapper.jsonProvider().createParser(new StringReader(json));
        return deserializer.deserialize(parser, mapper);
    }

    public static void assertGetterType (Class < ? > expected, Class < ?>clazz, String name){
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

