//package com.example.client.yelp_fusion.businesses.category_endpoints;
//
//import co.elastic.clients.elasticsearch.core.*;
//import co.elastic.clients.elasticsearch.indices.*;
//import com.example.client.json.*;
//import com.example.client.yelp_fusion.businesses.*;
//import com.fasterxml.jackson.databind.*;
//import jakarta.json.*;
//import jakarta.json.spi.*;
//import jakarta.json.stream.*;
//import org.junit.jupiter.api.*;
//
//import javax.management.*;
//import java.io.*;
//import java.util.*;
//
//public class BusinessSearchRequestWithCategoriesTest extends AbstractRequestTestCase {
//
//    static int limit = 50;
//
//    List<String> restaurantCategories = List.of(
//            "japanese,sushi", "bbq", "korean", "italian", "steak",
//            "seafood", "chinese", "mexican", "pizza", "salad",
//            "brunch", "dinner", "lunch", "breakfast",
//            "burgers", "french", "indian",
//            "vietnamese,pho", "breakfast", "vegetarian", "vegan", "thai",
//            "steakhouses", "peruvian", "ramen", "udon", "bars");
//
//    List<String> districts = List.of("mission", "Marina/Cow Hollow", "old oakland", "inner sunset", "richmond", "japantown");
//
//    ObjectMapper mapper;
//
//    // set request parameters
//    @BeforeEach
//    void before(){
//        requestParams = new HashMap<>();
//        requestParams.put("location", "SF");
//        requestParams.put("latitude", "37.751586275");
//        requestParams.put("longitude", "-122.447721511");
//        requestParams.put("term", "restaurants");
//        requestParams.put("limit", String.valueOf(limit));
//    }
//
//    @Test
//    void initializeTransportTest() {
//
//    }
//    // initialize esClient that provides access to namespace esClient methods
//    // parameters: Transport(required), TransportOptions(optional)
//    @Test
//    void initializeClientTest() {
//
//    }
//
//    // set endpoint fields using Namespace.Builder
//    @Test
//    void initializeEndpointTest() {
//
//    }
//
//
//
//    @Test
//    void performRequestTest(){
//
//    }
//
//    @Test
//    void indexJsonFileTest() throws IOException {
//        mapper = new ObjectMapper();
//
//        JsonReader jsonReader = Json.createReader(new FileReader("C:\\Users\\seost\\repositories\\yelp-fusion-java\\src\\test\\resources\\categories.json"));
//        JsonObject jsonObject = jsonReader.readObject();
//
//
//        IndexResponse response = esClient.index(i -> i
//                .index("restaurants-index-000001") // restaurants-index-000001 alias
//                .id("categories") // Document ID
//                .document(jsonObject) // Request body
//        );
//
//    }
//
//    @Test
//    void getDetailedBusinessesTest() throws IOException {
//
//        requestParams.put("categories", restaurantCategory);
//
//        requestParams.forEach((k, v) -> {
//            if (requestUsingCurl.toString().endsWith("search")) {
//                requestUsingCurl.append("?")
//                        .append(k)
//                        .append("=")
//                        .append(v);
//
//            } else {
//                requestUsingCurl
//                        .append("&")
//                        .append(k)
//                        .append("=")
//                        .append(v);
//            }
//        });
//
//        JsonObject jsonObject = yelpTransport.performRequest(requestUsingCurl.toString()); // GetBusinessSearchResponse response = this.transport.performRequest(GetBusinessSearchRequest.Builder ...)
//
//        mapper = new ObjectMapper();
//
//        String loc = requestParams.get("location");
//
//        // index results 1-50
//        BusinessResponse businessResponse = mapper.readValue(jsonObject.toString(), BusinessResponse.class);
//        IndexResponse response = esClient.index(i -> i // Creates or updates a document in an index.
//                .index("restaurants-index-000001") // restaurants-index-000001 alias
//                .id(String.format("%s-restaurants-%s-results-1", loc, restaurantCategory)) // Document ID
//                .document(businessResponse) // Request body
//        );
//
//        System.out.println("doc id = " + response.id());
//        System.out.println("index = " + response.index());
//        System.out.println("result = " + response.result());
//        System.out.println("businessResponse.getTotal= " + businessResponse.getTotal());
//
//        int total = jsonObject.getInt("total"); // max results per page = 50
//
//
//        if (total > 50) {
//            ingestAllResults(total, restaurantCategory); // iterate results 51 - up to 1000
//        }
//    }
//
//    void iterateBusinessDetail(int total, String restaurantCategory) throws IOException {
//        // add request param for offset
//        requestParams.put("offset", String.valueOf(limit));
//        requestUsingCurl
//                .append("&")
//                .append("offset")
//                .append("=");
//
//        int length = requestUsingCurl.toString().length();
//
//        // if total results > max results, add category filter
//        // limit = max results per page, increment offset by limit, iterate until
//        for (int offset = limit; offset <= total + 50; offset += limit) {
//            String request = requestUsingCurl.toString() + offset;
//
//            JsonObject jsonObj = yelpTransport.performRequest(request);
//
//            BusinessResponse businessRes = mapper.readValue(jsonObj.toString(), BusinessResponse.class);
//
//            String docId = String.format("restaurants-%s%s", restaurantCategory, offset);
//
//            IndexResponse response = esClient.index(i -> i
//                    .index("restaurants-index-000001")
//                    .id(docId)
//                    .document(businessRes)
//            );
//            System.out.println("current offset = " + offset);
//        }
//    }
//
//
//    String restaurantCategory = restaurantCategories.get(0);
//
//    @Test
//    void prepareRequestWithRestaurantCategoryTest() throws IOException {
//
//        requestParams.put("categories", restaurantCategory);
//
//        requestParams.forEach((k, v) -> {
//            if (requestUsingCurl.toString().endsWith("search")) {
//                requestUsingCurl.append("?")
//                        .append(k)
//                        .append("=")
//                        .append(v);
//
//            } else {
//                requestUsingCurl
//                        .append("&")
//                        .append(k)
//                        .append("=")
//                        .append(v);
//            }
//        }); // search?location=SF&latitude=&longitude=&sort_by=distance&limit=50categories= iterate through list & offset = depends on response parameter: total
//
//        JsonObject jsonObject = yelpTransport.performRequest(requestUsingCurl.toString()); // GetBusinessSearchResponse response = this.transport.performRequest(GetBusinessSearchRequest.Builder ...)
//
//        mapper = new ObjectMapper();
//
//        String loc = requestParams.get("location");
//
//        // index results 1-50
//        BusinessResponse businessResponse = mapper.readValue(jsonObject.toString(), BusinessResponse.class);
//        IndexResponse response = esClient.index(i -> i // Creates or updates a document in an index.
//                .index("restaurants-index-000001") // restaurants-index-000001 alias
//                .id(String.format("%s-restaurants-%s-results-1", loc, restaurantCategory)) // Document ID
//                .document(businessResponse) // Request body
//        );
//
//        System.out.println("doc id = " + response.id());
//        System.out.println("index = " + response.index());
//        System.out.println("result = " + response.result());
//        System.out.println("businessResponse.getTotal= " + businessResponse.getTotal());
//
//        int total = jsonObject.getInt("total"); // max results per page = 50
//
//        if (total > 50) {
//            ingestAllResults(total, restaurantCategory); // iterate results 51 - up to 1000
//        }
//    }
//
//    void ingestAllResults(int total, String restaurantCategory) throws IOException {
//        // add request param for offset
//        requestParams.put("offset", String.valueOf(limit));
//        requestUsingCurl
//                .append("&")
//                .append("offset")
//                .append("=");
//
//        int length = requestUsingCurl.toString().length();
//
//        // if total results > max results, add category filter
//        // limit = max results per page, increment offset by limit, iterate until
//        for (int offset = limit; offset <= total + 50; offset += limit) {
//            String request = requestUsingCurl.toString() + offset;
//
//            JsonObject jsonObj = yelpTransport.performRequest(request);
//
//            BusinessResponse businessRes = mapper.readValue(jsonObj.toString(), BusinessResponse.class);
//
//            String docId = String.format("restaurants-%s%s", restaurantCategory, offset);
//
//            IndexResponse response = esClient.index(i -> i
//                    .index("restaurants-index-000001")
//                    .id(docId)
//                    .document(businessRes)
//            );
//            System.out.println("current offset = " + offset);
//        }
//    }
//
//
//    public static <T> String toJson(T value, JsonpMapper mapper) {
//        StringWriter sw = new StringWriter();
//        JsonProvider provider = mapper.jsonProvider();
//        JsonGenerator generator = provider.createGenerator(sw);
//        mapper.serialize(value, generator);
//        generator.close();
//        return sw.toString();
//    }
//
//    public static <T> T fromJson(String json, Class<T> clazz, JsonpMapper mapper) {
//        JsonParser parser = mapper.jsonProvider().createParser(new StringReader(json));
//        return mapper.deserialize(parser, clazz);
//    }
//
////    protected <T> T fromJson(String json, Class<T> clazz) {
////        return fromJson(json, clazz, mapper);
////    }
//
//
//    //C:\Users\seost\repositories\yelp-fusion-java\src\test\java\com\example\esClient\yelp_fusion\business
//    // C:\Users\seost\repositories\yelp-fusion-java\src\test\resources\com\example\esClient\yelp_fusion\business\processors.json
//    @Test
//    void createPipelineFromResourceTest() throws IOException {
//
//        InputStream processors = this.getClass()
//                .getResourceAsStream("processors.json");
//
//        System.out.println(processors);
//
//        esClient.ingest().putPipeline(i ->
//                i.id("businesses-restaurants-sf-01"));
//
//        CreateIndexRequest req = CreateIndexRequest.of(b -> b
//                .index("restaurants-index-000001")
//                .withJson(processors)
//        );
//
//        boolean created = esClient.indices().create(req).acknowledged();
//    }
//
//    protected static List<String> restaurantIds;
//
//
//    String index = "restaurants-index-000001";
//    String searchText = "perilla-san-francisco-2";
//
//    String searchText1 = "region";
//
//    String budinessId = "RNy3_hU1N2qyS5PVc9RaYQ";
//
//
//    @Test
//    void createNewDocTest() throws IOException {
//
//        String searchText = "bike";
//
//        SearchResponse<ObjectName> response = esClient.search(s -> s
//                        .index(index)
//                        .query(q -> q
//                                .match(t -> t
//                                        .field("id")
//                                        .query("RNy3_hU1N2qyS5PVc9RaYQ")
//                                )
//                        ),
//                ObjectName.class
//        );
//    }
//}