//package com.example.client.yelp_fusion.businesses.business_endpoints;
//
//import co.elastic.clients.elasticsearch._types.*;
//import co.elastic.clients.elasticsearch.cat.*;
//import co.elastic.clients.elasticsearch.core.*;
//import co.elastic.clients.elasticsearch.indices.*;
//import co.elastic.clients.transport.endpoints.*;
//import com.example.client.yelp_fusion.businesses.*;
//import jakarta.json.*;
//import org.junit.jupiter.api.*;
//
//import java.io.*;
//import java.util.*;
//
//import static org.assertj.core.api.AssertionsForClassTypes.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class BusinessSearchRequestWithAttributesTest extends AbstractRequestTestCase {
//
//    private List<String> listOfAttributes = List.of("hot_and_new", "request_a_quote", "reservation", "waitlist_reservation", "deals", "gender_neutral_restrooms", "open_to_all", "wheelchair_accessible");
//
//    @Test
//    void requestSffRestaurantsByAttribute() throws IOException {
//
//        String index = "yelp-businesses-attributes-01";
//
//        // Check that it actually exists. Example of a boolean response
//        BooleanResponse existsResponse = esClient.indices().exists(b -> b.index(index));
//
//        if (!existsResponse.value()) {
//            // Create an index
//            CreateIndexResponse createIndexResponse = esClient.indices().create(b -> b
//                    .index(index)
//            );
//        }
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
//        }); // v3/businesses/search?location=SF&attributes=businessAttribute[i]&radius=40000&limit=50
//        for (String businessAttribute : List.of("open_to_all", "wheelchair_accessible")) {
//            String request = requestUsingCurl.toString() + "&" + "attributes=" + businessAttribute;
//            System.out.println(request);
//
//            JsonObject jsonObject = yelpClient.business(request);
//            if (jsonObject != null) {
//                assertThat(
//                        jsonObject.get("businesses").asJsonArray().size())
//                        .isGreaterThanOrEqualTo(1);
//
//                String documentId = String.format("yelp-business-attribute-%s-01", businessAttribute);
//
//
//                BusinessResponse businessResponse = mapper.objectMapper().readValue(jsonObject.toString(), BusinessResponse.class);
//
//                IndexResponse indexResponse = esClient.index(b -> b
//                        .index(index) // index: "yelp-businesses-attributes"
//                        .id(documentId) // Document ID: "yelp-business-attribute-%s-01"
//                        .document(businessResponse) // Request body.
//                        .refresh(Refresh.True) // Make it visible for search
//                );
//
//
//                System.out.println("doc id = " + indexResponse.id());
//                System.out.println("index = " + indexResponse.index());
//                System.out.println("result = " + indexResponse.result());
//                System.out.println("businessResponse.getTotal= " + businessResponse.getTotal());
//
//                int total = jsonObject.getInt("total"); // max results per page = 50
//
//                // while current cursor >= total results
//                if (total > 50) {
//                    yelpClient.getBusinesses(total, businessAttribute, index,
//                            documentId); // iterate results 51 - up to 1000
//                }
//            }
//        }
//    }
//
////    // while current cursor >= total results
////    void documentAllResults(int total, String restaurantCategory, String index, String documentId) throws IOException {
////        // add request param for offset
////        StringBuilder requestWithOffset = new StringBuilder(yelpRequest.toString());
////
////        requestWithOffset
////                .append("&")
////                .append("offset")
////                .append("=");
////
////        int length = requestWithOffset.toString().length();
////
////        String docId =
////                documentId.substring(0, documentId.length()-1) + "-";
////
////        // if total results > max results, add category filter
////        // limit = max results per page, increment offset by limit, iterate until
////        for (int offset = limit; offset <= total + 50; offset += limit) {
////            String request = requestWithOffset.toString() + offset;
////
////            JsonObject jsonObj = performRequest(request);
////
////            BusinessResponse businessRes = mapper.objectMapper().readValue(jsonObj.toString(), BusinessResponse.class);
////
////            String resultCursor = docId + offset;
////
////            IndexResponse response = esClient.index(i -> i
////                    .index(index)
////                    .id(resultCursor)
////                    .document(businessRes)
////            );
////            System.out.println("current offset = " + offset);
////        }
////    }
//
//    @Test
//    void searchDocumentsByAttributeTest() throws IOException {
//        String index = "yelp-attributes";
//        String documentId = "yelp-business-attribute-*";
//        // Check auto-created mapping
//        GetMappingResponse mapping = esClient.indices().getMapping(b -> b.index(index));
//
//        assertThat(mapping.get(index)).isEqualTo("test");
////                .mappings().properties().get("intValue")._kind()
//
//        esClient.security().updateApiKey(k->
//                k.id("api-key")
//        );
//
//        // Query by id
//        BusinessResponse response = esClient.get(b -> b
//                        .index(index)
//                        .id(documentId)
//                , BusinessResponse.class
//        ).source();
//
//        assertEquals(1337, response.getBusinesses().length);
//        assertEquals(1, response.getTotal());
//    }
//
//    @Test
//    public void testCatRequest() throws IOException {
//        // Cat requests should have the "format=json" added by the esTransport
//
//        NodesResponse nodes = esClient.cat().nodes(_0 -> _0);
//        System.out.println(AbstractRequestTestCase.toJson(nodes, esClient._transport().jsonpMapper()));
//
//        assertEquals(3, nodes.valueBody().size());
//        assertEquals("*", nodes.valueBody().get(0).master());
//    }
//
//    @Test
//    public void testException() {
//
//    }
//}
//
//
