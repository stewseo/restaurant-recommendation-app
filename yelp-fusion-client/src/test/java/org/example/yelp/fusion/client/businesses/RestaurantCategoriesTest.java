package org.example.yelp.fusion.client.businesses;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.json.jackson.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import jakarta.json.*;
import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class RestaurantCategoriesTest extends AbstractRequestTestCase {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantCategoriesTest.class);

    static String index = "yelp-businesses-restaurants-sf";

    static String timestampPipeline = "timestamp-pipeline-id"; // enrich business data with timestampPipeline adding timestamp field

    static String geoPointIngestPipeline = "geo_location-pipeline";

    static Set<String> businessIdsByPrice = new HashSet<>();
    static StringBuilder requestSb = new StringBuilder("curl ");

    @BeforeAll
    static void setup() throws IOException {
        String pathToAllRestaurants = Objects.requireNonNull(RestaurantCategoriesTest.class.getResource("map-of-restaurant-categories.json")).getPath();

        JsonNode jsonNode = mapper
                .objectMapper
                .readValue(new FileReader(pathToAllRestaurants), ObjectNode.class)
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
                    mapOfCategoriesToRestaurants.computeIfAbsent(
                            alias.get("alias").asText(),
                            k -> new ArrayList<>()).add(id);
                });

                setOfValidBusinessIds.add(id);
            }
        }

        mapOfCategoriesToRestaurants.forEach((category, list) -> {

            PrintUtils.cyan(String.format("Category: %s has %d restaurants ",
                    category,
                    mapOfCategoriesToRestaurants.get(category).size()
            ));
        });

        assertThat(setOfValidBusinessIds.size()).isEqualTo(2931);
        PrintUtils.green("setOfNewBusinessIds.size = " + setOfValidBusinessIds.size());

        assertThat(mapOfCategoriesToRestaurants.keySet().size()).isEqualTo(225);
        PrintUtils.green("map of categories.size = " + mapOfCategoriesToRestaurants.keySet().size());
        PrintUtils.green("map of categories keyset = " + mapOfCategoriesToRestaurants.keySet());



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
    void restaurantsByCategoryTest() throws IOException {
        String businessSearchEndpoint = "businesses/search";
        String businessDetailsEndpoint = "businesses/";
        int maxResults = 1000; // max number of results that a response body can contain.
        int limit = 50;
        String sort_by = "review_count";
        String term = "restaurants";


        // reset offset, total hits, and max offset
        for (String category : mapOfCategoriesToRestaurants.keySet()) {
            int totalHits = 0;
            int offset = 0;
            int maxOffset = 0;

            String location = "San+Francisco";

            do {

                Map<String, String> params = new HashMap<>();
                params.put("location", "San+Francisco");
                params.put("limit", String.valueOf(limit));
                params.put("term", term);
                params.put("sort_by", sort_by);
                params.put("offset", String.valueOf(offset += limit));
                params.put("categories", category);

                // Use a Yelp business id as the request parameter for the Business Details Endpoint
                StringBuilder businessSearchRequestSb = requestSb;
                businessSearchRequestSb.append(businessSearchEndpoint);

                params.forEach((k, v) -> {
                    if (requestSb.toString().endsWith("search")) {
                        businessSearchRequestSb.append("?")
                                .append(k)
                                .append("=")
                                .append(v);

                    } else {
                        businessSearchRequestSb
                                .append("&")
                                .append(k)
                                .append("=")
                                .append(v);
                    }
                });
                PrintUtils.red("Request = " + businessSearchRequestSb.toString());
                JsonNode response = performRequestWithCurl(businessSearchRequestSb.toString());

                // Create a Business object
                BusinessSearchResponse businessSearchResponse = new JacksonJsonpMapper().objectMapper().readValue(response.toString(), BusinessSearchResponse.class);


                totalHits = businessSearchResponse.total();
                PrintUtils.red("businessSearchResponse total = " + totalHits);

                if (totalHits > 0) {
                maxOffset = totalHits + 49;

                    if (businessSearchResponse.total() > 0 && businessSearchResponse.error() == null) {

                        List<Business_> listOfBusinesses = businessSearchResponse.businesses();

                        for(Business_ business: listOfBusinesses){

                            String businessId = business.id().replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}_\\-]", "");

                            if(setOfValidBusinessIds.add(businessId)) {

                                PrintUtils.cyan("Business ID is new, Adding document for ID = " + businessId + " name of restaurant = " + business.name());

                                StringBuilder businessDetailsRequestSb = requestSb;

                                businessSearchRequestSb
                                        .append(businessDetailsEndpoint)
                                        .append(businessId);

                                Business_ businessDetails = performBusinessDetailsRequest(businessDetailsRequestSb.toString());
                                IndexResponse idxResp = addDocument(businessDetails);
                            }
                        }
                    }
                }
                offset += limit; // update offset
            } while (offset < maxOffset);
        }
    }

    private Business_ performBusinessDetailsRequest(String businessDetailsRequest) throws IOException {

        Process process = Runtime.getRuntime().exec(businessDetailsRequest);

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        Business_ business  = mapper.objectMapper.readValue(response, Business_.class);

        PrintUtils.cyan("business : " + business.name());

        return business;
    }

    private IndexResponse addDocument(Business_ business) throws IOException {
        assertThat(index).isEqualTo("yelp-businesses-restaurants-sf");
        String businessId = business.id();

        assertThat(businessId).isNotNull();
        logger.info("Creating Document and adding to database, BusinessID = {}",
                businessId);


        String docId = String.format("%s-%s", // yelp-businesses-restaurants-sf-<business id>
                index, businessId); // yelp business id

        PrintUtils.green("docId = " + docId);
        IndexRequest<Business_> idxRequest = IndexRequest.of(in -> in
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

        
        JsonObject jsonObject = Json.createReader(new StringReader(response)).readObject();

        JsonNode jsonNode = mapper.objectMapper.readValue(response, ObjectNode.class);

        PrintUtils.cyan("json object keyset size: " + jsonObject.keySet().size());

        PrintUtils.cyan("jsonNode size : " + jsonNode.size());

        return jsonNode;
    }
}
