package org.example.yelp.fusion.client.business.search;


import jakarta.json.*;
import jakarta.json.stream.*;
import org.example.elasticsearch.client._types.*;

import org.example.elasticsearch.client._types.ErrorResponse;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.transport.*;
import org.example.elasticsearch.client.transport.endpoints.*;
import org.example.elasticsearch.client.util.*;
import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.category.*;

import java.util.*;
import java.util.function.*;

// Build a BusinessSearchRequest for the business search endpoint
public class BusinessSearchRequest extends RequestBase implements JsonpSerializable {

    private final List<String> term;
    private final List<String> location;
    private final Double latitude;
    private final Double longitude;
    private final int radius;
    private final Categories categories;
    private final String locale;
    private final Integer limit;
    private final Integer offset;
    
    private final String sort_by;
    private final String price;

    private final Boolean open_now;
    private final int open_at;
    private final List<String> attributes;
    
    private BusinessSearchRequest(Builder builder){

        this.term = builder.term;
        this.location = builder.location; // location or latitude/longitude is the only required field
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.radius = builder.radius;
        this.categories = builder.categories;
        this.locale = builder.locale;
        this.limit = builder.limit;
        this.offset = builder.offset;
        this.sort_by = builder.sort_by;
        this.price = builder.price;
        this.open_now = builder.open_now;
        this.open_at = builder.open_at;
        this.attributes = builder.attributes;
    }

    public static BusinessSearchRequest of(Function<Builder, ObjectBuilder<BusinessSearchRequest>> fn) {
        return fn.apply(new Builder()).build();
    }

    public List<String> term() {
        return this.term;
    }
    public List<String> location() {
        return this.location;
    }

    public Double latitude() {
        return this.latitude;
    }

    public Double longitude() {
        return this.longitude;
    }

    public int radius() {
        return this.radius;
    }

    public Categories categories() {
        return this.categories;
    }

    public String locale() {
        return this.locale;
    }

    public Integer limit() {
        return this.limit;
    }
    public Integer offset() {
        return this.offset;
    }

    public String sort_by() {return this.sort_by;}

    public Boolean open_now() {
        return this.open_now;
    }

    public int open_at() {
        return this.open_at;
    }
    public String price() {
        return this.price;
    }

    public List<String> attributes() {
        return this.attributes();
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeKey("categories");
        this.categories.serialize(generator, mapper);

        if (this.longitude != null) {
            generator.writeKey("longitude");
            generator.write(this.latitude);
        }

        if (this.latitude != null) {
            generator.writeKey("latitude");
            generator.write(this.latitude);
        }

        if (this.radius != 0) {
            generator.writeKey("radius");
            generator.write(this.radius);
        }

        if (this.offset != null) {
            generator.writeKey("offset");
            generator.write(this.offset);
        }

        if (this.sort_by != null) {
            generator.writeKey("sort_by");
            generator.write(this.sort_by);
        }

        if (this.limit != null) {
            generator.writeKey("limit");
            generator.write(this.limit);
        }

        if (this.open_at != 0) {
            generator.writeKey("open_at");
            generator.write(this.open_at);
        }

        if (this.price != null) {
            generator.writeKey("price");
            generator.write(this.price);
        }

        if (ApiTypeHelper.isDefined(this.location)) {
            generator.writeKey("location");
            generator.writeStartArray();
            for (String item0 : this.location) {
                generator.write(item0);
            }
            generator.writeEnd();

        }
        if (ApiTypeHelper.isDefined(this.attributes)) {
            generator.writeKey("attributes");
            generator.writeStartArray();
            for (String item0 : this.attributes) {
                generator.write(item0);

            }
            generator.writeEnd();

        }
        if (ApiTypeHelper.isDefined(this.term)) {
            generator.writeKey("term");
            generator.writeStartArray();
            for (String item0 : this.term) {
                generator.write(item0);

            }
            generator.writeEnd();
        }

    }


        // Builder for BusinessSearchRequest

    public static class Builder extends WithJsonObjectBuilderBase<Builder> implements ObjectBuilder<BusinessSearchRequest> {

        private List<String> term;
        private List<String> location;

        private Double latitude;
        private Double longitude;
        private int radius;
        private Categories categories;

        private String locale;
        private Integer limit;
        private Integer offset;
        private String price;

        private String sort_by;
        private Boolean open_now;

        private int open_at;
        private List<String> attributes;

        public Builder() {
        }

        public final Builder term(List<String> list) {
            this.term = _listAddAll(this.term, list);
            return this;                               
        }
        public final Builder term(String value, String... values) {
            this.term = _listAdd(this.term, value, values);
            return this;
        }

        public final Builder categories(Categories value) {
            this.categories = value;
            return this;
        }

        public final Builder categories(Function<Categories.Builder, ObjectBuilder<Categories>> fn) {
            return this.categories(fn.apply(new Categories.Builder()).build());
        }

        public final Builder location(List<String> list) {
            this.location = _listAddAll(this.location, list);
            return this;
        }
        public final Builder location(String value, String... values) {
            this.location = _listAdd(this.location, value, values);
            return this;
        }

        public final Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public final Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public final Builder radius(int radius) {
            this.radius = radius;
            return this;
        }

