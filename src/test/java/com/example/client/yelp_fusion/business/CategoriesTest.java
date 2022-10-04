package com.example.client.yelp_fusion.business;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import com.example.client.yelp_fusion.category.*;
import com.fasterxml.jackson.databind.*;
import jakarta.json.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static com.example.client.transport.Transport.logger;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CategoriesTest extends AbstractYelpRequestTestCase {

    ObjectMapper mapper;

    @Test
    void indexAllYelpCategoriesTest() throws IOException {
        mapper = new ObjectMapper();

        JsonReader jsonReader = Json.createReader(new FileReader("src\\test\\resources\\categories.json"));
        JsonObject jsonObject = jsonReader.readObject();

        Businesses businesses = mapper.readValue(jsonObject.toString(), Businesses.class);
        IndexResponse response = client.index(i -> i // Creates or updates a document in an index.
                .index("restaurants-index-000001") // restaurants-index-000001 alias
                .id("categories-") // Document ID
                .document(businesses) // Request body
        );

    }
    String searchText = "categories";

    @Test
    void searchUsingParentCategoryTest() throws IOException {
        SearchResponse<Businesses> response = client.search(s -> s
                        .index("restaurants-index-000001")
                        .query(q -> q
                                .match(t -> t
                                        .field("categories.title")
                                        .query("title")
                                )
                        ),
                Businesses.class
        );
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }

        List<Hit<Businesses>> hits = response.hits().hits();
        for (Hit<Businesses> hit: hits) {
            Businesses businesses = hit.source();
            logger.info("Found product " + businesses.getAlias() + ", score " + hit.score());
        }
    }
}
