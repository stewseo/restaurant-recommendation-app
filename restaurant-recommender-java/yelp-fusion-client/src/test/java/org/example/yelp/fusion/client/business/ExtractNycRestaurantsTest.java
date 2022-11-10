package org.example.yelp.fusion.client.business;


import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.example.elasticsearch.client.json.jackson.*;
import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.*;
import org.example.yelp.fusion.client.business.search.*;
import org.example.yelp.fusion.client.transport.*;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

@SuppressWarnings({"ConstantConditions", "unused"})
public class ExtractNycRestaurantsTest extends AbstractRequestTestCase {

    public static final RequestOptions YELP_AUTHORIZATION_HEADER;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Authorization", "Bearer " + System.getenv("YELP_API_KEY"));
        YELP_AUTHORIZATION_HEADER = builder.build();
    }

    // initialize
    @BeforeAll
    static <T> void setup() throws IOException, URISyntaxException {

        ArrayNode arrayNode = mapper.objectMapper().createArrayNode();

        for (String path : List.of(
                ExtractNycRestaurantsTest.class.getResource("map-of-categories-nyc-0-7000.json").getPath(),
                ExtractNycRestaurantsTest.class.getResource("map-of-categories-nyc-7001-14000.json").getPath())) {

            arrayNode.addAll(
                    (ArrayNode) mapper.objectMapper()
                            .readValue(new FileReader(path),
                                    ObjectNode.class)
                            .get("hits")
                            .get("hits"));

        }

        assertThat(arrayNode).isNotNull();
        assertThat(arrayNode.size()).isEqualTo(indexedRestaurantsNyc);

        setOfNycRestaurants = new HashSet<>();

        Map<String, List<String>> unsortedRestaurantCategories = new HashMap<>();

        for (JsonNode jsonNode : arrayNode) {

            JsonNode sourceNode = jsonNode.get("_source");

            JsonNode categoryNode = sourceNode.get("categories");

            String id = sourceNode.get("id").asText();
            assertThat(id).isNotNull();

            if (categoryNode.isArray()) {
                ArrayNode aNode = (ArrayNode) categoryNode;
                assertThat(aNode).isNotNull();

                for (JsonNode aliasNode : aNode) {
                    String catAlias = aliasNode.get("alias").asText();

                    if (!catAlias.equals("restaurants") && !catAlias.equals("food")) {
                        unsortedRestaurantCategories.computeIfAbsent(
                                catAlias,
                                k -> new ArrayList<>()).add(id);
                    }
                }
                
                setOfNycRestaurants.add(id);

            }
        }
        
        // by number of restaurants in a category
        restaurantsPerCategory = unsortedRestaurantCategories.entrySet().stream().sorted(comparingInt(e -> e.getValue().size()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));

        assertThat(setOfNycRestaurants.size()).isEqualTo(indexedRestaurantsNyc);
        assertThat(restaurantsPerCategory.keySet().size()).isEqualTo(indexedCategoriesNycRestaurants);

        String header = String.format("-H \"%s: %s\" ", "Authorization", "Bearer " + System.getenv("YELP_API_KEY"));
        String scheme = "https";
        String apiBaseEndpoint = "api.yelp.com";

        requestSb = new StringBuilder("curl ")  //curl -H Authorization: Bearer $YELP_API_KEY
                .append(header)
                .append(scheme)
                .append("://")
                .append(apiBaseEndpoint)
                .append("/v3/")
                .append(businessSearchEndpoint)
                .append("?");

        String category = "korean";
        int idxOf = restaurantsPerCategory.keySet().stream().toList().indexOf(category);

        listOfCategories = restaurantsPerCategory
                .keySet()
                .stream()
                .skip(idxOf)
                .toList();

        setOfCatsGreaterThanMax = new HashSet<>();
        // invalid cateogories
        Set<String> setOfNullCategories = new HashSet<>();

    }
    static int previousIndex;

    @Test
    public void byLocationCategoryPriceTest() throws IOException, URISyntaxException {
        int maxResults = 1000;
        int limit = 50;
        String sort_by = "review_count";
        String term = "restaurants";
        String location = "New+York+City";
        previousIndex = 0;

        assertThat(indexNyc).isEqualTo("yelp-businesses-restaurants-nyc");
        assertThat(restaurantsPerCategory.keySet().size()).isEqualTo(indexedCategoriesNycRestaurants);

        int count = 0;

        for (String category : listOfCategories) {

            List<String> list = List.of("4", "3", "2", "1");

            for (String price : list) {
                // reset count parameters for each price & category combination
                int offset = 0;
                int maxOffset = 0;
                int total = 0;

                // build a BusinessSearchRequest at least once.
                do {
                    // upset atmotic offset
                    final int fOffset = offset;
                    PrintUtils.green("offset: " + offset + " " + maxOffset);

                    BusinessSearchResponse<Business> businessSearchResponse =  yelpClient.search(s -> s
                            .location("nyc")
                            .categories(c->c
                                    .alias(category)
                            )
                            .term("restaurants")
                            .limit(50)
                            .offset(fOffset)
                            .price(price)
                            .sort_by("review_count"),
                            Business.class);

                    if (businessSearchResponse != null && businessSearchResponse.businesses() != null) {

                        if (businessSearchResponse.total() > 0) {
                            total = businessSearchResponse.total();

                            maxOffset = total + 49;

                            if (total > maxResults) {
                                setOfCatsGreaterThanMax.add(category);
                                PrintUtils.red("setOfCatsGreaterThanMax = " + setOfCatsGreaterThanMax.size());
                            }
                            List<Business> businesses = businessSearchResponse.businesses();

                            PrintUtils.cyan(String.format("Checking if business exists in index: %s%n, " +
                                            "catgeory: %s , total results: %s%n" +
                                            "offset: %s, max offset: %s",
                                    indexNyc,
                                    category, total,
                                    offset, maxOffset)
                            );

                            addBusinesses(businessSearchResponse.businesses());
                        }
                    }

                    offset += limit; // update offset
                } while (offset < maxOffset); // iterate up to 20 50-business pages
            }
        }
    }

    
    private void addBusinesses(List<Business> listOfBusinesses) throws IOException, URISyntaxException {

        for (Business business : listOfBusinesses) {

            String businessId = business.id().replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}_\\-]", "");

            if (setOfNycRestaurants.add(businessId)) {

                BusinessDetailsResponse<Business> businessDetailsResponse = yelpClient.businessDetails(s -> s
                                .id(businessId),
                        Business.class);

                IndexResponse idxResp = null;
                try {

                    idxResp = addDocument(businessDetailsResponse.businesses().get(0));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NullPointerException n) {
                    throw new NullPointerException(n.getMessage());
                }
            }
        }
    }

    private IndexResponse addDocument(Business business) throws IOException {
        assertThat(indexNyc).isEqualTo("yelp-businesses-restaurants-nyc");
        String businessId = business.id();
        if(businessId != null) {

            String docId = String.format("%s-%s", // yelp-businesses-restaurants-sf-<business id>
                    indexNyc, businessId); // yelp business id

            IndexRequest<Business> idxRequest = IndexRequest.of(in -> in
                    .index(indexNyc) // index id
                    .id(docId)
                    .pipeline(geoPointIngestPipeline) // add geolocation
                    .pipeline(timestampPipeline) // add timestamp
                    .document(business) // Request body
            );
            // Create or updates a document in an index.
            return esClient.index(idxRequest);
        }
        return null;
    }
}
