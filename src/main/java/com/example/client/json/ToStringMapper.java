package com.example.client.json;

import jakarta.json.stream.JsonGenerator;

class ToStringMapper extends SimpleJsonpMapper {

    static final ToStringMapper INSTANCE = new ToStringMapper();

    private static final JsonpSerializer<?> toStringSerializer = new JsonpSerializer<Object>() {
        @Override
        public void serialize(Object value, JsonGenerator generator, JsonpMapper mapper) {
            if (value == null) {
                generator.writeNull();
            } else {
                generator.write(value.toString());
            }
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    protected <T> JsonpSerializer<T> getDefaultSerializer(T value) {
        return (JsonpSerializer<T>)toStringSerializer;
    }
}