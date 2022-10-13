package com.example.client.json;


import jakarta.json.spi.*;
import jakarta.json.stream.*;

import javax.annotation.*;

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
    public <T> T deserialize(JsonParser parser, Class<T> clazz) {
        return mapper.deserialize(parser, clazz);
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
    @Nullable
    public <T> T attribute(String name) {
        return mapper.attribute(name);
    }
}
