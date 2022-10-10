package com.example.client.yelp_fusion.endpoints.businesses;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.json.jackson.*;
import com.example.client.json.*;
import com.example.client.yelp_fusion.category.*;
import com.example.client.yelp_fusion.endpoints.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

public class YelpFusionCategoriesTests extends AbstractRequestTestCase {

    @Test
    void fromJsonTest() throws IOException {
        FileReader file = new FileReader(new File("C:\\Users\\seost\\repositories\\yelp-fusion-java\\src\\test\\resources\\categories.json"));

        List<Category> categories = Arrays.asList(new JacksonJsonpMapper().objectMapper().readValue(file, Category[].class));
//
//
//        PrintUtils.titleCyan(categories.size());

        IndexRequest<JsonData> req;

        req = IndexRequest.of(b -> b
                .index("yelp-categories")
                .withJson(file)
        );

        esClient.index(req);

    }
}
