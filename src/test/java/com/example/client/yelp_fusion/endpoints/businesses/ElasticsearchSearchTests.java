package com.example.client.yelp_fusion.endpoints.businesses;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.jackson.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.businesses.*;
import com.example.client.yelp_fusion.endpoints.*;
import com.fasterxml.jackson.databind.node.*;
import jakarta.json.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;


public class ElasticsearchSearchTests extends AbstractRequestTestCase {
    Logger logger = LoggerFactory.getLogger(ElasticsearchSearchTests.class);

    @Test
    void searchCategoriesTest() throws IOException {

        SearchResponse<ObjectNode> response = esClient.search(s -> s
                        .index("yelp-categories")
                        .query(q -> q
                                .match(t -> t
                                        .field("categories.parents")
                                        .query("restaurants")
                                )
                        ),
                ObjectNode.class
        );

        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }


        List<Hit<ObjectNode>> hits = response.hits().hits();

        for (Hit<ObjectNode> hit: hits) {

            Business categories = new JacksonJsonpMapper().objectMapper().readValue(hit.source().toString(), Business.class);

            JsonObject object = Json.createReader(new StringReader(categories.toString())).readObject();

            PrintUtils.println(object.size());
            logger.info("Found product " + hit.id() + ", score " + hit.score());
        }
        // Search by product name
        Query byName = MatchQuery.of(m -> m
                .field("categories.parents")
                .query("restaurants")
        )._toQuery();

        //        JsonObject categories = new JacksonJsonpMapper().objectMapper().readValue(file, JsonObject.class);

    }
}
