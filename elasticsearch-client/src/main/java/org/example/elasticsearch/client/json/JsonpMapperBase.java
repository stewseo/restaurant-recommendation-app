package org.example.elasticsearch.client.json;

import jakarta.json.*;
import jakarta.json.stream.*;

import java.lang.reflect.*;
import java.util.*;

public abstract class JsonpMapperBase implements JsonpMapper {

    protected abstract <T> JsonpDeserializer<T> getDefaultDeserializer(Class<T> clazz);

    private Map<String, Object> attributes;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T attribute(String name) {
        return attributes == null ? null : (T)attributes.get(name);
    }


    protected JsonpMapperBase addAttribute(String name, Object value) {
        if (attributes == null) {
            this.attributes = Collections.singletonMap(name, value);
        } else {
            Map<String, Object> newAttrs = new HashMap<>(attributes.size() + 1);
            newAttrs.putAll(attributes);
            newAttrs.put(name, value);
            this.attributes = newAttrs;
        }
        return this;
    }

    @Override
    public <T> T deserialize(JsonParser parser, Class<T> clazz) {
        JsonpDeserializer<T> deserializer = findDeserializer(clazz);
        if (deserializer != null) {
            return deserializer.deserialize(parser, this);
        }

        return getDefaultDeserializer(clazz).deserialize(parser, this);
    }


    @SuppressWarnings("unchecked")
    public static <T> JsonpDeserializer<T> findDeserializer(Class<T> clazz) {
        JsonpDeserializable annotation = clazz.getAnnotation(JsonpDeserializable.class);
        if (annotation != null) {
            try {
                Field field = clazz.getDeclaredField(annotation.field());
                return (JsonpDeserializer<T>)field.get(null);
            } catch (Exception e) {
                throw new RuntimeException("No deserializer found in '" + clazz.getName() + "." + annotation.field() + "'");
            }
        }

        if (clazz == Void.class) {
            return (JsonpDeserializer<T>)JsonpDeserializerBase.VOID;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> JsonpSerializer<T> findSerializer(T value) {
        Class<?> valueClass = value.getClass();
        if (JsonpSerializable.class.isAssignableFrom(valueClass)) {
            return (JsonpSerializer<T>) JsonpSerializableSerializer.INSTANCE;
        }

        if (JsonValue.class.isAssignableFrom(valueClass)) {
            return (JsonpSerializer<T>) JsonpValueSerializer.INSTANCE;
        }

        return null;
    }

    protected static class JsonpSerializableSerializer<T extends JsonpSerializable> implements JsonpSerializer<T> {
        @Override
        public void serialize(T value, JsonGenerator generator, JsonpMapper mapper) {
            value.serialize(generator, mapper);
        }

        protected static final JsonpSerializer<?> INSTANCE = new JsonpSerializableSerializer<>();

    }

    protected static class JsonpValueSerializer implements JsonpSerializer<JsonValue> {
        @Override
        public void serialize(JsonValue value, JsonGenerator generator, JsonpMapper mapper) {
            generator.write(value);
        }

        protected static final JsonpSerializer<?> INSTANCE = new JsonpValueSerializer();

    }

}