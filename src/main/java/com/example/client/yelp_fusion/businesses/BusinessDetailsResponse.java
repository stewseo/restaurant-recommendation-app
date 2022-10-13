package com.example.client.yelp_fusion.businesses;

import com.example.client.json.*;
import com.example.client.util.*;
import jakarta.json.stream.*;

import java.util.*;
import java.util.function.*;

@SuppressWarnings("unused")
@JsonpDeserializable
public class BusinessDetailsResponse implements JsonpSerializable {

    public Business business;
    public List<Business> businesses;
    public String
            display_phone, // Phone number of the business formatted nicely to be displayed to users. The format is the standard phone number format for the business's country.
            id, // Unique Yelp ID of this business.
            alias, // Unique Yelp alias of this business. Can contain unicode characters.
            image_url, // URL of photo for this business.
            name, // name of business
            phone, // Phone number of the business.
            price, // Price level of the business. Value is one of $, $$, $$$ and $$$$.
            rating,  // Rating for this business (value ranges from 1, 1.5, ... 4.5, 5).
            url; // URL for business page on Yelp.
    public Object region, messaging;
    public Coordinates coordinates;
    public Location location; // The location of this business, including address, city, state, zip code and country.

    public int
            limit,
            offset,
            open_at,
            review_count,
            distance;
    public double
            latitude,
            longitude;
    public boolean
            is_claimed,
            is_closed,
            open_now;

    public Object[]
            transactions,
            categories, // A list of category title and alias pairs associated with this business.
            photos,
            special_hours,
            attributes;

    public Business.Hours[] hours;

    private final int total;
    private final Map<String, Business> result;


    private int total(){
        return this.total;
    }

    private Map<String, Business>  result(){
        return this.result;
    }

    private List<Business>  businesses(){
        return this.businesses;
    }


    // ---------------------------------------------------------------------------------------------

    private BusinessDetailsResponse(Builder builder) {
        this.total = builder.total;
        this.result = ApiTypeHelper.unmodifiableRequired(builder.result, this, "result");

    }

    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        for (Map.Entry<String, Business> item0 : this.result.entrySet()) {
            generator.writeKey(item0.getKey());
            item0.getValue().serialize(generator, mapper);

        }

        generator.writeEnd();

    }

    public static class Builder extends WithJsonObjectBuilderBase<Builder>
            implements
            ObjectBuilder<BusinessDetailsResponse> {
        private Map<String, Business> result = new HashMap<>();
        private int total;

        public final BusinessDetailsResponse.Builder total(int total) {
            this.total = total;
            return this;
        }


        public final BusinessDetailsResponse.Builder result(Map<String, Business> map) {
            this.result = _mapPutAll(this.result, map);
            return this;
        }


        public final BusinessDetailsResponse.Builder result(String key, Business value) {
            this.result = _mapPut(this.result, key, value);
            return this;
        }


        public final BusinessDetailsResponse.Builder result(String key, Function<Business.Builder, ObjectBuilder<Business>> fn) {
            return result(key, fn.apply(new Business.Builder()).build());
        }

        @Override
        public Builder withJson(JsonParser parser, JsonpMapper mapper) {

            Map<String, Business> value = (Map<String, Business>) JsonpDeserializer
                    .stringMapDeserializer(Business._DESERIALIZER).deserialize(parser, mapper);
            return this.result(value);
        }


        @Override
        protected BusinessDetailsResponse.Builder self() {
            return this;
        }


        public BusinessDetailsResponse build() {
            _checkSingleUse();

            return new BusinessDetailsResponse(this);
        }

    }

    public static final JsonpDeserializer<BusinessDetailsResponse> _DESERIALIZER = createBusinessDetailsResponseDeserializer();

    protected static JsonpDeserializer<BusinessDetailsResponse> createBusinessDetailsResponseDeserializer() {

        JsonpDeserializer<Map<String, Business>> valueDeserializer = JsonpDeserializer
                .stringMapDeserializer(Business._DESERIALIZER);

        return JsonpDeserializer.of(valueDeserializer.acceptedEvents(), (parser, mapper, event) -> new Builder()
                .result(valueDeserializer.deserialize(parser, mapper, event)).build());
    }
}

