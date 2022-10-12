package com.example.client._types;

import co.elastic.clients.json.*;
import co.elastic.clients.util.*;
import jakarta.json.stream.*;

import javax.annotation.*;
import java.util.*;
import java.util.function.*;

@JsonpDeserializable
public class ErrorCause implements JsonpSerializable {
    private final Map<String, JsonData> metadata;

    @Nullable
    private final String type;

    @Nullable
    private final String reason;

    @Nullable
    private final String stackTrace;

    @Nullable
    private final ErrorCause causedBy;

    private final List<ErrorCause> rootCause;

    private final List<ErrorCause> suppressed;

    // ---------------------------------------------------------------------------------------------

    private ErrorCause(ErrorCause.Builder builder) {

        this.metadata = ApiTypeHelper.unmodifiable(builder.metadata);

        this.type = builder.type;
        this.reason = builder.reason;
        this.stackTrace = builder.stackTrace;
        this.causedBy = builder.causedBy;
        this.rootCause = ApiTypeHelper.unmodifiable(builder.rootCause);
        this.suppressed = ApiTypeHelper.unmodifiable(builder.suppressed);

    }

    public static ErrorCause of(Function<ErrorCause.Builder, ObjectBuilder<ErrorCause>> fn) {
        return fn.apply(new ErrorCause.Builder()).build();
    }

    /**
     * Additional details about the error
     */
    public final Map<String, JsonData> metadata() {
        return this.metadata;
    }

    /**
     * The type of error
     * <p>
     * API name: {@code type}
     */
    @Nullable
    public final String type() {
        return this.type;
    }

    /**
     * A human-readable explanation of the error, in english
     * <p>
     * API name: {@code reason}
     */
    @Nullable
    public final String reason() {
        return this.reason;
    }

    /**
     * The server stack trace. Present only if the <code>error_trace=true</code>
     * parameter was sent with the request.
     * <p>
     * API name: {@code stack_trace}
     */
    @Nullable
    public final String stackTrace() {
        return this.stackTrace;
    }

    /**
     * API name: {@code caused_by}
     */
    @Nullable
    public final ErrorCause causedBy() {
        return this.causedBy;
    }

    /**
     * API name: {@code root_cause}
     */
    public final List<ErrorCause> rootCause() {
        return this.rootCause;
    }

    /**
     * API name: {@code suppressed}
     */
    public final List<ErrorCause> suppressed() {
        return this.suppressed;
    }

    /**
     * Serialize this object to JSON.
     */
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

    public static class Builder extends WithJsonObjectBuilderBase<ErrorCause.Builder> implements ObjectBuilder<ErrorCause> {
        @Nullable
        private Map<String, JsonData> metadata = new HashMap<>();

        /**
         * Additional details about the error
         * <p>
         * Adds all entries of <code>map</code> to <code>metadata</code>.
         */
        public final ErrorCause.Builder metadata(Map<String, JsonData> map) {
            this.metadata = _mapPutAll(this.metadata, map);
            return this;
        }

        /**
         * Additional details about the error
         * <p>
         * Adds an entry to <code>metadata</code>.
         */
        public final ErrorCause.Builder metadata(String key, JsonData value) {
            this.metadata = _mapPut(this.metadata, key, value);
            return this;
        }

        @Nullable
        private String type;

        @Nullable
        private String reason;

        @Nullable
        private String stackTrace;

        @Nullable
        private ErrorCause causedBy;

        @Nullable
        private List<ErrorCause> rootCause;

        @Nullable
        private List<ErrorCause> suppressed;

        /**
         * The type of error
         * <p>
         * API name: {@code type}
         */
        public final ErrorCause.Builder type(@Nullable String value) {
            this.type = value;
            return this;
        }

        /**
         * A human-readable explanation of the error, in english
         * <p>
         * API name: {@code reason}
         */
        public final ErrorCause.Builder reason(@Nullable String value) {
            this.reason = value;
            return this;
        }

