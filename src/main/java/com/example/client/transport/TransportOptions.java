package com.example.client.transport;

import co.elastic.clients.transport.*;
import co.elastic.clients.util.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public interface TransportOptions {

    Map<String, String> headers();

    Map<String, String> queryParameters();

    Function<List<String>, Boolean> onWarnings();

    Builder toBuilder();

    default TransportOptions with(Consumer<Builder> fn) {
        Builder builder = toBuilder();
        fn.accept(builder);
        return builder.build();
    }

    interface Builder extends ObjectBuilder<TransportOptions> {

        Builder addHeader(String name, String value);

        Builder setParameter(String name, String value);

        Builder onWarnings(Function<List<String>, Boolean> listener);
    }
}