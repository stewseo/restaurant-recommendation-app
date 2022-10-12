package com.example.client.json;

import jakarta.json.stream.*;

public interface JsonpSerializable {
    void serialize(JsonGenerator generator, JsonpMapper mapper);
}