package com.example.client.yelp_fusion.category;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import com.example.client.yelp_fusion.*;
import com.example.client.yelp_fusion.businesses.*;
import com.fasterxml.jackson.databind.*;
import jakarta.json.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static com.example.client.transport.Transport.*;

public class CategoryListTest extends AbstractRequestTestCase {

    ObjectMapper mapper;

    @Test
    void testets() throws IOException {

    }

    @Test
    void indexAllYelpCategoriesTest() throws IOException {
        mapper = new ObjectMapper();

        JsonReader jsonReader = Json.createReader(new FileReader("src\\test\\resources\\categories.json"));
        JsonObject jsonObject = jsonReader.readObject();

        Business businesses = mapper.readValue(jsonObject.toString(), Business.class);

        IndexResponse response = esClient.index(i -> i // Creates or updates a document in an index.
                .index("restaurants-index-000001") // restaurants-index-000001 alias
                .id("categories-") // Document ID
                .document(businesses) // Request body
        );

    }
    String searchText = "categories";

    //C:\Users\seost\repositories\yelp-fusion-java\src\test\java\com\example\esClient\yelp_fusion\business
    // C:\Users\seost\repositories\yelp-fusion-java\src\test\resources\com\example\esClient\yelp_fusion\business
    @Test
    void searchUsingParentCategoryTest() throws IOException {
        SearchResponse<BusinessSearchResponse> response = esClient.search(s -> s
                        .index("restaurants-index-000001")
                        .query(q -> q
                                .match(t -> t
                                        .field("categories")
                                        .query("title")
                                )
                        ),
                BusinessSearchResponse.class
        );
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }

        List<Hit<BusinessSearchResponse>> hits = response.hits().hits();
        for (Hit<BusinessSearchResponse> hit: hits) {
            BusinessSearchResponse businesses = hit.source();
            logger.info("Found product " + businesses.getTotal() + ", score " + hit.score());
        }
    }
}
