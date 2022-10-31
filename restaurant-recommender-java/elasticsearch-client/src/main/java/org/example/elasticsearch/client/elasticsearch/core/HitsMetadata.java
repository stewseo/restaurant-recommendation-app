package org.example.elasticsearch.client.elasticsearch.core;

import com.fasterxml.jackson.databind.annotation.*;
import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.util.*;

import java.util.*;
import java.util.function.*;

@JsonpDeserializable
public class HitsMetadata<T> implements JsonpSerializable {

    private final TotalHits total;

    private final List<Hit<T>> hits;

    private final Double maxScore;

    private final JsonpSerializer<T> tSerializer;

    // ---------------------------------------------------------------------------------------------

    private HitsMetadata(Builder<T> builder) {

        this.total = builder.total;
        this.hits = ApiTypeHelper.unmodifiableRequired(builder.hits, this, "hits");
        this.maxScore = builder.maxScore;
        this.tSerializer = builder.tSerializer;

    }

    public static <T> HitsMetadata<T> of(Function<Builder<T>, ObjectBuilder<HitsMetadata<T>>> fn) {
        return fn.apply(new HitsMetadata.Builder<>()).build();
    }



    public final TotalHits total() {
        return this.total;
    }

    public final List<Hit<T>> hits() {
        return this.hits;
    }


    public final Double maxScore() {
        return this.maxScore;
    }


    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {

        if (this.total != null) {
            generator.writeKey("total");
            this.total.serialize(generator, mapper);

        }
        if (ApiTypeHelper.isDefined(this.hits)) {
            generator.writeKey("hits");
            generator.writeStartArray();
            for (Hit<T> item0 : this.hits) {
                item0.serialize(generator, mapper);

            }
            generator.writeEnd();

        }
        if (this.maxScore != null) {
            generator.writeKey("max_score");
            JsonpUtils.serializeDoubleOrNull(generator, this.maxScore, Double.NaN);
        }

    }

    @Override
    public String toString() {
        return JsonpUtils.toString(this);
    }


    public static class Builder<T> extends WithJsonObjectBuilderBase<Builder<T>>
            implements
            ObjectBuilder<HitsMetadata<T>> {

        private TotalHits total;

        private List<Hit<T>> hits;


        private Double maxScore;


        private JsonpSerializer<T> tSerializer;


        public final HitsMetadata.Builder<T> total(TotalHits value) {
            this.total = value;
            return this;
        }


        public final HitsMetadata.Builder<T> total(Function<TotalHits.Builder, ObjectBuilder<TotalHits>> fn) {
            return this.total(fn.apply(new TotalHits.Builder()).build());
        }


        public final HitsMetadata.Builder<T> hits(List<Hit<T>> list) {
            this.hits = _listAddAll(this.hits, list);
            return this;
        }


        public final HitsMetadata.Builder<T> hits(Hit<T> value, Hit<T>... values) {
            this.hits = _listAdd(this.hits, value, values);
            return this;
        }

        public final HitsMetadata.Builder<T> hits(Function<Hit.Builder<T>, ObjectBuilder<Hit<T>>> fn) {
            return hits(fn.apply(new Hit.Builder<T>()).build());
        }


        public final HitsMetadata.Builder<T> maxScore( Double value) {
            this.maxScore = value;
            return this;
        }


        public final HitsMetadata.Builder<T> tSerializer(JsonpSerializer<T> value) {
            this.tSerializer = value;
            return this;
        }

        @Override
        protected HitsMetadata.Builder<T> self() {
            return this;
        }


        public HitsMetadata<T> build() {
            _checkSingleUse();

            return new HitsMetadata<T>(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static <T> JsonpDeserializer<HitsMetadata<T>> createHitsMetadataDeserializer(
            JsonpDeserializer<T> tDeserializer) {
        return ObjectBuilderDeserializer.createForObject((Supplier<HitsMetadata.Builder<T>>) HitsMetadata.Builder::new,
                op -> HitsMetadata.setupHitsMetadataDeserializer(op, tDeserializer));
    }

    ;

    public static final JsonpDeserializer<HitsMetadata<Object>> _DESERIALIZER = JsonpDeserializer
            .lazy(() -> createHitsMetadataDeserializer(
                    new NamedDeserializer<>("co.elastic.clients:Deserializer:_global.search._types.T")));

    protected static <T> void setupHitsMetadataDeserializer(ObjectDeserializer<HitsMetadata.Builder<T>> op,
                                                            JsonpDeserializer<T> tDeserializer) {

        op.add(Builder::total, TotalHits._DESERIALIZER, "total");

        op.add(Builder::hits, JsonpDeserializer.arrayDeserializer(Hit.createHitDeserializer(tDeserializer)), "hits");

        op.add(Builder::maxScore, JsonpDeserializer.doubleOrNullDeserializer(Double.NaN), "max_score");

    }
}




