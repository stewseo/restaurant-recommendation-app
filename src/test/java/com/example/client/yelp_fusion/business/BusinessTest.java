package com.example.client.yelp_fusion.business;

import org.apache.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

class BusinessTest {

    static String yelpFusionHost = "api.yelp.com/v3";
    static String protocol = "https";
    static String businessSearchEndpoint = "businesses/search";

    static StringBuilder requestUsingCurl;

    static  Map<String, String> requestParams;

    static int limit = 50;
    @BeforeAll
    static void beforeAll() {
        requestParams = new HashMap<>();
        requestParams.put("location", "SF");
        requestParams.put("latitude", "37.751586275");
        requestParams.put("longitude", "-122.447721511");
        requestParams.put("term", "restaurants");
        requestParams.put("limit", String.valueOf(limit));

        requestUsingCurl = new StringBuilder(String.format("curl -H \"Authorization: Bearer %s\" ", System.getenv("YELP_API_KEY")));
        String requestMethod = "GET";
        requestUsingCurl.append(requestMethod).append(String.format(" %s://%s/%s", protocol, yelpFusionHost, businessSearchEndpoint));

    }

    @Test
    void businessSearchTest() throws IOException {

        requestParams.forEach((k,v) ->{
            if(!requestUsingCurl.toString().contains("?")) {
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

        Process process = Runtime.getRuntime().exec(requestUsingCurl.toString());

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    @Test
    void offsetIterationTest() throws IOException {

        requestParams.forEach((k,v) ->{
            if(!requestUsingCurl.toString().contains("?")) {
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

        int maxResults = 1000;


        int length = requestUsingCurl.toString().length();
        for(int offset = limit; offset <= 100; offset += 50) {
            if(!requestUsingCurl.toString().contains("offset")){
                requestUsingCurl.append("&").append("offset").append("=").append(limit);
            }
            // max results per page = 50
            requestUsingCurl.replace(length-2, length, String.valueOf(offset));
            Process process = Runtime.getRuntime().exec(requestUsingCurl.toString());

            InputStream inputStream = process.getInputStream();

            String response = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            System.out.println("" + response);
        }
    }
}