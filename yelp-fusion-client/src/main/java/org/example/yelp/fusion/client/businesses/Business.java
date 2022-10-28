
package org.example.yelp.fusion.client.businesses;


import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.util.*;

import java.util.*;
import java.util.function.*;

@SuppressWarnings("unused")
@JsonpDeserializable
public class Business implements JsonpSerializable {

    // ------------------------------------------------------- Fields
    private final Business_.Coordinates coordinates;

    private final Location location; // The location of this business, including address, city, state, zip code and country.

    private final String
            display_phone, // Phone number of the business formatted nicely to be displayed to users. The format is the standard phone number format for the business's country.
            id, // Unique Yelp ID of this business.
            alias, // Unique Yelp alias of this business. Can contain unicode characters.
            image_url, // URL of photo for this business.

            name, // name of business

            phone, // Phone number of the business.
            price, // Price level of the business. Value is one of $, $$, $$$ and $$$$.
            url; // URL for business page on Yelp.
    private final int
            open_at,
            review_count,
            distance;
    private final double
            latitude,
            longitude,
            rating;
    private final boolean
            is_claim,
            is_closed,
            open_now;
    private final Object[]
            transactions,
            categories, // A list of category title and alias pairs associated with this business.
            photos,
            special_hours,
            attributes;

    private final Hours[] hours;
    private final Object messaging; // Contains Business Messaging / Request a Quote information for this business.
                            // This field only appears in the response for businesses that have messaging enabled.



    // --------------------------------------------------------  Constructor
    private Business(Builder builder) {
        this.location = builder.location;
        this.alias = builder.alias;
        this.attributes = builder.attributes;
        this.display_phone = builder.display_phone;
        this.id = builder.id;
        this.categories = builder.categories;
        this.coordinates = builder.coordinates;
        this.hours = builder.hours;
        this.image_url = builder.image_url;
        this.is_claim = builder.is_claim;
        this.is_closed = builder.is_closed;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.messaging= builder.messaging;
        this.name = builder.name;
        this.open_at = builder.open_at;
        this.open_now = builder.open_now;
        this.phone = builder.phone;
        this.photos = builder.photos;
        this.price = builder.price;
        this.url = builder.url;
        this.rating = builder.rating;
        this.review_count = builder.review_count;
        this.special_hours = builder.special_hours;
        this.transactions = builder.transactions;
        this.distance = builder.distance;
    }

    public static Business of(Function<Business.Builder, ObjectBuilder<Business>> fn) {
        return fn.apply(new Business.Builder()).build();
    }

    public final Business_.Coordinates getCoordinates() {
        return coordinates;
    }

    public final Location getLocation() {
        return location;
    }

    public final String getDisplay_phone() {
        return display_phone;
    }

    public final String getId() {
        return id;
    }

    public final String getAlias() {
        return alias;
    }

    public final String getImage_url() {
        return image_url;
    }

    public final String getName() {
        return name;
    }

    public final String getPhone() {
        return phone;
    }

    public final String getPrice() {
        return price;
    }

    public final Double getRating() {
        return rating;
    }

    public final String getUrl() {
        return url;
    }

    public final int getOpen_at() {
        return open_at;
    }

    public final int getReview_count() {
        return review_count;
    }

    public final int getDistance() {
        return distance;
    }

    public final double getLatitude() {
        return latitude;
    }

    public final double getLongitude() {
        return longitude;
    }

    public final boolean isIs_claim() {
        return is_claim;
    }

    public final boolean isIs_closed() {
        return is_closed;
    }

    public final boolean isOpen_now() {
        return open_now;
    }

    public final Object[] getTransactions() {
        return transactions;
    }

    public final Object[] getCategories() {
        return categories;
    }

    public final Object[] getPhotos() {
        return photos;
    }

    public final Object[] getSpecial_hours() {
        return special_hours;
    }

    public final Object[] getAttributes() {
        return attributes;
    }

    public final Hours[] getHours() {
        return hours;
    }

    public final Object getMessaging() {
        return messaging;
    }

    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {

        if (this.is_claim) {
            generator.writeKey("is_claim");
            generator.write(this.is_claim);

        }
        if (is_closed) {
            generator.writeKey("is_closed");
            generator.write(this.is_closed);
        }
        if (this.display_phone != null) {
            generator.writeKey("display_phone");
            generator.write(this.display_phone);

        }
        if (this.id != null) {
            generator.writeKey("id");
            generator.write(this.id);

        }
        if (this.alias != null) {
            generator.writeKey("alias");
            generator.write(this.alias);

        }
        if (this.image_url != null) {
            generator.writeKey("image_url");
            generator.write(this.image_url);

        }
        if (this.name != null) {
            generator.writeKey("name");
            generator.write(this.name);
        }
        if (this.phone != null) {
            generator.writeKey("phone");
            generator.write(this.phone);
        }
        if (this.url != null) {
            generator.writeKey("url");
            generator.write(this.url);
        }
        if (this.rating != 0) {
            generator.writeKey("rating");
            generator.write(this.rating);
        }
        if (this.open_at != 0) {
            generator.writeKey("open_at");
            generator.write(this.open_at);
        }
        if (this.price != null) {
            generator.writeKey("price");
            generator.write(this.price);
        }
        if (this.review_count != 0) {
            generator.writeKey("review_count");
            generator.write(this.review_count);
        }
        if (this.longitude != 0) {
            generator.writeKey("longitude");
            generator.write(this.longitude);
        }
        if (this.latitude != 0) {
            generator.writeKey("latitude");
            generator.write(this.latitude);
        }
        if (this.distance != 0) {
            generator.writeKey("distance");
            generator.write(this.distance);
        }
    }

