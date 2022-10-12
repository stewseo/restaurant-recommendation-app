package com.example.client.yelp_fusion.businesses;

import com.example.client._types.*;
import com.example.client.json.*;
import com.example.client.transport.*;
import com.example.client.transport.endpoints.*;
import com.example.client.util.*;
import jakarta.json.stream.*;

import java.util.*;
import java.util.function.*;

@SuppressWarnings("unused")
@JsonpDeserializable
public class BusinessRequest<TDocument> extends RequestBase implements JsonpSerializable {

    // required
    public String location;
    public double latitude;

    public double longitude;

    public int radius;

    public int limit;

    public int offset;
    public String locale;
    public Terms[] term;

    public Business[] businesses;
    public String sort_by;
    private final TDocument document;

    private final JsonpSerializer<TDocument> tDocumentSerializer;

    private final String id;

    private Time masterTimeout;

    private Boolean summary;

    private BusinessRequest(Builder<TDocument> builder) {

        this.id = builder.id;
        this.latitude = builder.latitude;
        this.document = ApiTypeHelper.requireNonNull(builder.document, this, "document");
        this.tDocumentSerializer = builder.tDocumentSerializer;

    }

    public static <TDocument> BusinessRequest<TDocument> of(
            Function<Builder<TDocument>, ObjectBuilder<BusinessRequest<TDocument>>> fn) {
        return fn.apply(new Builder<>()).build();
    }


    public final String id() {
        return this.id;
    }


    public final Double latitude(double latitude) {
        return this.latitude;
    }

    public final Double longitude(double longitude) {
        return this.longitude;
    }

    public final String locale(String locale) {
        return this.locale;
    }

    public final int radius(int radius) {
        return this.radius;
    }

    public final int limit(int limit) {
        return this.limit;
    }

    public final int offset(int offset) {
        return this.offset;
    }

    public final Terms[] term(Terms[] term) {
        return this.term;
    }

    public final Business[] businesses(Business[] businesses) {
        return this.businesses;
    }

    public final String sort_by(String sort_by) {
        return this.sort_by;
    }


    public final TDocument document() {
        return this.document;
    }

    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        JsonpUtils.serialize(this.document, generator, tDocumentSerializer, mapper);

    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Builder for {@link BusinessRequest}.
     */

    @SuppressWarnings("FieldCanBeLocal")
    public static class Builder<TDocument> extends RequestBase.AbstractBuilder<Builder<TDocument>>
            implements
            ObjectBuilder<BusinessRequest<TDocument>> {

        private String id;
        public double latitude;

        public double longitude;
        private Coordinates coordinates;

        private String locale;

        private int radius;

        private int limit;

        private int offset;

        private Terms[] term;

        private Business[] businesses;

        private String sort_by;
        private TDocument document;

        private JsonpSerializer<TDocument> tDocumentSerializer;


        public final Builder<TDocument> term(Terms[] term) {
            this.term = term;
            return this;
        }

        public final Builder<TDocument> businesses(Business[] businesses) {
            this.businesses = businesses;
            return this;
        }

        public final Builder<TDocument> latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public final Builder<TDocument> longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public final Builder<TDocument> sort_by(String sort_by) {
            this.sort_by = sort_by;
            return this;
        }

        public final Builder<TDocument> id(String id) {
            this.id = id;
            return this;
        }

        public final Builder<TDocument> coordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public final Builder<TDocument> locale(String locale) {
            this.locale = locale;
            return this;
        }

        public final Builder<TDocument> radius(int radius) {
            this.radius = radius;
            return this;
        }

        public final Builder<TDocument> limit(int limit) {
            this.limit = limit;
            return this;
        }

        public final Builder<TDocument> offset(int offset) {
            this.offset = offset;
            return this;
        }


        public final Builder<TDocument> document(TDocument value) {
            this.document = value;
            return this;
        }

        /**
         * Serializer for TDocument. If not set, an attempt will be made to find a
         * serializer from the JSON context.
         */
        public final Builder<TDocument> tDocumentSerializer(JsonpSerializer<TDocument> value) {
            this.tDocumentSerializer = value;
            return this;
        }


        @Override
        protected Builder<TDocument> self() {
            return this;
        }


        public BusinessRequest<TDocument> build() {
            _checkSingleUse();

            return new BusinessRequest<TDocument>(this);
        }
    }


    public static final JsonpDeserializer<BusinessRequest<Object>> _DESERIALIZER = createGetBusinessRequestDeserializer(
            new NamedDeserializer<>("co.elastic.clients:Deserializer:_global.index.TDocument"));

    public static <TDocument> JsonpDeserializer<BusinessRequest<TDocument>> createGetBusinessRequestDeserializer(
            JsonpDeserializer<TDocument> tDocumentDeserializer) {

        return JsonpDeserializer.of(tDocumentDeserializer.acceptedEvents(),
                (parser, mapper, event) -> new Builder<TDocument>()
                        .document(tDocumentDeserializer.deserialize(parser, mapper, event)).build());
    }

    // ---------------------------------------------------------------------------------------------

    public static final Endpoint<BusinessRequest<?>, BusinessResponse, ErrorResponse> _ENDPOINT = new SimpleEndpoint<>(
            "v3/businesses",

            // Request method
            request -> {

                final int _id = 1 << 1;

                int propsSet = 0;

                propsSet |= _id;

                if (propsSet == (_id))
                    return "GET";
                throw SimpleEndpoint.noPathTemplateFound("method");

            },

            // Request path
            request -> {
                final int _index = 1 << 0;
                final int _id = 1 << 1;

                int propsSet = 0;

                propsSet |= _index;
                if (request.id() != null)
                    propsSet |= _id;

                if (propsSet == (_index | _id)) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("/");
                    SimpleEndpoint.pathEncode(request.id, buf);
                    buf.append("/_doc");
                    buf.append("/");
                    SimpleEndpoint.pathEncode(request.id, buf);
                    return buf.toString();
                }
                if (propsSet == (_index)) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("/");
                    SimpleEndpoint.pathEncode(request.id, buf);
                    buf.append("/_doc");
                    return buf.toString();
                }
                throw SimpleEndpoint.noPathTemplateFound("path");

            },

            // Request parameters
            request -> {
                Map<String, String> params = new HashMap<>();
                if (request.latitude != 0) {
                    params.put("latitude", String.valueOf(request.latitude));
                }
                if (request.longitude != 0) {
                    params.put("longitude", String.valueOf(request.longitude));
                }
                if (request.location != null) {
                    params.put("location", request.location);
                }
                if (request.term != null) {
                    params.put("pipeline", String.valueOf(request.term));
                }
                if (request.radius != 0) {
                    params.put("radius", String.valueOf(request.radius));
                }
                if (request.limit != 0) {
                    params.put("limit", String.valueOf(request.limit));
                }
                if (request.offset != 0) {
                    params.put("offset", String.valueOf(request.offset));
                }
                return params;

            }, SimpleEndpoint.emptyMap(), true, BusinessResponse._DESERIALIZER);
}