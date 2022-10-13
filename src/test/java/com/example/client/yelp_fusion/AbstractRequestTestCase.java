package com.example.client.yelp_fusion;

import co.elastic.clients.elasticsearch.*;
import co.elastic.clients.transport.*;
import com.example.client.json.jackson.*;
import com.example.client.transport.restclient.*;
import com.example.ll_restclient.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.*;

// build all requests before transporting
public abstract class AbstractRequestTestCase {

    @BeforeAll
    static void beforeAll() throws IOException {

        initYelpFusionClient();
        initElasticsearchClient();
    }
//    private static final com.example.client._types.RequestOptions AUTHORIZATION_HEADER;
    static {
//         Holds parts of the request that should be shared between many requests in the same application.
//         Create a singleton instance and share it between all requests:
//        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + System.getenv("YELP_API_KEY"));
//        AUTHORIZATION_HEADER = builder.build();
    }

    static String protocol = "https";
    private static JacksonJsonpMapper mapper;

    protected static YelpFusionClient yelpClient;

    private static void initYelpFusionClient() throws IOException {

        String yelpFusionHost = "api.yelp.com/v3";
        int port = 9243;
        com.example.ll_restclient.RestClient restClient = createRestClientWithDefaultHeaders(yelpFusionHost, port, protocol, "Bearer ", System.getenv("YELP_API_KEY"));
        //Build a client for the Yelp Fusion API


        YelpRestTransport yelpTransport = new YelpRestTransport(
                restClient, // Client that connects to an Elasticsearch cluster through HTTP.
                mapper); // A partial implementation of JSONP's SPI on top of Jackson.

        yelpClient = new YelpFusionClient(yelpTransport);
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
                new HttpHost(host, port, protocol));

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        "ApiKey " + encodedApiKey)};

        builder.setDefaultHeaders(defaultHeaders);

        org.elasticsearch.client.RestClient restClient = builder.build();

        ElasticsearchTransport esTransport = new co.elastic.clients.transport.rest_client.RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());

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

    private static  com.example.ll_restclient.RestClient createRestClientWithDefaultHeaders(String host, int port, String scheme, String authorizationType, String token) {
        //Build a client for the Yelp Fusion API
        com.example.ll_restclient.RestClientBuilder restBuilder =  com.example.ll_restclient.RestClient.builder(
                new HttpHost(host, port, scheme)); //Create HttpHost instance with the given scheme, hostname and port.

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        authorizationType + token)};


        restBuilder.setDefaultHeaders(defaultHeaders); //Set default headers

       return restBuilder.build(); // Create a new RestClient based on the provided configuration. All http handling is delegated to this RestClient
    }


}
