package com.example.client.yelp_fusion.businesses;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.*;
import co.elastic.clients.json.jackson.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.*;
import com.example.client.yelp_fusion.category.*;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.*;

import java.io.*;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class RestaurantsByCategoriesTest extends AbstractRequestTestCase {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantsByCategoriesTest.class);

    private static int limit = 50; // max businesses per page

    private static List<Category> listOfYelpCategories;

    private static Map<String, List<Category>> mapOfCategories; // groups categories by parent names

    // create Category list
    // create Category map, by parent category
    @BeforeAll
    static void setup() throws IOException {
        String price = "$";

        FileReader file = new FileReader(new File(String.format(
                "C:\\Users\\seost\\repositories\\yelp-fusion-java\\src\\test\\resources\\sf-%s.json", price
        )));

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
    void categoriesTest() {
        List<Map.Entry<String, List<Category>>> entries = mapOfCategories.entrySet().stream().toList(); // get all keys
        PrintUtils.titleRed("total parent categories: " + entries.size());

        PrintUtils.titleGreen("parent category names: " + mapOfCategories.keySet());

        List<Category> category = mapOfCategories.get("restaurants"); // List of restaurants located in San Francisco
        PrintUtils.titleGreen("total sub-categories of restaurants: " + category.size());
        PrintUtils.titleCyan("category[0] = " + category.get(0).getAlias());
    }


    @Test
    void blackListTest() {
        List<Category> subCategoriesOfRestaurant = mapOfCategories.get("restaurants"); // List of categories that contains a parent category of type: "restaurants"

        for(Category category: subCategoriesOfRestaurant) {
            Object[] countryBlackList = category.getCountry_blacklist();
            // if US is a blacklisted country for this category, remove it from the categories list
            if(countryBlackList != null) {
                if (Arrays.stream(countryBlackList).toList().contains("US")) {
                    PrintUtils.titleRed("Restaurant sub category: " + category.getAlias() + " \nblack listed countries: " + Arrays.toString(countryBlackList));
                }
            }
        }
    }

    @Test
    void createDocumentsByCategoryAndLocationTest() throws IOException {
        List<Category> subCategoriesOfRestaurant = mapOfCategories.get("restaurants"); // List of categories that contains a parent category of type: "restaurants"

        int maxResults = 1000; // max number of results that a response body can contain.

        // index a document for each parent category with at least 1 result
        for(Category category : subCategoriesOfRestaurant) {

            // reset offset, total hits, and max offset
            int totalHits = 0;
            int offset = 0;
            int maxOffset = 0;

            // perform a Request using a restaurant sub-category at least once
            do {

                String subCategoryAlias = category.getAlias();
                PrintUtils.titleGreen("Current restaurant sub-category: " + category.getAlias());

                String index = "yelp-businesses-sf-restaurants";

                // build a BusinessSearchRequest:
                // search?location=San+Francisco&offset={offset}&limit=50&latitude&longitude&term=restaurants&categories={subCategoryAlias}
                BusinessSearchRequest businessSearchRequest = new BusinessSearchRequest.Builder()
                        .limit(limit)
                        .offset(offset) // update offset to get next 50 results
                        .radius("40000")
                        .latitude(37.751586275)
                        .longitude(-122.447721511)
                        .term("restaurants")
                        .categories(subCategoryAlias) //
                        .build();

                // submit the request to v3/businesses/search
                BusinessSearchResponse businessSearchResponse =
                        yelpClient.businessSearch(businessSearchRequest); //submit the request to yelp

                totalHits = PrintUtils.println(businessSearchResponse.total);
                PrintUtils.titleGreen("current offset: " + offset + " total hits: " + totalHits);

                if (totalHits > 0) {
                    if (maxOffset == 0) {
                        maxOffset = totalHits + 49;
                    }

                    List<Business> businesses = List.of(businessSearchResponse.businesses);
                    if(businesses.size() > 0) {
                        assertThat(businesses.size()).isGreaterThanOrEqualTo(1);

                        BulkRequest.Builder br = new BulkRequest.Builder();

                        for (Business business : businesses) {
                            if (business == null) {
                                PrintUtils.titleGreen("business == null ");
                            }
                            String docId = String.format(
                                    "yelp-business-restaurants-%s-%s", // yelp-businesses-restaurants-pizza-<business id>
                                    subCategoryAlias, // restaurant sub-category: sushi, burgers, pizza...
                                    business.getId()); // yelp business id

                            br.operations(op -> op
                                    .index(idx -> idx
                                            .index(index)
                                            .id(docId)
                                            .document(business)
                                    )
                            );
                            PrintUtils.titleRed("indexing doc: " + docId);
                        }
                        BulkResponse result = esClient.bulk(br.build());
                        // Log errors, if any
                        if (result.errors()) {
                            PrintUtils.titleRed("Bulk had errors");
                            for (BulkResponseItem item : result.items()) {
                                if (item.error() != null) {
                                    PrintUtils.titleRed(item.error().reason());
                                }
                            }
                        }
                    }
                }
                offset+=limit; // update offset
            } while (offset < maxOffset);
        }
    }
}
