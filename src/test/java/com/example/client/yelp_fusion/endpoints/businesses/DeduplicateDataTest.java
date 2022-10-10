package com.example.client.yelp_fusion.endpoints.businesses;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.jackson.*;
import com.example.client.json.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.businesses.*;
import com.example.client.yelp_fusion.category.*;
import com.example.client.yelp_fusion.endpoints.*;
import com.fasterxml.jackson.databind.node.*;
import jakarta.json.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public class DeduplicateDataTest extends AbstractRequestTestCase {

    Logger logger = LoggerFactory.getLogger(DeduplicateDataTest.class);
    // Build a TestBusinessSearchRequest that will be used by
    // TestYelpTransport
    // TestYelpTransportOptions

        List<String> restaurantCategories = List.of(
            "japanese,sushi",
                "bbq",
                "bakeries",
                "cafes",
                "korean",
                "italian",
                "steak",
                "seafood",
                "chinese",
                "mexican",
                "Cheesesteaks",
                "Chicken Wings",
                "pizza",
                "salad",
                "brunch",
                "dinner",
                "lunch",
                "breakfast",
                "burgers",
                "french",
                "indian",
                "vietnamese,pho",
                "breakfast",
                "vegetarian",
                "vegan",
                "thai",
                "steakhouses",
                "peruvian",
                "ramen",
                "udon",
                "bars"
        );

    List<String> sfDistricts = List.of(
            "inner sunset",
            "outer sunset",
            "mission",
            "Mission Bay",
            "richmond",
            "haight",
            "bernal heights",
            "castro"
    );

    List<String> typeOfMeal = List.of(
            "breakfast",
            "dinner",
            "lunch",
            "brunch",
            "dessert",
            "snack"
    );

    Map<String, String> categories;
    String typeOfMealString = typeOfMeal.get(1);

    String category = restaurantCategories.get(0);

    private List<String> listOfAttributes = List.of("hot_and_new", "request_a_quote", "reservation", "waitlist_reservation", "deals", "gender_neutral_restrooms", "open_to_all", "wheelchair_accessible");


    @Test
    void fromJsonTest() throws IOException {
        FileReader file = new FileReader(new File("C:\\Users\\seost\\repositories\\yelp-fusion-java\\src\\test\\resources\\categories.json"));

        List<Category> categories = Arrays.asList(new JacksonJsonpMapper().objectMapper().readValue(file, Category[].class));

//        PrintUtils.titleCyan(categories.size());

        IndexRequest<JsonData> req;

        req = IndexRequest.of(b -> b
                .index("yelp-categories")
                .withJson(file)
        );

        esClient.index(req);

    }

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


    @Test
    void searchByCoordinateTest() throws IOException {
        FileReader file = new FileReader(new File("C:\\Users\\seost\\repositories\\yelp-fusion-java\\src\\test\\resources\\categories.json"));

        JsonArray jsonArray = Json.createReader(file).readArray();

        List<Category> categories = Arrays.asList(new JacksonJsonpMapper().objectMapper().readValue(jsonArray.toString(), Category[].class));

        List<Category> list = categories.stream()
                .filter(e -> {
                    return Arrays.toString(e.getParents()).equalsIgnoreCase("[restaurants]");
                }
                ).toList();
    }


}
