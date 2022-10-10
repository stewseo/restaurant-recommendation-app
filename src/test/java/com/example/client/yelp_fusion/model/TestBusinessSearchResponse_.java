package com.example.client.yelp_fusion.model;

import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.util.*;

import java.util.function.*;

public class TestBusinessSearchResponse_ <TDocument> extends ResponseBody<TDocument> {

    protected TestBusinessSearchResponse_(AbstractBuilder<TDocument, ?> builder) {
        super(builder);
        System.out.println("test ");
    }

    public static <TDocument> TestBusinessSearchResponse_<TDocument> of(
            Function<TestBusinessSearchResponse_.Builder<TDocument>, ObjectBuilder<TestBusinessSearchResponse_<TDocument>>> fn) {
        return fn.apply(new TestBusinessSearchResponse_.Builder<>()).build();
    }

    public static class Builder<TDocument> extends ResponseBody.AbstractBuilder<TDocument, TestBusinessSearchResponse_.Builder<TDocument>>
            implements
            ObjectBuilder<TestBusinessSearchResponse_<TDocument>> {
        @Override
        protected TestBusinessSearchResponse_.Builder<TDocument> self() {
            return this;
        }

        public TestBusinessSearchResponse_<TDocument> build() {
            _checkSingleUse();

            return new TestBusinessSearchResponse_<TDocument>(this);
        }
    }
}
