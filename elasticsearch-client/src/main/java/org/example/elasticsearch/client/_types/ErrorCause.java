package org.example.elasticsearch.client._types;

import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.util.*;


import java.util.*;
import java.util.function.*;
@JsonpDeserializable
public class ErrorCause implements JsonpSerializable {
    private final Map<String, JsonData> metadata;


    private final String type;


    private final String reason;


    private final String stackTrace;


    private final ErrorCause causedBy;

    private final List<ErrorCause> rootCause;

    private final List<ErrorCause> suppressed;

    // ---------------------------------------------------------------------------------------------

    private ErrorCause(Builder builder) {

        this.metadata = ApiTypeHelper.unmodifiable(builder.metadata);

        this.type = builder.type;
        this.reason = builder.reason;
        this.stackTrace = builder.stackTrace;
        this.causedBy = builder.causedBy;
        this.rootCause = ApiTypeHelper.unmodifiable(builder.rootCause);
        this.suppressed = ApiTypeHelper.unmodifiable(builder.suppressed);

    }

    public static ErrorCause of(Function<Builder, ObjectBuilder<ErrorCause>> fn) {
        return fn.apply(new Builder()).build();
    }

    public final Map<String, JsonData> metadata() {
        return this.metadata;
    }


    
    public final String type() {
        return this.type;
    }


    
    public final String reason() {
        return this.reason;
    }

    
    public final String stackTrace() {
        return this.stackTrace;
    }


    
    public final ErrorCause causedBy() {
        return this.causedBy;
    }

    public final List<ErrorCause> rootCause() {
        return this.rootCause;
    }


    public final List<ErrorCause> suppressed() {
        return this.suppressed;
    }

    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {

        for (Map.Entry<String, JsonData> item0 : this.metadata.entrySet()) {
            generator.writeKey(item0.getKey());
            item0.getValue().serialize(generator, mapper);

        }

        if (this.type != null) {
            generator.writeKey("type");
            generator.write(this.type);

        }
        if (this.reason != null) {
            generator.writeKey("reason");
            generator.write(this.reason);

        }
        if (this.stackTrace != null) {
            generator.writeKey("stack_trace");
            generator.write(this.stackTrace);

        }
        if (this.causedBy != null) {
            generator.writeKey("caused_by");
            this.causedBy.serialize(generator, mapper);

        }
        if (ApiTypeHelper.isDefined(this.rootCause)) {
            generator.writeKey("root_cause");
            generator.writeStartArray();
            for (ErrorCause item0 : this.rootCause) {
                item0.serialize(generator, mapper);

            }
            generator.writeEnd();

        }
        if (ApiTypeHelper.isDefined(this.suppressed)) {
            generator.writeKey("suppressed");
            generator.writeStartArray();
            for (ErrorCause item0 : this.suppressed) {
                item0.serialize(generator, mapper);

            }
            generator.writeEnd();

        }

    }

    @Override
    public String toString() {
        return JsonpUtils.toString(this);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Builder for {@link ErrorCause}.
     */

    public static class Builder extends WithJsonObjectBuilderBase<Builder> implements ObjectBuilder<ErrorCause> {

        private Map<String, JsonData> metadata = new HashMap<>();


        public final Builder metadata(Map<String, JsonData> map) {
            this.metadata = _mapPutAll(this.metadata, map);
            return this;
        }

        /**
         * Additional details about the error
         * <p>
         * Adds an entry to <code>metadata</code>.
         */
        public final Builder metadata(String key, JsonData value) {
            this.metadata = _mapPut(this.metadata, key, value);
            return this;
        }


        private String type;


        private String reason;


        private String stackTrace;

        
        private ErrorCause causedBy;

        
        private List<ErrorCause> rootCause;

        
        private List<ErrorCause> suppressed;

        /**
         * The type of error
         * <p>
         * API name: {@code type}
         */
        public final Builder type( String value) {
            this.type = value;
            return this;
        }


        public final Builder reason( String value) {
            this.reason = value;
            return this;
        }

        public final Builder stackTrace( String value) {
            this.stackTrace = value;
            return this;
        }

        /**
         * API name: {@code caused_by}
         */
        public final Builder causedBy( ErrorCause value) {
            this.causedBy = value;
            return this;
        }

        /**
         * API name: {@code caused_by}
         */
        public final Builder causedBy(Function<Builder, ObjectBuilder<ErrorCause>> fn) {
            return this.causedBy(fn.apply(new Builder()).build());
        }

        /**
         * API name: {@code root_cause}
         * <p>
         * Adds all elements of <code>list</code> to <code>rootCause</code>.
         */
        public final Builder rootCause(List<ErrorCause> list) {
            this.rootCause = _listAddAll(this.rootCause, list);
            return this;
        }

        /**
         * API name: {@code root_cause}
         * <p>
         * Adds one or more values to <code>rootCause</code>.
         */
        public final Builder rootCause(ErrorCause value, ErrorCause... values) {
            this.rootCause = _listAdd(this.rootCause, value, values);
            return this;
        }

        /**
         * API name: {@code root_cause}
         * <p>
         * Adds a value to <code>rootCause</code> using a builder lambda.
         */
        public final Builder rootCause(Function<Builder, ObjectBuilder<ErrorCause>> fn) {
            return rootCause(fn.apply(new Builder()).build());
        }

        /**
         * API name: {@code suppressed}
         * <p>
         * Adds all elements of <code>list</code> to <code>suppressed</code>.
         */
        public final Builder suppressed(List<ErrorCause> list) {
            this.suppressed = _listAddAll(this.suppressed, list);
            return this;
        }

        /**
         * API name: {@code suppressed}
         * <p>
         * Adds one or more values to <code>suppressed</code>.
         */
        public final Builder suppressed(ErrorCause value, ErrorCause... values) {
            this.suppressed = _listAdd(this.suppressed, value, values);
            return this;
        }

        /**
         * API name: {@code suppressed}
         * <p>
         * Adds a value to <code>suppressed</code> using a builder lambda.
         */
        public final Builder suppressed(Function<Builder, ObjectBuilder<ErrorCause>> fn) {
            return suppressed(fn.apply(new Builder()).build());
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds a {@link ErrorCause}.
         *
         * @throws NullPointerException
         *             if some of the required fields are null.
         */
        public ErrorCause build() {
            _checkSingleUse();

            return new ErrorCause(this);
        }
    }


    public static final JsonpDeserializer<ErrorCause> _DESERIALIZER = ObjectBuilderDeserializer.lazy(Builder::new,
            ErrorCause::setupErrorCauseDeserializer);

    protected static void setupErrorCauseDeserializer(ObjectDeserializer<Builder> op) {

        op.add(Builder::type, JsonpDeserializer.stringDeserializer(), "type");
        op.add(Builder::reason, JsonpDeserializer.stringDeserializer(), "reason");
        op.add(Builder::stackTrace, JsonpDeserializer.stringDeserializer(), "stack_trace");
        op.add(Builder::causedBy, ErrorCause._DESERIALIZER, "caused_by");
        op.add(Builder::rootCause, JsonpDeserializer.arrayDeserializer(ErrorCause._DESERIALIZER), "root_cause");
        op.add(Builder::suppressed, JsonpDeserializer.arrayDeserializer(ErrorCause._DESERIALIZER), "suppressed");

        op.setUnknownFieldHandler((builder, name, parser, mapper) -> {
            builder.metadata(name, JsonData._DESERIALIZER.deserialize(parser, mapper));
        });
        op.shortcutProperty("reason");

    }

}
