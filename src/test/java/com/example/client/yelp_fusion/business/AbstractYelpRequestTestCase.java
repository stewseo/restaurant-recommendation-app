package com.example.client.yelp_fusion.business;

import co.elastic.clients.elasticsearch.*;
import co.elastic.clients.json.jackson.*;
import co.elastic.clients.transport.*;
import co.elastic.clients.transport.rest_client.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.elasticsearch.client.*;
import org.junit.jupiter.api.*;

import java.nio.charset.*;
import java.util.*;

public class AbstractYelpRequestTestCase {
    static String yelpFusionHost = "api.yelp.com/v3";
    static String protocol = "https";
    static String businessSearchEndpoint = "businesses/search";
    static  Map<String, String> requestParams;
    static StringBuilder requestUsingCurl;

    static int limit = 50;

    @BeforeAll
    static void beforeAll() {
        initRequestUsingCurl();
        initElasticsearchClient();
    }

    private static void initRequestParams() {
        requestParams = new HashMap<>();
        requestParams.put("location", "SF");
        requestParams.put("latitude", "37.751586275");
        requestParams.put("longitude", "-122.447721511");
        requestParams.put("term", "restaurants");
        requestParams.put("limit", String.valueOf(limit));
    }

    private static void initRequestUsingCurl() {
        initRequestParams();
        String yelpFusionBearerToken = System.getenv("YELP_API_KEY");
        requestUsingCurl = new StringBuilder(String.format("curl -H \"Authorization: Bearer %s\" ", yelpFusionBearerToken));
        String httpMethod = "GET";
        requestUsingCurl
                .append(httpMethod)
                .append(String.format(" %s://%s/%s",
                        protocol,
                        yelpFusionHost,
                        businessSearchEndpoint));
    }

    static ElasticsearchClient client;
    private static void initElasticsearchClient() {
        initRestClientTransport();
        client = new ElasticsearchClient(transport);
    }
    static ElasticsearchTransport transport;
    private static void initRestClientTransport() {
        initRestClient();
        transport = new RestClientTransport(httpClient, new JacksonJsonpMapper());
    }
    static RestClient httpClient;
    private static void initRestClient() {
        String apiKeyAuthentication = System.getenv("API_KEY_ID") + ":" + System.getenv("API_KEY_SECRET");

        String encodedApiKey = java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyAuthentication) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));

        RestClientBuilder builder = RestClient.builder(
                new HttpHost("my-deployment-158070.es.us-west-1.aws.found.io", 443, protocol));

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        "ApiKey " + encodedApiKey)};

        builder.setDefaultHeaders(defaultHeaders);
        httpClient = builder.build();
    }
}
