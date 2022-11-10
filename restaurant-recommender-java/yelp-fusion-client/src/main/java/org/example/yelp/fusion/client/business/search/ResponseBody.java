package org.example.yelp.fusion.client.business.search;



import jakarta.json.JsonValue;
import org.example.elasticsearch.client.elasticsearch.core.HitsMetadata;

import org.example.elasticsearch.client.json.*;

import org.example.elasticsearch.client.util.*;
import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.business.*;
import jakarta.json.stream.JsonGenerator;

import java.util.*;
import java.util.function.*;


public abstract class ResponseBody<TDocument> implements JsonpSerializable {

    // ---------------------------------------------------------------------------------------------
    private final int total;
    private final List<Business> businesses;
    private final Error error;

    private final Map<String, Coordinates> region;

    private final HitsMetadata<TDocument> hits;

    private final JsonpSerializer<TDocument> tDocumentSerializer;

    // ---------------------------------------------------------------------------------------------

    public int total() {
        return total;
    }

    public Error error() {
        return error;
    }

    public List<Business> businesses() {
        return businesses;
    }

    public Map<String, Coordinates> region() {
        return region;
    }

    
    public JsonpSerializer<TDocument> tDocumentSerializer() {
        return tDocumentSerializer;
    }

    protected ResponseBody(AbstractBuilder<TDocument, ?> builder) {
        this.total = builder.total;
        this.error = builder.error;
        this.businesses = builder.businesses;
        this.tDocumentSerializer = builder.tDocumentSerializer;
        this.region = builder.region;
        this.hits = ApiTypeHelper.requireNonNull(builder.hits, this, "hits");
    }


    public final HitsMetadata<TDocument> hits() {
        return this.hits;
    }


    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {


        generator.writeKey("hits");
        this.hits.serialize(generator, mapper);

        if (this.businesses != null) {
            generator.writeKey("businesses");
            this.businesses.forEach(e-> generator.write((JsonValue) e));
        }
        if (this.total != 0) {
            generator.writeKey("total");
            generator.write(this.total);

        }
        if (this.error != null) {
            generator.writeKey("error");
            generator.write((JsonValue) this.error);
        }

        if (ApiTypeHelper.isDefined(this.region)) {
            generator.writeKey("region");
            generator.writeStartObject();
            for (Map.Entry<String, Coordinates> item0 : this.region.entrySet()) {
                generator.writeKey(item0.getKey());
                item0.getValue().serialize(generator, mapper);
            }
            generator.writeEnd();

        }
    }

    @Override
    public String toString() {
        return JsonpUtils.toString(this);
    }


    protected abstract static class AbstractBuilder<TDocument, BuilderT extends AbstractBuilder<TDocument, BuilderT>>
            extends
            WithJsonObjectBuilderBase<BuilderT> {

        private int total;

        private Error error;

        private List<Business> businesses;

        private Map<String, Coordinates> region;
        private HitsMetadata<TDocument> hits;


        private JsonpSerializer<TDocument> tDocumentSerializer;

        public final BuilderT total(Integer value) {
            this.total = value;
            return self();
        }
        public final BuilderT region(Map<String, Coordinates> value) {
            this.region = value;
            return self();
        }

        public final BuilderT businesses(List<Business> value) {
            this.businesses = value;
            return self();
        }

        public final BuilderT error(Error value) {
            this.error = value;
            return self();
        }


        public final BuilderT hits(HitsMetadata<TDocument> value) {
            this.hits = value;
            return self();
        }

        public final BuilderT hits(
                Function<HitsMetadata.Builder<TDocument>, ObjectBuilder<HitsMetadata<TDocument>>> fn) {
            return this.hits(fn.apply(new HitsMetadata.Builder<TDocument>()).build());
        }


        public final BuilderT tDocumentSerializer( JsonpSerializer<TDocument> value) {
            this.tDocumentSerializer = value;
            return self();
        }

        protected abstract BuilderT self();

    }

    // ---------------------------------------------------------------------------------------------
    protected static <TDocument, BuilderT extends AbstractBuilder<TDocument, BuilderT>> void setupResponseBodyDeserializer(
            ObjectDeserializer<BuilderT> op, JsonpDeserializer<TDocument> tDocumentDeserializer) {

        op.add(AbstractBuilder::total, JsonpDeserializer.integerDeserializer(), "total");
        op.add(AbstractBuilder::region, JsonpDeserializer.stringMapDeserializer(Coordinates._DESERIALIZER), "region");
        op.add(AbstractBuilder::businesses, JsonpDeserializer.arrayDeserializer(Business._DESERIALIZER), "businesses");
        op.add(AbstractBuilder::hits, HitsMetadata.createHitsMetadataDeserializer(tDocumentDeserializer), "hits");
    }

}
