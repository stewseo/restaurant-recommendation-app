package com.example.client._types;


import com.example.client.json.*;
import com.example.client.util.*;
import com.example.client.yelp_fusion.businesses.*;
import jakarta.json.stream.*;

@JsonpDeserializable
public abstract class WriteResponseBase implements JsonpSerializable {
    private String id;
    private Result result;
    private int total;
    private Business[] businesses;
    private Object region;

    // ---------------------------------------------------------------------------------------------
    protected WriteResponseBase() {

    }
    protected WriteResponseBase(WriteResponseBase.AbstractBuilder<?> builder) {

        this.id = ApiTypeHelper.requireNonNull(builder.id, this, "id");
        this.result = ApiTypeHelper.requireNonNull(builder.result, this, "result");
        this.total = ApiTypeHelper.requireNonNull(builder.total, this, "total");
        this.businesses = ApiTypeHelper.requireNonNull(builder.businesses, this, "businesses");
        this.region = ApiTypeHelper.requireNonNull(builder.region, this, "region");

    }
    public final int total() {
        return this.total;
    }

    public final Business[] businesses() {
        return this.businesses;
    }

    public final Object region() {

        return this.region;
    }

    /**
     * Required - API name: {@code _id}
     */
    public final String id() {
        return this.id;
    }



    /**
     * Required - API name: {@code result}
     */
    public final Result result() {
        return this.result;
    }



    /**
     * Serialize this object to JSON.
     */
    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {

        generator.writeKey("_id");
        generator.write(this.id);
        generator.writeKey("result");

        this.result.serialize(generator, mapper);

    }

    @Override
    public String toString() {
        return JsonpUtils.toString(this);
    }

    protected abstract static class AbstractBuilder<BuilderT extends WriteResponseBase.AbstractBuilder<BuilderT>>
            extends
            WithJsonObjectBuilderBase<BuilderT> {
        private String id;

        private Result result;

        private int total;

        private Business[] businesses;

        private Object region;

        /**
         * Required - API name: {@code _id}
         */
        public final BuilderT id(String value) {
            this.id = value;
            return self();
        }



        /**
         * Required - API name: {@code result}
         */
        public final BuilderT result(Result value) {
            this.result = value;
            return self();
        }

        public final BuilderT total(int total) {
            this.total = total;
            return self();
        }

        public final BuilderT businesses(Business[] businesses) {
            this.businesses = businesses;
            return self();
        }

        public final BuilderT region(Object region) {
            this.region = region;
            return self();
        }



        protected abstract BuilderT self();

    }

    // ---------------------------------------------------------------------------------------------
    protected static <BuilderT extends AbstractBuilder<BuilderT>> void setupWriteResponseBaseDeserializer(
            ObjectDeserializer<BuilderT> op) {

        op.add(WriteResponseBase.AbstractBuilder::id, JsonpDeserializer.stringDeserializer(), "_id");
        op.add(WriteResponseBase.AbstractBuilder::total, JsonpDeserializer.integerDeserializer(), "total");
        op.add(WriteResponseBase.AbstractBuilder::result, Result._DESERIALIZER, "result");

    }

}
