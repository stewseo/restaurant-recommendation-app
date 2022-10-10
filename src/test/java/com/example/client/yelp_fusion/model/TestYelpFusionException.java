package com.example.client.yelp_fusion.model;

import co.elastic.clients.elasticsearch._types.*;

public class TestYelpFusionException extends RuntimeException {
    private final ErrorResponse response;
    private final String endpointId;

    public TestYelpFusionException(String endpointId, ErrorResponse response) {
        super("[" + endpointId + "] failed: [" + response.error().type() + "] " + response.error().reason());
        this.response = response;
        this.endpointId = endpointId;
    }

    /**
     * Identifier of the API endpoint that failed to be called.
     */
    public String endpointId() {
        return this.endpointId;
    }

    /**
     * The error response sent
     */
    public ErrorResponse response() {
        return this.response;
    }

    /**
     * The cause of the error. Shortcut for {@code response().error()}.
     */
    public ErrorCause error() {
        return this.response.error();
    }

    /**
     * Status code returned by Elasticsearch. Shortcut for
     * {@code response().status()}.
     */
    public int status() {
        return this.response.status();
    }
}
