package com.example.client.json.jackson;

import co.elastic.clients.json.jackson.*;
import com.example.client.json.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import jakarta.json.spi.*;
import jakarta.json.stream.*;

import java.io.*;
import java.util.*;

public class JacksonJsonpMapper extends JsonpMapperBase {

    private final JacksonJsonProvider provider;
    private final ObjectMapper objectMapper;

    private JacksonJsonpMapper(ObjectMapper objectMapper, JacksonJsonProvider provider) {
        this.objectMapper = objectMapper;
        this.provider = provider;
    }

    public JacksonJsonpMapper(ObjectMapper objectMapper) {
        this(
                objectMapper
                        .configure(SerializationFeature.INDENT_OUTPUT, false)
                        .setSerializationInclusion(JsonInclude.Include.NON_NULL),
                // Creating the json factory from the mapper ensures it will be returned by JsonParser.getCodec()
                new JacksonJsonProvider(objectMapper.getFactory())
        );
    }

    public JacksonJsonpMapper() {
        this(new ObjectMapper());
    }

    @Override
    public <T> JsonpMapper withAttribute(String name, T value) {
        return new JacksonJsonpMapper(this.objectMapper, this.provider).addAttribute(name, value);
    }

    /**
     * Returns the underlying Jackson mapper.
     */
    public ObjectMapper objectMapper() {
        return this.objectMapper;
    }

    @Override
    public JsonProvider jsonProvider() {
        return provider;
    }

    @Override
    protected  <T> JsonpDeserializer<T> getDefaultDeserializer(Class<T> clazz) {
        return new JacksonValueParser<>(clazz);
    }

    @Override
    public <T> void serialize(T value, JsonGenerator generator) {

        if (!(generator instanceof JacksonJsonpGenerator)) {
            throw new IllegalArgumentException("Jackson's ObjectMapper can only be used with the JacksonJsonpProvider");
        }

        JsonpSerializer<T> serializer = findSerializer(value);
        if (serializer != null) {
            serializer.serialize(value, generator, this);
            return;
        }

        com.fasterxml.jackson.core.JsonGenerator jkGenerator = ((JacksonJsonpGenerator)generator).jacksonGenerator();
        try {
            objectMapper.writeValue(jkGenerator, value);
        } catch (IOException ioe) {
            throw JacksonUtils.convertException(ioe);
        }
    }

    private class JacksonValueParser<T> extends JsonpDeserializerBase<T> {

        private final Class<T> clazz;

        protected JacksonValueParser(Class<T> clazz) {
            super(EnumSet.allOf(JsonParser.Event.class));
            this.clazz = clazz;
        }

        @Override
        public T deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {

            if (!(parser instanceof JacksonJsonpParser)) {
                throw new IllegalArgumentException("Jackson's ObjectMapper can only be used with the JacksonJsonpProvider");
            }

            com.fasterxml.jackson.core.JsonParser jkParser = ((JacksonJsonpParser)parser).jacksonParser();

            try {
                return objectMapper.readValue(jkParser, clazz);
            } catch(IOException ioe) {
                throw JacksonUtils.convertException(ioe);
            }
        }
    }
}