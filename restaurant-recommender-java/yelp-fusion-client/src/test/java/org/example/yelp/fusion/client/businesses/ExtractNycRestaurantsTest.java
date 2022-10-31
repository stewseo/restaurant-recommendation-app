package org.example.yelp.fusion.client.businesses;

import co.elastic.clients.elasticsearch.core.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.example.elasticsearch.client.json.jackson.*;
import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.*;
import org.example.yelp.fusion.client.businesses.search.*;
import org.example.yelp.fusion.client.transport.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

public class ExtractNycRestaurantsTest extends AbstractRequestTestCase {


    private static final Logger logger = LoggerFactory.getLogger(ExtractSFRestaurantsTest.class);

    static String timestampPipeline = "timestamp-pipeline-id"; // enrich business data with timestampPipeline adding timestamp field

    static String geoPointIngestPipeline = "geo_location-pipeline";
    static String indexNyc = "yelp-businesses-restaurants-nyc";
    static StringBuilder requestSb;

    static String businessSearchEndpoint = "/businesses/search";
    static String businessDetailsEndpoint = "/businesses";
    static Set<String> setOfNycRestaurants;     // business id's in database with parent category: "restaurants"
    private static List<String> listOfCategories; // keep track of current category by index

    static private Set<String> categoriesGreaterThanMax; // categories that require additional request parameters

    static private Map<String, Set<Coordinates>> mapOfNeighborhoodParams; // mapping neighborhood name to query params: latitude, longitude, search radius, sort_by
    static private Set<String> nullCategories; // invalid cateogories

    // initialize
    @BeforeAll
    static <T> void setup() throws IOException {
        String pathToCategoriesJson = Objects.requireNonNull(ExtractSFRestaurantsTest.class.getResource("map-of-restaurants-categories-nyc.json")).getPath();

        JsonNode jsonNode = mapper
                .objectMapper
                .readValue(new FileReader(pathToCategoriesJson), ObjectNode.class)
                .get("hits")
                .get("hits");

        assertThat(jsonNode.size()).isGreaterThanOrEqualTo(1);

        PrintUtils.cyan(String.format("number of nodes in Jackson's JSON tree model: %d", jsonNode.size()));

        Set<String> categories = new HashSet<>();

        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            PrintUtils.green("object " + objectNode.get(0));
        }

        setOfNycRestaurants = new HashSet<>();

        Map<String, List<String>> unsortedRestaurantCategories = new HashMap<>();

        if (jsonNode.isArray()) {

            ArrayNode arrayNode = (ArrayNode) jsonNode;

            for (int i = 0; i < jsonNode.size(); i++) {
                JsonNode sourceNode = jsonNode.get(i).get("_source");

                JsonNode categoryNode = sourceNode.get("categories");

                String id = sourceNode.get("id").asText();

                PrintUtils.green("id: " + id);

                categoryNode.forEach(alias -> {
                    unsortedRestaurantCategories.computeIfAbsent(
                            alias.get("alias").asText(),
                            k -> new ArrayList<>()).add(id);
                });

                setOfNycRestaurants.add(id);
            }

        }


        int nycTotalRestaurants = 3100; // ids in index: yelp-businesses-restaurants-nyc
        int nycRestaurantsCategories = 279; // distinct categories in index: yelp-businesses-restaurants-nyc

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

        assertThat(setOfNycRestaurants.size()).isEqualTo(nycTotalRestaurants);
        assertThat(restaurantsPerCategory.keySet().size()).isEqualTo(nycRestaurantsCategories);

        PrintUtils.green("Total restaurant id's = " + setOfNycRestaurants.size());
        PrintUtils.green("Total categories = " + restaurantsPerCategory.keySet().size());

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

        PrintUtils.green(restaurantsPerCategory.remove("restaurants"));

        // more convenient way to track current category. list is sorted by current most popular total restaurants per category.
        // note: does not mean this category has been selected as a parameter.
        listOfCategories = unsortedRestaurantCategories.entrySet().stream().sorted(comparingInt(e -> e.getValue().size()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ))
                .keySet()
                .stream()
                .toList();

