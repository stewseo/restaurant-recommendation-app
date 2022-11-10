package org.example.yelp.fusion.client;


import co.elastic.clients.elasticsearch.*;

import co.elastic.clients.json.jsonb.*;
import co.elastic.clients.transport.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.example.elasticsearch.client.json.jackson.*;
import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.category.*;
import org.example.yelp.fusion.client.transport.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;

// build all requests before transporting
public abstract class AbstractRequestTestCase extends Assertions {

    @BeforeAll
    static void beforeAll() throws IOException {

        setOfValidBusinessIds = new HashSet<>();
        setOfNycRestaurants = new HashSet<>();
        restaurantsPerCategory = new HashMap<>();

        mapper = new JacksonJsonpMapper();

        initElasticsearchClient();

        initYelpFusionClient();

    }

    static String scheme = "https";
    protected static org.example.elasticsearch.client.json.jackson.JacksonJsonpMapper mapper;

    private static JsonbJsonpMapper jsonbJsonpMapper;
    protected static YelpFusionClient yelpClient;

    public static String timestampPipeline = "timestamp-pipeline"; // format:
    public static String geoPointIngestPipeline = "geo_location-pipeline";

    public static String indexNyc = "yelp-businesses-restaurants-nyc";

    public static StringBuilder requestSb;

    private static void initYelpFusionClient() throws IOException {

        String yelpFusionHost = "api.yelp.com";
        int port = 443;

        // create rest client
        RestClientBuilder restBuilder = RestClient.builder(new HttpHost(yelpFusionHost, port, scheme));
        Header[] defaultHeaders ={new BasicHeader("Authorization", "Bearer " + System.getenv("YELP_API_KEY"))};

        restBuilder.setDefaultHeaders(defaultHeaders);

        YelpRestTransport yelpTransport;

        try (RestClient restClient = restBuilder.build()) {
            // initialize Transport with a rest client and object mapper
            yelpTransport = new YelpRestTransport(
                    restClient,
                    mapper);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        yelpClient = new YelpFusionClient(yelpTransport);
    }


    public static ElasticsearchClient esClient;

    private static void initElasticsearchClient() {

        String host = "my-deployment-e7b376.es.us-west-1.aws.found.io";
        int port = 443;

        String apiKeyIdAndSecret = System.getenv("API_KEY_ID") + ":" + System.getenv("API_KEY_SECRET");


        String encodedApiKey = java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));

        PrintUtils.println(encodedApiKey);

        org.elasticsearch.client.RestClientBuilder builder = org.elasticsearch.client.RestClient.builder(
                new HttpHost(host, port, scheme));

        Header[] defaultHeaders = {new BasicHeader("Authorization", "ApiKey " + encodedApiKey)};

        builder.setDefaultHeaders(defaultHeaders);

        org.elasticsearch.client.RestClient restClient = builder.build();

        ElasticsearchTransport esTransport = new co.elastic.clients.transport.rest_client.RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());

        esClient = new ElasticsearchClient(esTransport);
    }

    public static String businessSearchEndpoint = "/businesses/search";

    public static String businessDetailsEndpoint = "/businesses";
    public static Set<String> setOfNycRestaurants;     // business id's in database with parent category: "restaurants"

    public static List<String> listOfCategories; // keep track of current category by index

    public static final int indexedCategoriesNycRestaurants = 309; // total sub categories of "restaurants" in new york city
    public static final int indexedRestaurantsNyc = 17773; // total restaurants in database for NYC

    public static final int indexedRestaurantsSF = 4410;   // total restaurants in SF

    public static final int indexedCategoriesSFRestaurants = 244; // total sub categories of "restaurants" in San Francisco
    public static Set<String> setOfValidBusinessIds;
    public static Map<String, List<String>> restaurantsPerCategory;
    public static Set<String> setOfCatsGreaterThanMax; // categories that require additional request parameters
}

