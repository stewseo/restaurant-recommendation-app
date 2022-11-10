package org.example.elasticsearch.client.util;
import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;
import org.example.lowlevel.restclient.*;


public abstract class WithJsonObjectBuilderBase<B> extends ObjectBuilderBase implements WithJson<B> {

    protected abstract B self();

    @Override
    public B withJson(JsonParser parser, JsonpMapper mapper) {
        PrintUtils.cyan("WithJsonObjectBuilderBase.withJson ");
        JsonpDeserializer<?> classDeser = JsonpMapperBase.findDeserializer(this.getClass().getEnclosingClass());
        if (classDeser == null) {
            throw new IllegalArgumentException("Class " + this.getClass().getEnclosingClass() + " cannot be read from JSON");
        }

        // Generic parameters are always deserialized to JsonData unless the parent mapper can provide a deserializer
        mapper = new WithJsonObjectBuilderBase.WithJsonMapper(mapper);

        @SuppressWarnings("unchecked")
        ObjectDeserializer<B> builderDeser = (ObjectDeserializer<B>) DelegatingDeserializer.unwrap(classDeser);
        return builderDeser.deserialize(self(), parser, mapper, parser.next());
    }

    private static class WithJsonMapper extends DelegatingJsonpMapper {
        WithJsonMapper(JsonpMapper parent) {
            super(parent);
        }

        @Override
        public <T> T attribute(String name) {
            PrintUtils.cyan("attribute " + name);
            T attr = mapper.attribute(name);
            if (attr == null && name.startsWith("co.elastic.clients:Deserializer")) {
                @SuppressWarnings("unchecked")
                T result = (T) JsonData._DESERIALIZER;
                return result;
            } else {
                return attr;
            }
        }

        @Override
        public <T> JsonpMapper withAttribute(String name, T value) {
            return new WithJsonObjectBuilderBase.WithJsonMapper(this.mapper.withAttribute(name, value));
        }
    }
}