        nullCategories = new HashSet<>();
    }

    static int previousIndex;

    @Test
    public void allrestaurantsLocationNycTest() throws IOException {
        int maxResults = 1000;
        int limit = 50;
        String sort_by = "review_count";
        String term = "restaurants";
        String location = "New+York+City";
        previousIndex = 0;
        assertThat(indexNyc).isEqualTo("yelp-businesses-restaurants-nyc");
        assertThat(restaurantsPerCategory.keySet().size()).isEqualTo(278);

        for (String category : listOfCategories) {
            // reset parameters
            int totalHits = 0;
            int offset = 0;
            int maxOffset = 0;

            previousIndex = listOfCategories.indexOf(category);
            PrintUtils.red(String.format("Current index position: %s of category: %s", previousIndex, category));

            // send a requ
            // est to the yelp fusion business search endpoint at least once.
            do {
                // upset atmotic offset
                int atomicOffset = offset;
                BusinessSearchRequest requestWithNycLocParam = BusinessSearchRequest.of(i -> i
                        .location(location)
                        .categories(category)
                        .terms(term)
                        .limit(limit)
                        .offset(atomicOffset)
                        .sort_by(sort_by));

                // lowlevel-restclient.RequestBase or elasticsearch-client.types_.RequestBase
                assertThat(requestWithNycLocParam.toString()).isEqualTo(
                        String.format("BusinessSearchRequest: {\"sort_by\":\"review_count\",\"limit\":50,\"offset\":%s,\"location\":[\"New+York+City\"],\"terms\":[\"restaurants\"],\"categories\":[\"%s\"]}",
                                offset,
                                category
                        ));

                JsonNode response;
                try {
                    response = performRequestWithCurl(requestWithNycLocParam);
                } catch (IOException e) {
                    throw new RuntimeException("Perform Request with Curl failed: {}", e);
                }
                // meta fields from businesses/search: error, total, businesses
                response.fieldNames().forEachRemaining(name -> {

                    PrintUtils.red("field name = " + name + " number of fields = " + response.size());

                    List<Business_> listOfBusinesses = new ArrayList<>();

                    // extract all business id's by sending request with more specific parameters
                    if (response.get("error") != null) {
                        JsonNode nodeError = response.get("error");
                        PrintUtils.green("error.description = " + nodeError.get("description"));

                        if(nodeError.toString().contains("You've reached the access limit for this client.")){
                            return;
                        }
                        if (nodeError.toString().contains("is greater than the maximum of 1000")) {
                            categoriesGreaterThanMax.add(category);

                            PrintUtils.cyan("error.description contained is greater than maximum 1000 results. Add additional request parameters and resend.");
                            int newOffset = 0;

                            for (int price : List.of(4, 3, 2, 1)) {
                                // iterate each page of results before sending next query, categories = current & price = $, $$, $$$ and $$$$
                                BusinessSearchRequest requestWithAddedPriceParam = BusinessSearchRequest.of(i -> i
                                        .location(location)
                                        .categories(category)
                                        .terms(term)
                                        .limit(limit)
                                        .offset(newOffset)
                                        .sort_by(sort_by)
                                        .price(String.valueOf(price))
                                );

                                try {
                                    // send a new request with additonal parameters
                                    JsonNode resp = performRequestWithCurl(requestWithNycLocParam);
                                    int total =  resp.get("total").asInt();
                                    int tempMax = total + 49;
                                    int tempOffset = 0;
                                    while(tempOffset < tempMax) {
                                        tempOffset += limit;

                                        try {
                                            listOfBusinesses.addAll(List.of(new JacksonJsonpMapper().objectMapper.readValue(response.get("businesses").toString(), Business_[].class)));
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                // add districts if still greater than 1000
                                // add latitude, longitude, search radius and sort_by distance if still greater than 1000
                            }
                        }
                    }

                    else if(response.get("businesses") != null) {
                        if (response.get("total") != null || response.get("total").asInt() != 0) {
                            try {
                                listOfBusinesses.addAll(List.of(new JacksonJsonpMapper().objectMapper.readValue(response.get("businesses").toString(), Business_[].class)));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    else {
                        PrintUtils.green("Nothing should print here. Category = " + category);
                        nullCategories.add(category);
                    }

                    PrintUtils.green("Adding " + listOfBusinesses.size() + " businesses to Set of restaurant ids");
                    addAllBusinesses(listOfBusinesses);
                });
                offset += limit; // update offset
            } while (offset < maxOffset);
        }
        PrintUtils.green("null categories set " + nullCategories.size());
        PrintUtils.green("Greater than 1000 results categories " + categoriesGreaterThanMax.size());

    }
    private void addAllBusinesses(List<Business_> listofBusinesses) {
        for (Business_ business : listofBusinesses) {

            String businessId = business.id().replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}_\\-]", "");

            int previousSize = setOfNycRestaurants.size();
            // try adding to set of businesses id's
            if (setOfNycRestaurants.add(businessId)) {

                PrintUtils.green(String.format(
                        "Adding document for Business ID = %s%nname of restaurant = %s%n set of valid business ids contains = %s restaurant id's",
                        businessId,
                        business.name(),
                        setOfNycRestaurants.size()
                ));
                String header = String.format("-H \"%s: %s\" ", "Authorization", "Bearer " + System.getenv("YELP_API_KEY"));

                String yelpBase = "https://api.yelp.com/v3";
                StringBuilder businessDetailsRequestSb = new StringBuilder(String.format("curl %s %s", header, yelpBase));
                // TODO: @JsonpDeserializable BusinessDetailsRequest extends RequestBase BusinessDetailsResponse<TDocument> extends ResponseBody<TDocument>
                businessDetailsRequestSb
                        .append(businessDetailsEndpoint)
                        .append("/")
                        .append(businessId);

                JsonNode jsonNode = null;
                try {

                    jsonNode = performBusinessDetailsRequest(businessDetailsRequestSb);

                    Business_ businessDetails = mapper.objectMapper.readValue(jsonNode.toString(), Business_.class);

                    IndexResponse idxResp = addDocument(businessDetails);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private <RequestT> JsonNode performRequestWithCurl(RequestT request) throws IOException {
        String header = String.format("-H \"%s: %s\" ", "Authorization", "Bearer " + System.getenv("YELP_API_KEY"));

        String scheme = "https";
        String apiBaseEndpoint = "api.yelp.com";

        requestSb = new StringBuilder("curl ")  //curl -H Authorization: Bearer $YELP_API_KEY
                .append(header)
                .append(scheme)
                .append("://")
                .append(apiBaseEndpoint)
                .append("/v3")
                .append(businessSearchEndpoint)
                .append("?");

        if(yelpClient._transport() instanceof YelpRestTransport){
            YelpRestTransport transport = (YelpRestTransport) yelpClient._transport();
            transport.options().headers();
            transport.restClient().getNodes().get(0).getHost().getSchemeName();
            transport.restClient().getNodes().get(0).getHost().getHostName();
            // request.endpoint = endpoint id
        }
        if(request instanceof BusinessSearchRequest) {

            BusinessSearchRequest businessSearchRequest = (BusinessSearchRequest) request;

            requestSb
                    .append("categories").append("=").append(businessSearchRequest.categories().get(0))
                    .append("&location").append("=").append(businessSearchRequest.location().get(0))
                    .append("&term").append("=").append(businessSearchRequest.terms().get(0))
                    .append("&offset").append("=").append(businessSearchRequest.offset())
                    .append("&sort_by").append("=").append((businessSearchRequest).sort_by())
                    .append("&limit").append("=").append((businessSearchRequest).limit())
                    .append("price").append("=").append((businessSearchRequest).price());

            PrintUtils.green(requestSb.toString());
        }

        Process process = Runtime.getRuntime().exec(requestSb.toString());

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        return mapper.objectMapper.readValue(response, ObjectNode.class);
    }

    private IndexResponse addDocument(Business_ business) throws IOException {
        assertThat(indexNyc).isEqualTo("yelp-businesses-restaurants-nyc");
        String businessId = business.id();
        PrintUtils.green(business.id());

        assertThat(businessId).isNotNull();

        logger.info("Creating Document and adding to database, BusinessID = {}",
                businessId);


        String docId = String.format("%s-%s", // yelp-businesses-restaurants-sf-<business id>
                indexNyc, businessId); // yelp business id

        PrintUtils.green("docId = " + docId);
        IndexRequest<Business_> idxRequest = IndexRequest.of(in -> in
                .index(indexNyc) // index id
                .id(docId)
                .pipeline(geoPointIngestPipeline) // add geolocation
                .pipeline(timestampPipeline) // add timestamp
                .document(business) // Request body
        );
        // Create or updates a document in an index.
        return esClient.index(idxRequest);
    }

    private JsonNode performBusinessDetailsRequest(StringBuilder businessDetailsRequest) throws IOException {
        PrintUtils.red(businessDetailsRequest.toString());

        Process process = Runtime.getRuntime().exec(businessDetailsRequest.toString());

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        return mapper.objectMapper.readValue(response, ObjectNode.class);
    }
}
