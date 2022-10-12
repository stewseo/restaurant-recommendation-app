package com.example.client._types;

import com.example.client.json.*;
import com.example.client.util.*;
import jakarta.json.stream.*;

import java.util.function.*;

public class ErrorResponse implements JsonpSerializable {
    private String error;

    private final int status;

    // ---------------------------------------------------------------------------------------------

    private ErrorResponse(Builder builder) {

        this.error = builder.error;
        this.status = builder.status;

    }

    public static ErrorResponse of(Function<Builder, ObjectBuilder<ErrorResponse>> fn) {
        return fn.apply(new Builder()).build();
    }


    public final String error() {
        return this.error;
    }


    public final int status() {
        return this.status;
    }

    @Override
    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        System.out.println("test");
    }


    public static class Builder extends WithJsonObjectBuilderBase<Builder> implements ObjectBuilder<ErrorResponse> {
        private String error;

        private Integer status;


        public final Builder error(String value) {
            this.error = value;
            return this;
        }

        public final Builder status(int value) {
            this.status = value;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public ErrorResponse build() {
            _checkSingleUse();

            return new ErrorResponse(this);
        }
    }

    public static final JsonpDeserializer<ErrorResponse> _DESERIALIZER = ObjectBuilderDeserializer.lazy(ErrorResponse.Builder::new,
           ErrorResponse::setupErrorResponseDeserializer);

    protected static void setupErrorResponseDeserializer(ObjectDeserializer<ErrorResponse.Builder> op) {
        System.out.println("setupErrorResponseDeserializer" + op);
//        op.add(ErrorResponse.Builder::error, ErrorCause._DESERIALIZER, "error");
        op.add(ErrorResponse.Builder::status, JsonpDeserializer.integerDeserializer(), "status");
    }
}