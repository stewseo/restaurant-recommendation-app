package org.example.elasticsearch.client.json;

import jakarta.json.stream.*;

import java.util.*;

public class UnexpectedJsonEventException extends JsonParsingException {
    public UnexpectedJsonEventException(JsonParser parser, JsonParser.Event event) {
        super("Unexpected JSON event '" + event + "'", parser.getLocation());
    }

    public UnexpectedJsonEventException(JsonParser parser, JsonParser.Event event, JsonParser.Event expected) {
        super("Unexpected JSON event '" + event + "' instead of '" + expected + "'", parser.getLocation());
    }

    public UnexpectedJsonEventException(JsonParser parser, JsonParser.Event event, EnumSet<JsonParser.Event> expected) {
        super("Unexpected JSON event '" + event + "' instead of '" + expected + "'", parser.getLocation());
    }
}