        /**
         * The server stack trace. Present only if the <code>error_trace=true</code>
         * parameter was sent with the request.
         * <p>
         * API name: {@code stack_trace}
         */
        public final ErrorCause.Builder stackTrace(@Nullable String value) {
            this.stackTrace = value;
            return this;
        }

        /**
         * API name: {@code caused_by}
         */
        public final ErrorCause.Builder causedBy(@Nullable ErrorCause value) {
            this.causedBy = value;
            return this;
        }

        /**
         * API name: {@code caused_by}
         */
        public final ErrorCause.Builder causedBy(Function<ErrorCause.Builder, ObjectBuilder<ErrorCause>> fn) {
            return this.causedBy(fn.apply(new ErrorCause.Builder()).build());
        }

        /**
         * API name: {@code root_cause}
         * <p>
         * Adds all elements of <code>list</code> to <code>rootCause</code>.
         */
        public final ErrorCause.Builder rootCause(List<ErrorCause> list) {
            this.rootCause = _listAddAll(this.rootCause, list);
            return this;
        }

        /**
         * API name: {@code root_cause}
         * <p>
         * Adds one or more values to <code>rootCause</code>.
         */
        public final ErrorCause.Builder rootCause(ErrorCause value, ErrorCause... values) {
            this.rootCause = _listAdd(this.rootCause, value, values);
            return this;
        }

        /**
         * API name: {@code root_cause}
         * <p>
         * Adds a value to <code>rootCause</code> using a builder lambda.
         */
        public final ErrorCause.Builder rootCause(Function<ErrorCause.Builder, ObjectBuilder<ErrorCause>> fn) {
            return rootCause(fn.apply(new ErrorCause.Builder()).build());
        }

        /**
         * API name: {@code suppressed}
         * <p>
         * Adds all elements of <code>list</code> to <code>suppressed</code>.
         */
        public final ErrorCause.Builder suppressed(List<ErrorCause> list) {
            this.suppressed = _listAddAll(this.suppressed, list);
            return this;
        }

        /**
         * API name: {@code suppressed}
         * <p>
         * Adds one or more values to <code>suppressed</code>.
         */
        public final ErrorCause.Builder suppressed(ErrorCause value, ErrorCause... values) {
            this.suppressed = _listAdd(this.suppressed, value, values);
            return this;
        }

        /**
         * API name: {@code suppressed}
         * <p>
         * Adds a value to <code>suppressed</code> using a builder lambda.
         */
        public final ErrorCause.Builder suppressed(Function<ErrorCause.Builder, ObjectBuilder<ErrorCause>> fn) {
            return suppressed(fn.apply(new ErrorCause.Builder()).build());
        }

        @Override
        protected ErrorCause.Builder self() {
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

    // ---------------------------------------------------------------------------------------------

    /**
     * Json deserializer for {@link ErrorCause}
     */
    public static final JsonpDeserializer<ErrorCause> _DESERIALIZER = ObjectBuilderDeserializer.lazy(ErrorCause.Builder::new,
            ErrorCause::setupErrorCauseDeserializer);

    protected static void setupErrorCauseDeserializer(ObjectDeserializer<ErrorCause.Builder> op) {

        op.add(ErrorCause.Builder::type, JsonpDeserializer.stringDeserializer(), "type");
        op.add(ErrorCause.Builder::reason, JsonpDeserializer.stringDeserializer(), "reason");
        op.add(ErrorCause.Builder::stackTrace, JsonpDeserializer.stringDeserializer(), "stack_trace");
        op.add(ErrorCause.Builder::causedBy, ErrorCause._DESERIALIZER, "caused_by");
        op.add(ErrorCause.Builder::rootCause, JsonpDeserializer.arrayDeserializer(ErrorCause._DESERIALIZER), "root_cause");
        op.add(ErrorCause.Builder::suppressed, JsonpDeserializer.arrayDeserializer(ErrorCause._DESERIALIZER), "suppressed");

        op.setUnknownFieldHandler((builder, name, parser, mapper) -> {
            builder.metadata(name, JsonData._DESERIALIZER.deserialize(parser, mapper));
        });
        op.shortcutProperty("reason");

    }

}
