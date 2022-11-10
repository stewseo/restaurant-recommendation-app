package org.example.yelp.fusion.client.category;


import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
@JsonpDeserializable
public class Categories implements JsonpSerializable {

    private final String alias, title;

    private final List<String> parents;

    private final List<String> country_whitelist;

    private final List<String> country_blacklist;


    private Categories(Builder builder) {
        this.alias = builder.alias;
        this.title = builder.title;
        this.parents = builder.parents;
        this.country_blacklist = builder.country_blacklist;
        this.country_whitelist = builder.country_whitelist;
    }
    public String alias() {
        return alias;
    }

    private String title() {
        return title;
    }

    private List<String> parents() {
        return parents;
    }

    private List<String> country_whitelist() {
        return country_whitelist;
    }

    private List<String> country_blacklist() {
        return country_blacklist;
    }

    public static Categories of(Function<Builder, ObjectBuilder<Categories>> fn) {
        return fn.apply(new Builder()).build();
    }

    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        generator.writeStartObject();
        serializeInternal(generator, mapper);
        generator.writeEnd();
    }

    protected void serializeInternal(JsonGenerator generator, JsonpMapper mapper) {
        if (this.alias != null) {
            generator.writeKey("alias");
            generator.write(this.alias);
        }

        if (this.title != null) {
            generator.writeKey("title");
            generator.write(this.title);
        }
        if (this.parents != null) {
            generator.writeKey("parents");
            generator.writeStartArray();
            for (String item0 : this.parents) {
                generator.write(item0);

            }
            generator.writeEnd();
        }
    }
    public static class Builder extends ObjectBuilderBase implements ObjectBuilder<Categories> {
        private String alias, title;

        private List<String> parents;

        private List<String> country_whitelist;
        private List<String> country_blacklist;

        public Builder(){}


        public final Builder alias(String id) {
            this.alias = id;
            return this;
        }
        public final Builder title(String title) {
            this.title = title;
            return this;
        }

        public final Builder parents(List<String> parents) {
            this.parents = _listAddAll(this.parents, parents);
            return this;
        }

        public final Builder parents(String value, String... values) {
            this.parents = _listAdd(this.parents, value, values);
            return this;
        }

        public final Builder country_whitelist(List<String> parents) {
            this.country_whitelist = _listAddAll(this.country_whitelist, parents);
            return this;
        }

        public final Builder country_whitelist(String value, String... values) {
            this.country_whitelist = _listAdd(this.country_whitelist, value, values);
            return this;
        }
        public final Builder country_blacklist(List<String> parents) {
            this.country_blacklist = _listAddAll(this.country_blacklist, parents);
            return this;
        }

        public final Builder country_blacklist(String value, String... values) {
            this.country_blacklist = _listAdd(this.country_blacklist, value, values);
            return this;
        }


        public Categories build() {
            _checkSingleUse();
            return new Categories(this);
        }

    }

    @Override
    public String toString() {
        return "Category{" +
                "alias='" + alias + '\'' +
                ", title='" + title + '\'' +
                ", parents=" + parents +
                '}';
    }

    public static final JsonpDeserializer<Categories> _DESERIALIZER =
            ObjectBuilderDeserializer.lazy(Categories.Builder::new,
                    Categories::setupCategoriesDeserializer);

    protected static void setupCategoriesDeserializer(ObjectDeserializer<Categories.Builder> op) {

        op.add(Builder::alias, JsonpDeserializer.stringDeserializer(), "alias");
        op.add(Builder::title, JsonpDeserializer.stringDeserializer(), "title");
        op.add(Builder::parents, JsonpDeserializer.arrayDeserializer(JsonpDeserializer.stringDeserializer()), "parents");

    }
}
