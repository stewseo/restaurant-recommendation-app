package com.example.client.yelp_fusion.model;

import com.example.client._types.*;
import com.example.client.json.*;
import com.example.client.transport.endpoints.*;
import com.example.client.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

// Business Search Endpoint
// Defined by request parameter fields
@SuppressWarnings("ALL")
public class TestBusinessSearchRequest extends RequestBase implements Serializable {
    private final String id;
    private final String location;
    private final String term;
    private final Double latitude;
    private final Double longitude;
    private final String radius;
    private final String sort_by;
    private final int limit;
    private final int offset;
    private final String price;
    private final String open_at;
    private final boolean open_now;
    private final List<String> categories;
    private final List<String> attributes;


    private TestBusinessSearchRequest(TestBusinessSearchRequest.Builder builder) {
        this.term = builder.term;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.radius = builder.radius;
        this.sort_by = builder.sort_by;
        this.limit = builder.limit;
        this.offset = builder.offset;
        this.categories = builder.categories;
        this.location = builder.location;
        this.id = builder.id;
        this.open_at = builder.open_at;
        this.open_now = builder.open_now;
        this.price = builder.price;
        this.attributes = builder.attributes;
    }

    public static TestBusinessSearchRequest of(Function<Builder, ObjectBuilder<TestBusinessSearchRequest>> fn) {
        return fn.apply(new Builder()).build();
    }

    // methods for
    public final String id() {
        return this.id;
    }
    public final String location() {
        return this.location;
    }
    public final String term() {
        return this.term;
    }
    public final double latitude() {
        return this.latitude;
    }
    public final double longitude() {
        return this.longitude;
    }
    public final String radius() {
        return this.radius;
    }
    public final String sort_by() {return this.sort_by;}
    public final int limit() {
        return this.limit;
    }
    public final int offset() {
        return this.offset;
    }
    public final List<String> categories() {
        return this.categories;
    }
    public final boolean open_now() {
        return this.open_now;
    }
    public final String price() {
        return this.price;
    }
    public final String open_at() {
        return this.open_at;
    }
    public final List<String> attributes() {
        return this.attributes();
    }

    // Builder for TestBusinessSearchRequest

    public static class Builder extends com.example.client.util.WithJsonObjectBuilderBase<TestBusinessSearchRequest.Builder>
            implements com.example.client.util.ObjectBuilder<TestBusinessSearchRequest> {
        private String location;

        private String id;

        private String open_at;
        private String term;
        private Double latitude;
        private Double longitude;
        private String radius;
        private int limit;
        private int offset;
        private String sort_by;
        private List<String> categories;

        private List<String> attributes;

        private String price;
        private boolean open_now;


        public final Builder location(String location) {
            this.location = location;
            return this;
        }
        public final Builder id(String id) {
            this.id = id;
            return this;
        }

        public final Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public final Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public final Builder term(String term) {
            this.term = term;
            return this;
        }

        public final Builder radius(String radius) {
            this.radius = radius;
            return this;
        }

        public final Builder sort_by(String sort_by) {
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

        public final Builder open_now(boolean open_now) {
            this.open_now = open_now;
            return this;
        }

        public final Builder price(String price) {
            this.price = price;
            return this;
        }

        public final Builder open_at(String open_at) {
            this.open_at = open_at;
            return this;
        }

        // List<String> categories and attributes Business Search Builder inputs
        public final Builder categories(List<String> list) {
            this.categories = _listAddAll(this.categories, list);
            return this;
        }

        public final Builder categories(String value, String... values) {
            this.categories = _listAdd(this.categories, value, values);
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
        public TestBusinessSearchRequest build() {
            _checkSingleUse();
            return new TestBusinessSearchRequest(this);
        }
    }

    //Json deserializer for TestBusinessSearchRequest

    public static final JsonpDeserializer<TestBusinessSearchRequest> _DESERIALIZER =
            ObjectBuilderDeserializer.lazy(TestBusinessSearchRequest.Builder::new,
            TestBusinessSearchRequest::setupSearchRequestDeserializer);

    protected static void setupSearchRequestDeserializer(ObjectDeserializer<Builder> op) {
        op.add(TestBusinessSearchRequest.Builder::term, JsonpDeserializer.stringDeserializer(), "id");

        op.add(
                TestBusinessSearchRequest.Builder::location, // static Builder method for TestBusinessSearchRequest.Builder.location()
                JsonpDeserializer.stringDeserializer(), // add correct deserializer type
                "location"); // add name of parameter
        op.add(TestBusinessSearchRequest.Builder::latitude, JsonpDeserializer.doubleDeserializer(), "latitude");
        op.add(TestBusinessSearchRequest.Builder::longitude, JsonpDeserializer.doubleDeserializer(), "longitude");
        op.add(TestBusinessSearchRequest.Builder::radius, JsonpDeserializer.stringDeserializer(), "radius");
        op.add(TestBusinessSearchRequest.Builder::categories, JsonpDeserializer.stringDeserializer(), "categories");
        op.add(TestBusinessSearchRequest.Builder::limit, JsonpDeserializer.integerDeserializer(), "limit");
        op.add(TestBusinessSearchRequest.Builder::offset, JsonpDeserializer.integerDeserializer(), "offset");
        op.add(TestBusinessSearchRequest.Builder::sort_by, JsonpDeserializer.stringDeserializer(), "sort_by");
        op.add(TestBusinessSearchRequest.Builder::price, JsonpDeserializer.stringDeserializer(), "price");
        op.add(TestBusinessSearchRequest.Builder::open_now, JsonpDeserializer.booleanDeserializer(), "open_now");
        op.add(TestBusinessSearchRequest.Builder::open_at, JsonpDeserializer.stringDeserializer(), "open_at");
        op.add(TestBusinessSearchRequest.Builder::attributes, JsonpDeserializer.arrayDeserializer(JsonpDeserializer.stringDeserializer()), "attributes");

    }

    // endpoint businesses/search
    public static final TestBusinessSearchEndpoint<TestBusinessSearchRequest, ?> _ENDPOINT = new TestBusinessSearchEndpoint<>("businesses/search",
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
                if (request.location != null) {
                    parameters.put("location", request.location);
                }
                if (request.latitude != null) {
                    parameters.put("latitude", String.valueOf(request.latitude));
                }
                if (request.longitude != null) {
                    parameters.put("longitude", String.valueOf(request.longitude));
                }
                if (request.term != null) {
                    parameters.put("term", request.term);
                }
                if (request.radius != null) {
                    parameters.put("radius", String.valueOf(request.radius));
                }
                if (request.offset > 0) {
                    parameters.put("offset", String.valueOf(request.offset));
                }
                if (request.limit > 0) {
                    parameters.put("limit", String.valueOf(request.limit));
                }
                if (request.sort_by != null) {
                    parameters.put("sort_by", request.sort_by);
                }
                if (request.categories != null) {
                    parameters.put("categories", request.categories.toString());
                }
                if (request.attributes != null) {
                    parameters.put("attributes", request.attributes.toString());
                }

                return parameters;

            }, TestBusinessSearchEndpoint.emptyMap(), true);
}

