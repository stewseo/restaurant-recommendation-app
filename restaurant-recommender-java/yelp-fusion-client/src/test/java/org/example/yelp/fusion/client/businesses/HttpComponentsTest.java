package org.example.yelp.fusion.client.businesses;

import org.apache.http.*;

import org.apache.http.auth.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.message.*;
import org.apache.http.nio.protocol.*;

import org.example.elasticsearch.client.json.jackson.*;
import org.example.elasticsearch.client.transport.*;
import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.*;
import org.example.yelp.fusion.client.businesses.search.*;
import org.example.yelp.fusion.client.transport.*;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


import static org.assertj.core.api.Assertions.assertThat;

public class HttpComponentsTest extends AbstractRequestTestCase {

    private static final RequestOptions COMMON_OPTIONS;
    
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Authorization", "Bearer " + System.getenv("YELP_API_KEY"));

        COMMON_OPTIONS = builder.build();
    }

    @Test
    void authenticationBearerTest() throws IOException, URISyntaxException {
        assertThat(yelpClient).isNotNull();
        HttpHost host = new HttpHost("api.yelp.com",8443,"https");
        RestClientBuilder restBuilder = RestClient.builder(host);
        RestClient client = restBuilder.build();

        int offset = 0;
        double longitude = -122.43;
        double latitude = 38.33;
        String location = "SF";

        JacksonJsonpMapper mapper = new JacksonJsonpMapper();
        YelpFusionTransport transport = new YelpRestTransport(client, mapper);
        YelpFusionClient yelpClient = new YelpFusionClient(transport);

        BusinessSearchResponse<Business> request = yelpClient.search(s-> s
                        .location(location)
                        .longitude(longitude)
                        .latitude(latitude)
                        .categories("restaurants")
                        .offset(offset)
                        .limit(50)
                        .terms("restaurants"),
                Business.class);

    }  
    @Test
    void asyncTest() throws IOException, ExecutionException, InterruptedException {
        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();

        String requestHeader = "Authorization";
        String bearerAndToken = String.format("Bearer %s", System.getenv("YELP_API_KEY"));

        HttpGet request = new HttpGet("-H Authorization: "+  bearerAndToken + " http://api.yelp.com/businsesses/search/location=sf");

        Future<HttpResponse> future = client.execute(request, null);
        HttpResponse response = future.get();

        PrintUtils.green("HttpResponse: " + response.getStatusLine() + " headers: " + Arrays.toString(response.getAllHeaders()));
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo( 200);
        client.close();
    }

    @Test
    public void whenUseAuthenticationWithHttpAsyncClient_thenCorrect() throws Exception {
        CredentialsProvider provider = new BasicCredentialsProvider();
        
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("user", "pass");
        provider.setCredentials(AuthScope.ANY, creds);

        CloseableHttpAsyncClient client =
                HttpAsyncClients.custom().setDefaultCredentialsProvider(provider).build();
        client.start();

        HttpGet request = new HttpGet("http://api.yelp.com/businsesses/search/location=sf");
        Future<HttpResponse> future = client.execute(request, null);
        HttpResponse response = future.get();

        PrintUtils.green(response.getStatusLine().getStatusCode());
        client.close();
    }

    private static HttpResponse responseOrException(HttpAsyncRequestProducer requestProducer) throws Exception {
        final HttpUriRequest request = (HttpUriRequest) requestProducer.generateRequest();
        final HttpHost httpHost = requestProducer.getTarget();
        // return the desired status code or exception depending on the path
        switch (request.getURI().getPath()) {
            case "/soe" -> throw new SocketTimeoutException(httpHost.toString());
            case "/coe" -> throw new ConnectTimeoutException(httpHost.toString());
            case "/ioe" -> throw new IOException(httpHost.toString());
            case "/closed" -> throw new ConnectionClosedException();
            case "/handshake" -> throw new SSLHandshakeException("");
            case "/uri" -> throw new URISyntaxException("", "");
            case "/runtime" -> throw new RuntimeException();
            default -> {
                int statusCode = Integer.parseInt(request.getURI().getPath().substring(1));
                StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("http", 1, 1), statusCode, "");
                final HttpResponse httpResponse = new BasicHttpResponse(statusLine);
                // return the same body that was sent
                if (request instanceof HttpEntityEnclosingRequest) {
                    HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                    if (entity != null) {
                        assertTrue(entity.isRepeatable());
                        httpResponse.setEntity(entity);
                    }
                }
                // return the same headers that were sent
                httpResponse.setHeaders(request.getAllHeaders());
                return httpResponse;
            }
        }
    }
}
