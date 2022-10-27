package org.example.elasticsearch.client.transport;

import java.io.*;

public class TransportException extends IOException {

    private final String endpointId;

    public TransportException(String message, String endpointId) {
        this(message, endpointId, null);
    }

    public TransportException(String message, String endpointId, Throwable cause) {
        super(endpointId == null ? message : "[" + endpointId + "] " + message, cause);
        this.endpointId = endpointId;
    }

    /**
     * Identifier of the API endpoint that caused the exception, if known.
     */
    String getEndpointId() {
        return endpointId;
    }
}