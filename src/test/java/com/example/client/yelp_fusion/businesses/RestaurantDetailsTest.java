package com.example.client.yelp_fusion.businesses;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.jackson.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.*;
import jakarta.json.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class RestaurantDetailsTest extends AbstractRequestTestCase {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantsByCategoriesTest.class);
    static Set<String> setOfBusinessIds = new HashSet<>();

    static Set<String> storedBusinessIds = new HashSet<>();

    static String index = "yelp-businesses-restaurants-sf";

    static String pipeline = "timestamp-pipeline-id";
    static List<String> priceList = List.of("$", "$$", "$$$", "$$$$");

    @BeforeAll
    static void setup() throws IOException {
        // search docs using field: price
        for (String str : priceList) {

            // files containing all SF restaurants by price: "$", "$$", "$$$", "$$$$"
            String path = Objects.requireNonNull(RestaurantDetailsTest.class.getResource(String.format("sf-%s.json", str))).getPath();

            FileReader file = new FileReader(new File(String.format(path)));

            Stream<JsonObject> stream = Json.createReader(file)
                    .readObject()
                    .get("hits")
                    .asJsonObject()
                    .get("hits")
                    .asJsonArray()
                    .stream()
                    .map(obj -> (JsonObject) obj);

            AtomicInteger count = new AtomicInteger();

            ((Stream<JsonObject>) stream).forEach((JsonObject prof) -> {

                String id = prof.get("fields")
                        .asJsonObject()
                        .getValue("/id")
                        .toString()
                        .replaceAll("[^a-zA-Z0-9]", "");

//                        .replaceAll("\\[", "")
//                        .replaceAll("]", "")
//                        .replaceAll("\"","");

                count.getAndIncrement();
                setOfBusinessIds.add(id);
            });

            PrintUtils.titleGreen(String.format("number" +
                    " of business ids in price: %s: %s", str, count));
        }

        PrintUtils.titleRed(String.format("total of unique business ids in list %s", setOfBusinessIds.size()));
    }

    @Test
    void duplicateTest() throws IOException {
        int totalDocuments = 1315;


        for (String businessId : setOfBusinessIds) {

            SearchResponse<Business_> response = esClient.search(s -> s
                            .index(index)
                            .query(q -> q
                                    .match(t -> t
                                            .field("id")
                                            .query(businessId)
                                    )
                            )
                            .size(5000),
                    Business_.class
            );

            TotalHits total = response.hits().total();
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

            if (isExactResult && total.value() > 0) {
                storedBusinessIds.add(businessId);
            }
            logger.info("{}index currently stores: {} businesses{}", PrintUtils.GREEN, storedBusinessIds.size(), PrintUtils.RESET);
        }

        JsonArray jsonArray = Json.createReader(new StringReader(setOfBusinessIds.toString())).readArray();
        FileWriter file = new FileWriter("valid-businesses-restaurants.json");
        file.write(jsonArray.toString());
        file.close();

        logger.info("{}index currently stores: {} businesses{}", PrintUtils.GREEN, storedBusinessIds.size(), PrintUtils.RESET);
    }


    @Test
    void requestBusinessDetailsTest() throws IOException {

        for (String businessId : setOfBusinessIds) {
            // Use a Yelp business id as the request parameter for the Business Details Endpoint
            BusinessDetailsRequest request = new BusinessDetailsRequest.Builder() // build Request with Yelp businesses/details query parameters
                    .id(businessId)
                    .build();

//             submit the request to v3/businesses/<id>
            BusinessDetailsResponse response =
                    yelpClient.businessDetails(request); //submit the request to yelp

            // Create a Business object
            Business_ business = new JacksonJsonpMapper().objectMapper().readValue(response.toString(), Business_.class);

            if(response.error == null && response.toString().length() > 10) {

                if (business.getError() == null && business.getId() != null) {
                    String docId = String.format(
                            "yelp-businesses-restaurants-sf-%s", // yelp-businesses-restaurants-pizza-<business id>
                            businessId); // yelp business id


                    // build index request with Business fields
                    IndexRequest<Business_> idxRequest = IndexRequest.of(i -> i
                            .index(index) // index id
                            .id(docId)
                            .pipeline(pipeline) // The pipeline id to preprocess incoming documents with
                            .document(business) // Request body
                    );
                    // Create or updates a document in an index.
                    IndexResponse resp = esClient.index(idxRequest);
                }
            }
        }
    }

    @Test
    void pipelineProcessorsTest() {

    }

    @Test
    void httpComponentsTest() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String apiKeyIdAndSecret = System.getenv("API_KEY_ID") + ":" + System.getenv("API_KEY_SECRET");
        String encodedApiKey = java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));
        HttpGet request = new HttpGet("https://my-deployment-170edf.es.us-west-1.aws.found.io");

        request.addHeader(HttpHeaders.AUTHORIZATION, " ApiKey " + encodedApiKey);

        CloseableHttpResponse response = httpClient.execute(request);
        // Get HttpResponse Status
        logger.info("protocol version: {}", response.getProtocolVersion());              // HTTP/1.1
        logger.info("status code: {}", response.getStatusLine().getStatusCode());   // 200
        logger.info("reason phrase: {}", response.getStatusLine().getReasonPhrase()); // OK
        logger.info("StatusLine: {}", response.getStatusLine().toString());// HTTP/1.1 200 OK


        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // return it as a String
            String result = EntityUtils.toString(entity);
            System.out.println(result);
        }

    }


}
