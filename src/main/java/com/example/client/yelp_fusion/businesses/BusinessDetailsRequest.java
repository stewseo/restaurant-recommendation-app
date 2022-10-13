package com.example.client.yelp_fusion.businesses;


import com.example.client._types.*;
import com.example.client.json.*;
import com.example.client.transport.*;
import com.example.client.transport.endpoints.*;
import com.example.client.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class BusinessDetailsRequest extends RequestBase implements Serializable {

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


    private BusinessDetailsRequest(BusinessDetailsRequest.Builder builder) {
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

    public static BusinessDetailsRequest of(Function<BusinessDetailsRequest.Builder, ObjectBuilder<BusinessDetailsRequest>> fn) {
        return fn.apply(new BusinessDetailsRequest.Builder()).build();
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

    public static class Builder extends com.example.client.util.WithJsonObjectBuilderBase<BusinessDetailsRequest.Builder>
            implements com.example.client.util.ObjectBuilder<BusinessDetailsRequest> {
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


        public final BusinessDetailsRequest.Builder location(String location) {
            this.location = location;
            return this;
        }
        public final BusinessDetailsRequest.Builder id(String id) {
            this.id = id;
            return this;
        }

        public final BusinessDetailsRequest.Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public final BusinessDetailsRequest.Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public final BusinessDetailsRequest.Builder term(String term) {
            this.term = term;
            return this;
        }

        public final BusinessDetailsRequest.Builder radius(String radius) {
            this.radius = radius;
            return this;
        }

        public final BusinessDetailsRequest.Builder sort_by(String sort_by) {
            this.sort_by = sort_by;
            return this;
        }

        public final BusinessDetailsRequest.Builder offset(int offset) {
            this.offset = offset;
            return this;
        }
        public final BusinessDetailsRequest.Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public final BusinessDetailsRequest.Builder open_now(boolean open_now) {
            this.open_now = open_now;
            return this;
        }

        public final BusinessDetailsRequest.Builder price(String price) {
            this.price = price;
            return this;
        }

        public final BusinessDetailsRequest.Builder open_at(String open_at) {
            this.open_at = open_at;
            return this;
        }

        // List<String> categories and attributes Business Search Builder inputs
        public final BusinessDetailsRequest.Builder categories(List<String> list) {
            this.categories = _listAddAll(this.categories, list);
            return this;
        }

        public final BusinessDetailsRequest.Builder categories(String value, String... values) {
            this.categories = _listAdd(this.categories, value, values);
            return this;
        }

        public final BusinessDetailsRequest.Builder attributes(List<String> list) {
            this.attributes = _listAddAll(this.attributes, list);
            return this;
        }

        public final BusinessDetailsRequest.Builder attributes(String value, String... values) {
            this.attributes = _listAdd(this.attributes, value, values);
            return this;
        }

        @Override
        protected BusinessDetailsRequest.Builder self() {
            return this;
        }

        @Override
        public BusinessDetailsRequest build() {
            _checkSingleUse();
            return new BusinessDetailsRequest(this);
        }
    }

    //Json deserializer for TestBusinessSearchRequest

    public static final JsonpDeserializer<BusinessDetailsRequest> _DESERIALIZER =
            ObjectBuilderDeserializer.lazy(BusinessDetailsRequest.Builder::new,
                    BusinessDetailsRequest::setupBusinessDetailsDeserializer);

    protected static void setupBusinessDetailsDeserializer(ObjectDeserializer<BusinessDetailsRequest.Builder> op) {
        op.add(BusinessDetailsRequest.Builder::term, JsonpDeserializer.stringDeserializer(), "id");

        op.add(BusinessDetailsRequest.Builder::location, // static Builder method for TestBusinessSearchRequest.Builder.location()
                JsonpDeserializer.stringDeserializer(), // add correct deserializer type
                "location"); // add name of parameter
        op.add(BusinessDetailsRequest.Builder::latitude, JsonpDeserializer.doubleDeserializer(), "latitude");
        op.add(BusinessDetailsRequest.Builder::longitude, JsonpDeserializer.doubleDeserializer(), "longitude");
        op.add(BusinessDetailsRequest.Builder::radius, JsonpDeserializer.stringDeserializer(), "radius");
        op.add(BusinessDetailsRequest.Builder::categories, JsonpDeserializer.stringDeserializer(), "categories");
        op.add(BusinessDetailsRequest.Builder::limit, JsonpDeserializer.integerDeserializer(), "limit");
        op.add(BusinessDetailsRequest.Builder::offset, JsonpDeserializer.integerDeserializer(), "offset");
        op.add(BusinessDetailsRequest.Builder::sort_by, JsonpDeserializer.stringDeserializer(), "sort_by");
        op.add(BusinessDetailsRequest.Builder::price, JsonpDeserializer.stringDeserializer(), "price");
        op.add(BusinessDetailsRequest.Builder::open_now, JsonpDeserializer.booleanDeserializer(), "open_now");
        op.add(BusinessDetailsRequest.Builder::open_at, JsonpDeserializer.stringDeserializer(), "open_at");
        op.add(BusinessDetailsRequest.Builder::attributes, JsonpDeserializer.arrayDeserializer(JsonpDeserializer.stringDeserializer()), "attributes");

    }

    // endpoint businesses/search
    public static final Endpoint<BusinessDetailsRequest,BusinessDetailsResponse,ErrorResponse> _ENDPOINT = new SimpleEndpoint<>("businesses/",
            request -> {
                return "GET";
            },
            // Request path
            request -> {
                StringBuilder buf = new StringBuilder();
                String id = request.id;
                buf.append("/businesses")
                        .append("/")
                        .append(id);
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

            }, SimpleEndpoint.emptyMap(), true, BusinessDetailsResponse._DESERIALIZER);
}

