package org.example.elasticsearch.client.transport.endpoints;

public class BooleanResponse {
    private final boolean value;

    public BooleanResponse(boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }
}
