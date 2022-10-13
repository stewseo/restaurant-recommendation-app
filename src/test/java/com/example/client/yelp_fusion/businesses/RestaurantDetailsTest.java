package com.example.client.yelp_fusion.businesses;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.*;
import co.elastic.clients.elasticsearch.core.search.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

public class RestaurantDetailsTest extends AbstractRequestTestCase {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantsByCategoryTest.class);

    private static int limit = 50; // max businesses per page

    private static List<String> listOfBusinesses;

    private static Map<String, List<Business>> mapOfCategories; // group businesses by key

    // Get all documents yelp-businesses-restaurants-sf
    // Get all Business id's

    static void setup() throws IOException {
        String index = "yelp-businesses-sf-restaurants";

        // all documents with field: id value greater than or equal to 0

        List<String> priceList = List.of("$", "$$", "$$$", "$$$$");

        listOfBusinesses = new ArrayList<>();

        Set<String> setOfBusinessesIds = new HashSet<>();
        // loop for each $
        for (String price : priceList) {
            SearchResponse<Business> response = esClient.search(s -> s
                            .index(index)
                            .query(q -> q
                                    .match(t -> t
                                            .field("price.keyword")
                                            .query(price)
                                    )
                            )
                            .size(5000),
                    Business.class
            );


            TotalHits total = response.hits().total();

            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;
            PrintUtils.titleCyan("total hits: " + total);

            if (isExactResult) {
                PrintUtils.titleRed("There are " + total.value() + " results");
            } else {
                PrintUtils.titleRed("There are more than " + total.value() + " results");
            }
            List<Hit<Business>> hits = response.hits().hits();

            PrintUtils.titleRed("Doc id: " + response.hits().hits().size());

            for (Hit<Business> hit : hits) {
                Business business = hit.source();
                PrintUtils.titleRed("Doc id: " + hit.id() + "Total businesses: " + business.getId() + ", score " + hit.score());
                Map<String, InnerHitsResult> innerHits = hit.innerHits();

                setOfBusinessesIds.add(business.getId());
            }
        }
        PrintUtils.titleGreen("number of business ids in set: " + setOfBusinessesIds.size());
        listOfBusinesses = new ArrayList<>(setOfBusinessesIds);
        PrintUtils.titleGreen("number of business ids in list: " + listOfBusinesses.size());
    }

    @Test
    void testWithHttp() throws IOException {

        URL url = new URL("https://yelp.api.com/v3/businesses/search/location=sf");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();



        conn.setRequestProperty("Authorization","Bearer "+ System.getenv("YELP_API_KEY"));

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String output;

        StringBuffer response = new StringBuffer();


        while ((output = in.readLine()) != null) {
            System.out.println("Response:-" + output.toString());
            ////you will get output in "output.toString()" ,Use it however you like
        }
        in.close();
    }
    @Test
    void estestApache() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String apiKeyIdAndSecret = System.getenv("API_KEY_ID") + ":" + System.getenv("API_KEY_SECRET");
        String encodedApiKey = java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));
        HttpGet request = new HttpGet("https://my-deployment-170edf.es.us-west-1.aws.found.io");

        request.addHeader(HttpHeaders.AUTHORIZATION, " ApiKey " + encodedApiKey);

        CloseableHttpResponse response = httpClient.execute(request);
        // Get HttpResponse Status
        System.out.println(response.getProtocolVersion());              // HTTP/1.1
        System.out.println(response.getStatusLine().getStatusCode());   // 200
        System.out.println(response.getStatusLine().getReasonPhrase()); // OK
        System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // return it as a String
            String result = EntityUtils.toString(entity);
            System.out.println(result);
        }

    }

    @Test
    void httpTest() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("https://yelp.api.com/v3/businesses/search/location=sf");
        String apiKeyIdAndSecret = System.getenv("YELP_API_KEY");
        String encodedApiKey = java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));

        DefaultHttpClient httpclient = new DefaultHttpClient();

        CloseableHttpResponse response = httpClient.execute(request);

        // Get HttpResponse Status
        System.out.println(response.getProtocolVersion());              // HTTP/1.1
        System.out.println(response.getStatusLine().getStatusCode());   // 200
        System.out.println(response.getStatusLine().getReasonPhrase()); // OK
        System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // return it as a String
            String result = EntityUtils.toString(entity);
            System.out.println(result);
        }

    }

    @Test
    void requestBusinessDetailsTest() throws IOException {
        PrintUtils.titleCyan("Number of businesses added: " + listOfBusinesses.size());

        for (String businessId : listOfBusinesses) {

            // reset offset, total hits, and max offset
            int totalHits = 0;
            int offset = 0;
            int maxOffset = 0;

            PrintUtils.titleGreen("Current business id: " + businessId);

            String index = "yelp-business-details";

            BusinessDetailsRequest businessDetailsRequest = new BusinessDetailsRequest.Builder()
                    .offset(offset) // update offset to get next 50 results
                    .id(businessId) // required id for business details endpoint
                    .build();

            // submit the request to v3/businesses/<id>
            BusinessDetailsResponse businessDetailsResponse =
                    yelpClient.businessDetails(businessDetailsRequest); //submit the request to yelp


            if (businessDetailsResponse.id != null) {

                BulkRequest.Builder br = new BulkRequest.Builder();

                String docId = String.format(
                        "yelp-business-details-%s", // yelp-businesses-restaurants-pizza-<business id>
                        businessId); // yelp business id

                br.operations(op -> op
                        .index(idx -> idx
                                .index(index)
                                .id(docId)
                                .document(businessDetailsResponse)
                        )
                );

                BulkResponse result = esClient.bulk(br.build());
                PrintUtils.titleCyan("indexing doc: " + docId + " alias: " + businessDetailsResponse.alias);
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
    }
}
