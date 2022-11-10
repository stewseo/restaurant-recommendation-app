package org.example.elasticsearch.client.transport;

import java.util.*;

public interface TransportInfo {

    String requestUrl();
    byte[] requestBody();
    String requestBodyText();
    // request headers

    int responseStatusCode();
    byte[] responseBody();
    String responseBodyText();
    // response headers

    List<String> warnings();
}