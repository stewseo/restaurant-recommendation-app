package com.example.client.yelp_fusion.endpoints.businesses;

import jakarta.json.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;

// test fields from business/search and business/{id} endpoints
class BusinessTest {

    static String yelpFusionHost = "api.yelp.com/v3";
    static String protocol = "https";
    static String businessSearchEndpoint = "businesses/search";

    static StringBuilder requestUsingCurl;

    static  Map<String, String> requestParams;

    static int limit = 50;
    @BeforeAll
    static void beforeAll() {
        initRequestParams();
        initRequestUsingCurl();
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

    private static void initRequestParams() {
        requestParams = new HashMap<>();
        requestParams.put("location", "SF");
        requestParams.put("latitude", "37.751586275");
        requestParams.put("longitude", "-122.447721511");
        requestParams.put("term", "restaurants");
        requestParams.put("limit", String.valueOf(limit));
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
            String response = performYelpFusionRequest(request);
        }
    }

    List<String> restaurantCategories = List.of("sushi", "american bbq", "korean bbq", "Italian", "steak", "seafood");

    @Test
    void prepareRequestWithRestaurantCategory() throws IOException {
        requestParams.put("categories", "sushi,bbq,steak");
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

        performYelpFusionRequest(requestParams.toString());
    }


    private String performYelpFusionRequest(String request) throws IOException {
        System.out.println(request);
        Process process = Runtime.getRuntime().exec(request);

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        var jsonReader = Json.createReader(new StringReader(response));
        var messageAsJson = jsonReader.readObject();

        assertThat(messageAsJson.get("total").toString()).isLessThanOrEqualTo("1000");

        return response;
    }

    private String performElasticsearchRequest() throws IOException {
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