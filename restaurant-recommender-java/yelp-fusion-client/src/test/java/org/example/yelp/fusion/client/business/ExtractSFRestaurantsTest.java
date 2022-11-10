package org.example.yelp.fusion.client.business;

import co.elastic.clients.elasticsearch.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.example.elasticsearch.client.elasticsearch.core.*;
import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.*;
import org.example.yelp.fusion.client.business.search.*;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class ExtractSFRestaurantsTest extends AbstractRequestTestCase {

    private static final Logger logger = LoggerFactory.getLogger(ExtractSFRestaurantsTest.class);

    static String index = "yelp-businesses-restaurants-sf";

    static String timestampPipeline = "timestamp-pipeline-id"; // enrich business data with timestampPipeline adding timestamp field

    static String geoPointIngestPipeline = "geo_location-pipeline";

    static StringBuilder requestSb;

    @BeforeAll
    static <T> void setup() throws IOException {
        String pathToCategoriesJson = Objects.requireNonNull(ExtractSFRestaurantsTest.class.getResource("map-of-restaurant-categories.json")).getPath();

        JsonNode jsonNode = mapper
                .objectMapper()
                .readValue(new FileReader(pathToCategoriesJson), ObjectNode.class)
                .get("hits")
                .get("hits");

        assertThat(jsonNode.size()).isGreaterThanOrEqualTo(1);

        PrintUtils.cyan(String.format("number of nodes in Jackson's JSON tree model: %d", jsonNode.size()));

        HashMap<?, ?> map = new HashMap<>();

        Set<String> categories = new HashSet<>();

        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            PrintUtils.green("object " + objectNode.get(0));
        }

        if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            PrintUtils.green("array " + jsonNode.get(0));


            for (int i = 0; i < jsonNode.size(); i++) {
                JsonNode sourceNode = jsonNode.get(i).get("_source");

                JsonNode categoryNode = sourceNode.get("categories");

                String id = sourceNode.get("id").asText();


                categoryNode.forEach(alias -> {
                    restaurantsPerCategory.computeIfAbsent(
                            alias.get("alias").asText(),
                            k -> new ArrayList<>()).add(id);
                });

                setOfValidBusinessIds.add(id);
            }
        }

        int yelpRestaurantsSfTotal = 4410;

        int yelpSubCatsOfRestaurants = 244; // remove "restaurants" category

        assertThat(setOfValidBusinessIds.size()).isEqualTo(yelpRestaurantsSfTotal);

        assertThat(restaurantsPerCategory.keySet().size()).isEqualTo(yelpSubCatsOfRestaurants);


        PrintUtils.green("setOfNewBusinessIds.size = " + setOfValidBusinessIds.size());
        PrintUtils.green("map of categories.size = " + restaurantsPerCategory.keySet().size());
        PrintUtils.green("map of categories keyset = " + restaurantsPerCategory.keySet());


        String header = String.format("-H \"%s: %s\" ", "Authorization", "Bearer " + System.getenv("YELP_API_KEY"));

        String scheme = "https";
        String apiBaseEndpoint = "api.yelp.com";
        requestSb = new StringBuilder("curl ")  //curl -H Authorization: Bearer $YELP_API_KEY
                .append(header)
                .append(scheme)
                .append("://")
                .append(apiBaseEndpoint)
                .append("/v3");
    }


    @Test
    void restaurantsByCategoryTest() throws IOException, URISyntaxException {
        PrintUtils.green(restaurantsPerCategory.get("restaurants").size());
        PrintUtils.green(restaurantsPerCategory.remove("restaurants"));

        String businessSearchEndpoint = "/businesses/search";
        String businessDetailsEndpoint = "/businesses";
        // cache parameters
        int maxResults = 1000;
        int limit = 50;
        String sort_by = "review_count";
        String term = "restaurants";

        String location = "SF";

        // reset offset, total hits, and max offset
        for (String category : restaurantsPerCategory.keySet()) {

            // reset parameters
            int totalHits = 0;
            int offset = 0;
            int maxOffset = 0;

            do {
                int testOffset = offset;
                PrintUtils.red(String.format("Category = %s, Offset = %s",
                        category,
                        offset));


                BusinessSearchResponse<Business> businessSearchResponse = yelpClient.search(s -> s
                                .location(location)
                                .term(term)
                                .categories(c -> c
                                        .alias(category))
                                .limit(limit)
                                .offset(testOffset)
                                .sort_by(sort_by)
                        ,
                        Business.class
                );


                if (businessSearchResponse.error() != null && businessSearchResponse.hits().total().value() == 0) {
                    PrintUtils.red("No Hits = " + businessSearchResponse.error());
                    PrintUtils.red(businessSearchResponse);
                } else {
                    TotalHits total = businessSearchResponse.hits().total();
                    boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

                    PrintUtils.red("businessSearchResponse total = " + totalHits);

                    if (isExactResult && total.value() > 0) {
                        maxOffset = totalHits + 49;

                        List<Hit<Business>> hits = businessSearchResponse.hits().hits();

                        for (Hit<Business> hit : hits) {
                            Business business = hit.source();
                            String businessId = business.id().replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}_\\-]", "");

                            int previousSize = setOfValidBusinessIds.size();

                            if (setOfValidBusinessIds.add(businessId)) {

                                PrintUtils.green(String.format(
                                        "Adding document for Business ID = %s%nname of restaurant = %s%n set of valid business ids contains = %s restaurant id's",
                                        businessId,
                                        business.name(),
                                        setOfValidBusinessIds.size()
                                ));

                                StringBuilder businessDetailsRequestSb = new StringBuilder(requestSb.toString());
                                // TODO: @JsonpDeserializable BusinessDetailsRequest extends RequestBase BusinessDetailsResponse<TDocument> extends ResponseBody<TDocument>
                                businessDetailsRequestSb
                                        .append(businessDetailsEndpoint)
                                        .append("/")
                                        .append(businessId);

                                PrintUtils.green(businessDetailsEndpoint.toString());

                                JsonNode jsonNode = performBusinessDetailsRequest(businessDetailsRequestSb);

                                Business businessDetails = mapper.objectMapper().readValue(jsonNode.toString(), Business.class);

                                IndexResponse idxResp = addDocument(businessDetails);
                            }
                        }
                    }
                }
                offset += limit; // update offset
            } while (offset < maxOffset);
        }
    }

    private JsonNode performBusinessDetailsRequest(StringBuilder businessDetailsRequest) throws IOException {
        PrintUtils.red(businessDetailsRequest.toString());
        
        Process process = Runtime.getRuntime().exec(businessDetailsRequest.toString());

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        return mapper.objectMapper().readValue(response, ObjectNode.class);
    }

    private IndexResponse addDocument(Business business) throws IOException {
        assertThat(index).isEqualTo("yelp-businesses-restaurants-sf");
        String businessId = business.id();


        assertThat(businessId).isNotNull();

        String docId = String.format("%s-%s", // yelp-businesses-restaurants-sf-<business id>
                index, businessId); // yelp business id

        PrintUtils.green("docId = " + docId);
        IndexRequest<Business> idxRequest = IndexRequest.of(in -> in
                .index(index) // index id
                .id(docId)
                .pipeline(geoPointIngestPipeline) // add geolocation
                .pipeline(timestampPipeline) // add timestamp
                .document(business) // Request body
        );
        // Create or updates a document in an index.
        return esClient.index(idxRequest);

    }

    private JsonNode performRequestWithCurl(String request) throws IOException {

        Process process = Runtime.getRuntime().exec(request);

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        JsonNode jsonNode = mapper.objectMapper().readValue(response, ObjectNode.class);

        PrintUtils.cyan("jsonNode size : " + jsonNode.size());

        return jsonNode;
    }
}
