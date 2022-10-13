package com.example.client._types;

import co.elastic.clients.json.*;
import co.elastic.clients.util.*;
import jakarta.json.stream.*;

import java.util.function.*;

@co.elastic.clients.json.JsonpDeserializable
public class ErrorResponse implements co.elastic.clients.json.JsonpSerializable {
    private final ErrorCause error;

    private final int status;

    // ---------------------------------------------------------------------------------------------

    private ErrorResponse(ErrorResponse.Builder builder) {

        this.error = co.elastic.clients.util.ApiTypeHelper.requireNonNull(builder.error, this, "error");
        this.status = co.elastic.clients.util.ApiTypeHelper.requireNonNull(builder.status, this, "status");

    }

    public static ErrorResponse of(Function<ErrorResponse.Builder, co.elastic.clients.util.ObjectBuilder<ErrorResponse>> fn) {
        return fn.apply(new ErrorResponse.Builder()).build();
    }

    /**
     * Required - API name: {@code error}
     */
    public final ErrorCause error() {
        return this.error;
    }

    /**
     * Required - API name: {@code status}
     */
    public final int status() {
        return this.status;
    }

    /**
     * Serialize this object to JSON.
     */
    public void serialize(JsonGenerator generator, co.elastic.clients.json.JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, co.elastic.clients.json.JsonpMapper mapper) {

        generator.writeKey("error");
        this.error.serialize(generator, mapper);

        generator.writeKey("status");
        generator.write(this.status);

    }

    @Override
    public String toString() {
        return co.elastic.clients.json.JsonpUtils.toString(this);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Builder for {@link ErrorResponse}.
     */

    public static class Builder extends co.elastic.clients.util.WithJsonObjectBuilderBase<ErrorResponse.Builder> implements co.elastic.clients.util.ObjectBuilder<ErrorResponse> {
        private ErrorCause error;

        private Integer status;

        /**
         * Required - API name: {@code error}
         */
        public final ErrorResponse.Builder error(ErrorCause value) {
            this.error = value;
            return this;
        }

        /**
         * Required - API name: {@code error}
         */
        public final ErrorResponse.Builder error(Function<ErrorCause.Builder, ObjectBuilder<ErrorCause>> fn) {
            return this.error(fn.apply(new ErrorCause.Builder()).build());
        }

        /**
         * Required - API name: {@code status}
         */
        public final ErrorResponse.Builder status(int value) {
            this.status = value;
            return this;
        }

        @Override
        protected ErrorResponse.Builder self() {
            return this;
        }

        /**
         * Builds a {@link ErrorResponse}.
         *
         * @throws NullPointerException
         *             if some of the required fields are null.
         */
        public ErrorResponse build() {
            _checkSingleUse();

            return new ErrorResponse(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Json deserializer for {@link ErrorResponse}
     */
    public static final co.elastic.clients.json.JsonpDeserializer<ErrorResponse> _DESERIALIZER = co.elastic.clients.json.ObjectBuilderDeserializer.lazy(ErrorResponse.Builder::new,
            ErrorResponse::setupErrorResponseDeserializer);

    protected static void setupErrorResponseDeserializer(co.elastic.clients.json.ObjectDeserializer<ErrorResponse.Builder> op) {

        op.add(ErrorResponse.Builder::error, ErrorCause._DESERIALIZER, "error");
        op.add(ErrorResponse.Builder::status, JsonpDeserializer.integerDeserializer(), "status");

    }

}
