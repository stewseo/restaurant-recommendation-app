package org.example.elasticsearch.client.json;


import jakarta.json.spi.*;
import jakarta.json.stream.*;

import java.lang.reflect.*;

public abstract class DelegatingJsonpMapper implements JsonpMapper {

    protected final JsonpMapper mapper;

    public DelegatingJsonpMapper(JsonpMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public JsonProvider jsonProvider() {
        return mapper.jsonProvider();
    }

    @Override
    public <T> T deserialize(JsonParser parser, Type type) {
        return mapper.deserialize(parser, type);
    }
    @Override
    public <T> void serialize(T value, JsonGenerator generator) {
        mapper.serialize(value, generator);
    }

    @Override
    public boolean ignoreUnknownFields() {
        return mapper.ignoreUnknownFields();
    }

    @Override
    public <T> T attribute(String name) {
        return mapper.attribute(name);
    }
}
