package com.example.client.yelp_fusion;

import co.elastic.clients.elasticsearch.*;
import co.elastic.clients.transport.*;
import com.example.client.json.jackson.*;
import com.example.client.transport.restclient.*;
import com.example.ll_restclient.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.io.*;
import java.nio.charset.*;

// build all requests before transporting
public abstract class AbstractRequestTestCase {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRequestTestCase.class);

    @BeforeAll
    static void beforeAll() throws IOException {
        initYelpFusionClient();
//        initElasticsearchClient();
    }
    static String scheme = "https";
    private static JacksonJsonpMapper mapper;
    protected static YelpFusionClient yelpClient;
    private static void initYelpFusionClient() throws IOException {
        mapper = new JacksonJsonpMapper();
        String yelpFusionHost = "api.yelp.com";
        int port = 9243;
        String requestHeader = "Authorization";
        String bearerAndToken = String.format("Bearer %s", System.getenv("YELP_API_KEY"));

        // create rest client
        com.example.ll_restclient.RestClientBuilder restClientBuilder =
                createRestClientBuilder(yelpFusionHost, port, scheme, requestHeader, bearerAndToken);

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

        String host = "my-deployment-170edf.es.us-west-1.aws.found.io";
        int port = 443;

        String apiKeyIdAndSecret = System.getenv("API_KEY_ID") + ":" + System.getenv("API_KEY_SECRET");

        String encodedApiKey = java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));


        org.elasticsearch.client.RestClientBuilder builder =  org.elasticsearch.client.RestClient.builder(
                new HttpHost(host, port, scheme));

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        "ApiKey " + encodedApiKey)};

        builder.setDefaultHeaders(defaultHeaders);

        org.elasticsearch.client.RestClient restClient = builder.build();

        ElasticsearchTransport esTransport = new co.elastic.clients.transport.rest_client.RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());

        esClient = new ElasticsearchClient(esTransport);
    }

    private static  com.example.ll_restclient.RestClientBuilder createRestClientBuilder(String host, int port, String scheme) {
        return createRestClientBuilder(host, port, scheme, null, null);
    }
    private static  com.example.ll_restclient.RestClientBuilder createRestClientBuilder(String host, int port, String scheme, String requestHeader, String value) {

        // set the base URL: scheme, hostname and port.
        com.example.ll_restclient.RestClientBuilder restBuilder =  com.example.ll_restclient.RestClient.builder(
                new HttpHost(host, port, scheme)); //Create HttpHost instance with the given scheme, hostname and port.

        // set a default header
        if(requestHeader != null && requestHeader.equalsIgnoreCase("authorization")) {
            Header[] defaultHeaders =
                    new Header[]{new BasicHeader(requestHeader, value)};

            restBuilder.setDefaultHeaders(defaultHeaders); //Set default headers
        }

        return restBuilder; // Create a new RestClient based on the provided configuration. All http handling is delegated to this RestClient
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


}
