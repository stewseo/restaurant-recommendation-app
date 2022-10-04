package com.example.client.yelp_fusion.business;

import jakarta.json.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.elasticsearch.client.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BusinessSearchRequestWithOffsetTest extends AbstractYelpRequestTestCase {



    static String businessSearchEndpoint = "businesses/search";

    static int limit = 50;

    static RestClient httpClient;

    private static void initRequestParams() {
        requestParams = new HashMap<>();
        requestParams.put("location", "SF");
        requestParams.put("latitude", "37.751586275");
        requestParams.put("longitude", "-122.447721511");
        requestParams.put("term", "restaurants");
        requestParams.put("limit", String.valueOf(limit));
    }

    private static void initRequestUsingCurl() {

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

    @Test
    void prepareRequestWithOffset() throws IOException {
        int maxResults = 1000;

        requestParams.forEach((k,v) ->{
            if(requestUsingCurl.toString().endsWith("search")) {
                requestUsingCurl.append("?")
                        .append(k)
                        .append("=")
                        .append(v);

            }else {
                requestUsingCurl
                        .append("&")
                        .append(k)
                        .append("=")
                        .append(v);
            }
        });

        requestParams.put("offset", String.valueOf(limit));
        requestUsingCurl
                .append("&")
                .append("offset")
                .append("=");

        int length = requestUsingCurl.toString().length();
        List<Integer> cursors = List.of(50, 100, 150);
        int i = 0;

        // max results per page = 50
        for(int offset = 0; offset <= 150; offset += limit) {
            String request = requestUsingCurl.toString() + offset;
            performRequest(request);
        }
    }

    private JsonObject performRequest(String request) throws IOException {
        System.out.println(request);
        Process process = Runtime.getRuntime().exec(request);

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        var jsonReader = Json.createReader(new StringReader(response));
        var messageAsJson = jsonReader.readObject();
//        int totalResults = messageAsJson.getInt("total");

        assertThat(messageAsJson.getInt("total")).isLessThanOrEqualTo(1000);

        return messageAsJson;
    }

    private String performRequest() throws IOException {
        System.out.println(requestUsingCurl.toString());
        Process process = Runtime.getRuntime().exec(requestUsingCurl.toString());

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        System.out.println("" + response);
        return response;
    }

}
