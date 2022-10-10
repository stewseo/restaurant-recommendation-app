package com.example.client.yelp_fusion.endpoints.businesses;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.json.jackson.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.businesses.*;
import com.example.client.yelp_fusion.category.*;
import com.example.client.yelp_fusion.endpoints.*;
import com.example.client.yelp_fusion.model.*;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.*;

import java.io.*;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class CreateOrUpdateYelpBusinessDocumentsTest extends AbstractRequestTestCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrUpdateYelpBusinessDocumentsTest.class);

    private static Map<String, List<Category>> mapOfCategories; // groups categories by parent names

    private static int limit = 50; // max businesses per page

    private static List<Category> listOfYelpCategories;

    @BeforeAll
    static void setup() throws IOException {

        FileReader file = new FileReader(new File("C:\\Users\\seost\\repositories\\yelp-fusion-java\\src\\test\\resources\\categories.json"));

        // parse categories to list
        List<Category> categories = Arrays.asList(new JacksonJsonpMapper().objectMapper().readValue(file, Category[].class));

        // map categories by field: parents
        mapOfCategories = new HashMap<>();

        for(Category category : categories) {

            List<String> parents = List.of(category.getParents());
            parents.forEach(parentCategory ->
                    mapOfCategories.computeIfAbsent(
                            parentCategory,
                            k -> new ArrayList<>()).add(category)
            );
        }
        PrintUtils.titleCyan("total categories: " + categories.size());
    }

    @Test
    void testConnections() {
        List<Category> category = mapOfCategories.get("restaurants"); // List of restaurants located in San Francisco
        PrintUtils.titleGreen("total sub-categories of restaurants: " + category.size());
        PrintUtils.titleRed("total parent categories: " + mapOfCategories.keySet().size());
    }

    @Test
    void requestTest() throws IOException {
        List<Category> category = mapOfCategories.get("restaurants"); // List of restaurants located in San Francisco

        List<Category> listOfRestaurantCategories = listOfYelpCategories.stream()
                .filter(e -> {
                            return Arrays.toString(e.getParents()).equalsIgnoreCase("[restaurants]");
                        }
                ).toList();

        int maxResults = 1000;
        // index a document for each category
        for(Category restaurant : listOfRestaurantCategories) {
            int offset = 0; // cursor to get response body from each page of results
            int count = 0; // cursor position

            String typeOfRestaurant = restaurant.getAlias(); // breakfast and brunch, chinese, delis

            assertThat(typeOfRestaurant).isNotNull();

            TestBusinessSearchRequest.Builder businessSearchBuilder = PrintUtils.println("request params: ", new TestBusinessSearchRequest.Builder()
                    .limit(limit)
                    .location("SF")
                    .offset(0)
                    .radius("40000")
                    .latitude(37.751586275)
                    .longitude(-122.447721511)
                    .term("restaurants")
                    .categories(typeOfRestaurant));


            BusinessEndpointResponse businessSearchResponse = yelpClient.businessSearch(businessSearchBuilder.build());

            TestBusinessEndpointResponse testBusinessSearchResponse =
                    new JacksonJsonpMapper().objectMapper().readValue(businessSearchResponse.toString(), TestBusinessEndpointResponse.class);

            int totalHits = PrintUtils.println(testBusinessSearchResponse.getTotal());

            String index = "yelp-restaurants-term-category";

            // temporary condition to check max results.
            while (PrintUtils.println(count++) <= totalHits && totalHits <= maxResults) {
                offset += limit;
                PrintUtils.println(offset);
                TestBusinessSearchRequest.Builder builderWhile = new TestBusinessSearchRequest.Builder()
                        .limit(limit)
                        .offset(offset) // update offset to get next 50 results
                        .radius("40000")
                        .latitude(37.751586275)
                        .longitude(-122.447721511)
                        .term("restaurants")
                        .categories(typeOfRestaurant);

                final TestBusinessEndpointResponse response = new JacksonJsonpMapper().objectMapper().readValue( // perform request with new offset
                        yelpClient.businessSearch(builderWhile.build()).toString(),
                        TestBusinessEndpointResponse.class);

                totalHits = response.getTotal();

                // business detail docs yelp-business-<business id>
                String docId = String.format("yelp-business-restaurants-%s-%s", typeOfRestaurant, offset); // yelp-businesses-restaurants-<by category type>-<offset>

                if(offset % 49 == 0) {
                    PrintUtils.println("business name: " + response.businesses.get(0).getName() +
                            "\nbusiness id: " + response.businesses.get(0).getId());
                }

                PrintUtils.titleGreen(String.format("%nbusinesses response:%noffset: %s%n businesses: "
                        + "%ntotal: %s%n Region: %s%n" + response.getRegion(), offset, response.getBusiness().size(), response.getTotal()));


                IndexRequest<TestBusinessEndpointResponse> request = IndexRequest.of(i -> i
                        .index(index)
                        .id(docId)
                        .document(response)
                );

                IndexResponse resp = esClient.index(request);

            }
        }
    }
}
