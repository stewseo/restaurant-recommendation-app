package com.example.client.transport;


import com.example.client._types.*;

public class _Exception extends RuntimeException {

    private final ErrorResponse response;
    private final String endpointId;

    public _Exception(String endpointId, ErrorResponse response) {
        super("[" + endpointId + "] failed: [" + response.error() + "] " + response.error());
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
     * The error response sent by Elasticsearch
     */
    public ErrorResponse response() {
        return this.response;
    }

    /**
     * The cause of the error. Shortcut for {@code response().error()}.
     */
    public String error() {
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
