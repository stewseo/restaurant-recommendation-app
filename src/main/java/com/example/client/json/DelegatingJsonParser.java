package com.example.client.json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

public abstract class DelegatingJsonParser implements JsonParser {

    private final JsonParser parser;

    public DelegatingJsonParser(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public boolean hasNext() {
        return parser.hasNext();
    }

    @Override
    public Event next() {
        return parser.next();
    }

    @Override
    public String getString() {
        return parser.getString();
    }

    @Override
    public boolean isIntegralNumber() {
        return parser.isIntegralNumber();
    }

    @Override
    public int getInt() {
        return parser.getInt();
    }

    @Override
    public long getLong() {
        return parser.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return parser.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return parser.getLocation();
    }

    @Override
    public JsonObject getObject() {
        return parser.getObject();
    }

    @Override
    public JsonValue getValue() {
        return parser.getValue();
    }

    @Override
    public JsonArray getArray() {
        return parser.getArray();
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        return parser.getArrayStream();
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        return parser.getObjectStream();
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        return parser.getValueStream();
    }

    @Override
    public void skipArray() {
        parser.skipArray();
    }

    @Override
    public void skipObject() {
        parser.skipObject();
    }

    @Override
    public void close() {
        parser.close();
    }
}