package org.example.yelp.fusion.client.business;

import jakarta.json.*;
import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.util.*;

import java.util.function.*;
@JsonpDeserializable
public class Coordinates implements JsonpSerializable {

    private Double latitude;

    private Double longitude;

    private GeoCoordinates geo_coordinates;

    private Coordinates(Builder builder) {
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.geo_coordinates = builder.geo_coordinates;
    }

    public static Coordinates of(Function<Builder, ObjectBuilder<Coordinates>> fn) {
        return fn.apply(new Coordinates.Builder()).build();
    }

    private Double latitude() {
        return latitude;
    }


    private Double longitude() {
        return longitude;
    }

    private GeoCoordinates geo_coordinates() {
        return geo_coordinates;
    }



    public static class Builder extends WithJsonObjectBuilderBase<Builder> implements ObjectBuilder<Coordinates> {
        private Double latitude;
        private Double longitude;
        private GeoCoordinates geo_coordinates;


        public final Builder latitude(Double value) {
            this.latitude = value;
            return this;
        }
        public final Builder longitude(Double value) {
            this.longitude = value;
            return this;
        }
        public final Builder geo_coordinates(GeoCoordinates value) {
            this.geo_coordinates = value;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Coordinates build() {
            _checkSingleUse();

            return new Coordinates(this);
        }
    }

    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {
        if (this.longitude != 0) {
            generator.writeKey("longitude");
            generator.write(this.longitude);
        }
        if (this.latitude != 0) {
            generator.writeKey("latitude");
            generator.write(this.latitude);
        }
        if (this.geo_coordinates != null) {
            generator.writeKey("geo_coordinates");
            generator.write((JsonValue) this.geo_coordinates);
        }
    }

    @Override
    public String toString() {
        return JsonpUtils.toString(this);
    }

    public static final JsonpDeserializer<Coordinates> _DESERIALIZER = ObjectBuilderDeserializer.lazy(Builder::new,
            Coordinates::setUpCoordinatesDeserializer);

    protected static void setUpCoordinatesDeserializer(ObjectDeserializer<Coordinates.Builder> op) {
        op.add(Builder::latitude, JsonpDeserializer.doubleDeserializer(), "latitude");
        op.add(Builder::longitude,  JsonpDeserializer.doubleDeserializer(), "longitude");
        op.add(Builder::geo_coordinates, GeoCoordinates._DESERIALIZER, "alias");
    }


    @JsonpDeserializable
    public static class GeoCoordinates implements JsonpSerializable {
        private final Double latitude;
        private final Double longitude;

        public GeoCoordinates(Builder build){
            this.latitude = build.latitude;
            this.longitude = build.longitude;
        }

        private Double latitude() {
            return latitude;
        }

        private Double longitude() {
            return longitude;
        }


        @Override
        public String toString() {
            return JsonpUtils.toString(this);
        }
        public static final JsonpDeserializer<GeoCoordinates> _DESERIALIZER = ObjectBuilderDeserializer.lazy(Builder::new,
                GeoCoordinates::setupGeoCoordinateDeserializer);

        @Override
        public void serialize(JsonGenerator generator, JsonpMapper mapper) {

        }


        public static class Builder extends WithJsonObjectBuilderBase<Builder> implements ObjectBuilder<GeoCoordinates> {
            private Double latitude;
            private Double longitude;

            private final Builder latitude(Double value) {
                this.latitude = value;
                return this;
            }
            private final Builder longitude(Double value) {
                this.longitude = value;
                return this;
            }

            public Builder(){}

            @Override
            protected Builder self() {
                return this;
            }

            public GeoCoordinates build() {
                _checkSingleUse();

                return new GeoCoordinates(this);
            }
        }

        private static void setupGeoCoordinateDeserializer(ObjectDeserializer<Builder> op) {
            op.add(Builder::latitude, JsonpDeserializer.doubleDeserializer(), "latitude");
            op.add(Builder::longitude,  JsonpDeserializer.doubleDeserializer(), "longitude");
        }
    }
}