    @Override
    public String toString() {
        return JsonpUtils.toString(this);
    }

    // ----------------------------------------------------------------- Builder
    public static class Builder extends WithJsonObjectBuilderBase<Builder> implements ObjectBuilder<Business> {
        private String display_phone, id, alias, image_url, name, phone, price, url;
        private int
                open_at,
                review_count,
                distance;
        private double
                latitude,
                longitude,
                rating;
        private boolean
                is_claim,
                is_closed,
                open_now;
        private Object[]
                transactions,
                categories,
                photos,
                special_hours,
                attributes;

        private Business_.Coordinates coordinates;
        private Location location; // The location of this business, including address, city, state, zip code and country.
        private Hours[] hours;
        private Object messaging; // Contains Business Messaging / Request a Quote information for this business.
        // This field only appears in the response for businesses that have messaging enabled.

        public final Business.Builder display_phone(String value) {
            this.display_phone = value;
            return this;
        }
        public final Business.Builder id(String value) {
            this.id = value;
            return this;
        }
        public final Business.Builder price(String value) {
            this.price = value;
            return this;
        }
        public final Business.Builder phone(String value) {
            this.phone = value;
            return this;
        }
        public final Business.Builder name(String value) {
            this.name = value;
            return this;
        }
        public final Business.Builder image_url(String value) {
            this.image_url = value;
            return this;
        }
        public final Business.Builder alias(String value) {
            this.alias = value;
            return this;
        }
        public final Business.Builder url(String value) {
            this.url = value;
            return this;
        }
        public final Business.Builder rating(Double value) {
            this.rating = value;
            return this;
        }
        public final Business.Builder messaging(Object value) {
            this.messaging = value;
            return this;
        }
        public final Business.Builder hours(Hours[] value) {
            this.hours = value;
            return this;
        }
        public final Business.Builder location(Location value) {
            this.location = value;
            return this;
        }
        public final Business.Builder coordinates(Business_.Coordinates value) {
            this.coordinates = value;
            return this;
        }
        public final Business.Builder attributes(Object[] value) {
            this.special_hours = value;
            return this;
        }
        public final Business.Builder categories(Object[] value) {
            this.categories = value;
            return this;
        }
        public final Business.Builder transactions(Object[] value) {
            this.transactions = value;
            return this;
        }
        public final Business.Builder special_hours(Object[] value) {
            this.special_hours = value;
            return this;
        }


        public final Business.Builder review_count(int value) {
            this.review_count = value;
            return this;
        }
        public final Business.Builder open_at(int value) {
            this.open_at = value;
            return this;
        }

        public final Business.Builder distance(int value) {
            this.distance = value;
            return this;
        }
        public final Business.Builder longitude(double value) {
            this.longitude = value;
            return this;
        }
        public final Business.Builder latitude(double value) {
            this.latitude = value;
            return this;
        }
        public final Business.Builder is_closed(boolean value) {
            this.is_closed = value;
            return this;
        }
        public final Business.Builder is_claim(boolean value) {
            this.is_claim = value;
            return this;
        }

        @Override
        protected Business.Builder self() {
            return this;
        }

        public Business build() {
            _checkSingleUse();

            return new Business(this);
        }
    }


    public static final JsonpDeserializer<Business> _DESERIALIZER = ObjectBuilderDeserializer.lazy(Business.Builder::new,
            Business::setUpBusinessDeserializer);

    protected static void setUpBusinessDeserializer(ObjectDeserializer<Business.Builder> op) {

        op.add(Business.Builder::id, JsonpDeserializer.stringDeserializer(), "id");
        op.add(Business.Builder::display_phone,  JsonpDeserializer.stringDeserializer(), "display_phone");
        op.add(Business.Builder::alias, JsonpDeserializer.stringDeserializer(), "alias");
        op.add(Business.Builder::url, JsonpDeserializer.stringDeserializer(), "url");
        op.add(Business.Builder::alias, JsonpDeserializer.stringDeserializer(), "alias");
        op.add(Business.Builder::image_url, JsonpDeserializer.stringDeserializer(), "image_url");
        op.add(Business.Builder::price, JsonpDeserializer.stringDeserializer(), "price");
        op.add(Business.Builder::phone, JsonpDeserializer.stringDeserializer(), "phone");
        op.add(Business.Builder::latitude, JsonpDeserializer.doubleDeserializer(), "latitude");
        op.add(Business.Builder::longitude, JsonpDeserializer.doubleDeserializer(), "longitude");
        op.add(Business.Builder::rating, JsonpDeserializer.doubleDeserializer(), "rating");
        op.add(Business.Builder::price, JsonpDeserializer.stringDeserializer(), "price");

    }
    static class Hours {
        public Object[] open;
        public String hours_type;
        public boolean is_open_now;

        public Hours() {
        }

        public Object[] getOpen() {
            return open;
        }

        public void setOpen(Object[] open) {
            this.open = open;
        }

        public String getHours_type() {
            return hours_type;
        }

        public void setHours_type(String hours_type) {
            this.hours_type = hours_type;
        }

        public boolean isIs_open_now() {
            return is_open_now;
        }

        public void setIs_open_now(boolean is_open_now) {
            this.is_open_now = is_open_now;
        }

        @Override
        public String toString() {
            return "Hours{" +
                    "open=" + Arrays.toString(open) +
                    ", hours_type='" + hours_type + '\'' +
                    ", is_open_now=" + is_open_now +
                    '}';
        }
    }
}

