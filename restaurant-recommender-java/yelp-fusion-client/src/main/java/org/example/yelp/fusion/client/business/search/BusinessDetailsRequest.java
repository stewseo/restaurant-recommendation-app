package org.example.yelp.fusion.client.business.search;

import jakarta.json.stream.*;
import org.example.elasticsearch.client._types.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.transport.*;
import org.example.elasticsearch.client.util.*;

import java.util.*;
import java.util.function.*;

public class BusinessDetailsRequest extends RequestBase implements JsonpSerializable {
    private final String id;

    private BusinessDetailsRequest(Builder builder) {
        this.id = builder.id;
    }


    public static BusinessDetailsRequest of(Function<Builder, ObjectBuilder<BusinessDetailsRequest>> fn) {
        return fn.apply(new Builder()).build();
    }


    protected String id(){
        return this.id;
    }


    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {

        if (this.id != null) {
            generator.writeKey("id");
            generator.write(this.id);
        }
    }

    public static class Builder extends WithJsonObjectBuilderBase<Builder> implements ObjectBuilder<BusinessDetailsRequest> {
        private String id;

        public final Builder id(String value) {
            this.id = value;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public BusinessDetailsRequest build() {
            _checkSingleUse();
            return new BusinessDetailsRequest(this);
        }

    }

    public static final EndpointBase<BusinessDetailsRequest, ?> _ENDPOINT = new EndpointBase<>("v3/businesses",

            // Request method
            request -> {
                return "GET";
            },

            // Request path
            request -> {
                return "/businesses";
            },

            // Request parameters
            request -> {
                Map<String, String> params = new HashMap<>();
                if (request.id != null) {
                    params.put("id", request.id);

                }
                return params;

            }, EndpointBase.emptyMap(), false);

}