        public final Builder locale(String locale) {
            this.sort_by = sort_by;
            return this;
        }
        public final Builder offset(int offset) {
            this.offset = offset;
            return this;
        }
        public final Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public final Builder sort_by(String sort_by) {
            this.sort_by = sort_by;
            return this;
        }

        public final Builder open_now(boolean open_now) {
            this.open_now = open_now;
            return this;
        }

        public final Builder open_at(int open_at) {
            this.open_at = open_at;
            return this;
        }

        public final Builder price(String price) {
            this.price = price;
            return this;
        }

        public final Builder attributes(List<String> list) {
            this.attributes = _listAddAll(this.attributes, list);
            return this;
        }

        public final Builder attributes(String value, String... values) {
            this.attributes = _listAdd(this.attributes, value, values);
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public BusinessSearchRequest build() {
            _checkSingleUse();
            return new BusinessSearchRequest(this);
        }

    }

    //Json deserializer for BusinessSearchRequest

    public static final JsonpDeserializer<BusinessSearchRequest> _DESERIALIZER =
            ObjectBuilderDeserializer.lazy(BusinessSearchRequest.Builder::new,
                    BusinessSearchRequest::setupSearchRequestDeserializer);

    @Override
    public String toString() {

        return JsonpUtils.toString(this);
    }
    protected static void setupSearchRequestDeserializer(ObjectDeserializer<Builder> op) {

        op.add(BusinessSearchRequest.Builder::term, JsonpDeserializer.arrayDeserializer(JsonpDeserializer.stringDeserializer()), "term");
        op.add(BusinessSearchRequest.Builder::location, JsonpDeserializer.stringDeserializer(), "location");    // required field
        op.add(BusinessSearchRequest.Builder::latitude, JsonpDeserializer.doubleDeserializer(), "latitude");
        op.add(BusinessSearchRequest.Builder::longitude, JsonpDeserializer.doubleDeserializer(), "longitude");
        op.add(BusinessSearchRequest.Builder::radius, JsonpDeserializer.integerDeserializer(), "radius");
        op.add(BusinessSearchRequest.Builder::categories, Categories._DESERIALIZER, "categories");
        op.add(BusinessSearchRequest.Builder::attributes, JsonpDeserializer.arrayDeserializer(JsonpDeserializer.stringDeserializer()), "attributes");
        op.add(BusinessSearchRequest.Builder::limit, JsonpDeserializer.integerDeserializer(), "limit");
        op.add(BusinessSearchRequest.Builder::offset, JsonpDeserializer.integerDeserializer(), "offset");
        op.add(BusinessSearchRequest.Builder::sort_by, JsonpDeserializer.stringDeserializer(), "sort_by");
        op.add(BusinessSearchRequest.Builder::price, JsonpDeserializer.stringDeserializer(), "price");
        op.add(BusinessSearchRequest.Builder::open_now, JsonpDeserializer.booleanDeserializer(), "open_now");
        op.add(BusinessSearchRequest.Builder::open_at, JsonpDeserializer.integerDeserializer(), "open_at");
    }
    public static final SimpleEndpoint<BusinessSearchRequest, ?> _ENDPOINT = new SimpleEndpoint<>("businesses/search",
            request -> {
                return "GET";
            },
            // Request path
            request -> {
                StringBuilder buf = new StringBuilder();
                buf.append("/businesses/search");
                return buf.toString();
            },
            // Request parameters
            request -> {
                Map<String, String> parameters = new HashMap<>();
                if (request.term != null) {
                    request.term.forEach(term -> parameters.put("term", term));
                }
                if (request.location() != null) {
                    request.location.forEach(location -> parameters.put("location", location));
                }
                if (request.latitude != null) {
                    parameters.put("latitude", String.valueOf(request.latitude));
                }
                if (request.longitude != null) {
                    parameters.put("longitude", String.valueOf(request.longitude));
                }
                if (request.radius != 0) {
                    parameters.put("radius", String.valueOf(request.radius));
                }
                if (request.locale != null) {
                    parameters.put("locale", String.valueOf(request.locale));
                }
                if (request.limit != null) {
                    parameters.put("limit", String.valueOf(request.limit));
                }
                if (request.offset != null) {
                    parameters.put("offset", String.valueOf(request.offset));
                }
                if (request.sort_by != null) {
                    parameters.put("sort_by", request.sort_by);
                }
                if (request.price != null) {
                    parameters.put("price", request.price);
                }
                if (request.open_now != null) {
                    parameters.put("open_now", String.valueOf(request.open_now));
                }
                if (request.open_at != 0) {
                    parameters.put("open_at", String.valueOf(request.open_at));
                }
                if (request.attributes != null) {
                    request.attributes.forEach(attribute -> parameters.put("attribute", attribute));
                }

                return parameters;
            }, SimpleEndpoint.emptyMap(),
            false,
            BusinessSearchResponse._DESERIALIZER);

    public static <TDocument> Endpoint<BusinessSearchRequest, BusinessSearchResponse<TDocument>, ErrorResponse> createSearchEndpoint(
            JsonpDeserializer<TDocument> tDocumentDeserializer) {
        return _ENDPOINT
                .withResponseDeserializer(BusinessSearchResponse.createSearchResponseDeserializer(tDocumentDeserializer));
    }
}
