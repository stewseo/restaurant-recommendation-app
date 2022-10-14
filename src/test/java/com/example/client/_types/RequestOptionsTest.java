package com.example.client._types;

import com.example.client.util.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

public class RequestOptionsTest {
    private static final Logger logger = LoggerFactory.getLogger(RequestOptionsTest.class);

    private static com.example.client._types.RequestOptions authorizationHeader;
    @Test
    static void requestOptionsTest(){
        // Initialize RequestOptions options object with a header class field
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Authorization", "Bearer " + System.getenv("YELP_API_KEY"));
        PrintUtils.titleGreen("Initializing Request Test Case");
        logger.info("RequestOptions AUTHORIZATION_HEADER = {}", builder);
        authorizationHeader = builder.build();
    }
}
