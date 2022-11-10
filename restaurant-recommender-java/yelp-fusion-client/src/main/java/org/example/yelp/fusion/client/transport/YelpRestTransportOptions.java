package org.example.yelp.fusion.client.transport;


import org.apache.http.impl.nio.client.*;
import org.apache.http.util.*;
import org.example.elasticsearch.client.transport.*;
import org.example.elasticsearch.client.util.*;
import org.example.lowlevel.restclient.*;


import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class YelpRestTransportOptions implements TransportOptions {

    private final RequestOptions options; // gets shared between multiple requests in an application

    static YelpRestTransportOptions of(TransportOptions options) {
        // if options have been set, return Transport Options
        if (options instanceof YelpRestTransportOptions) {
            return (YelpRestTransportOptions)options; // return the parameter options

        } else {
            final Builder builder = new Builder(RequestOptions.DEFAULT.toBuilder());
            options.headers().forEach(h -> builder.addHeader(h.getKey(), h.getValue()));
            options.queryParameters().forEach(builder::setParameter);
            builder.onWarnings(options.onWarnings());
            return builder.build(); // return TestYelpRestClientOptions with headers, query parameters, warnings
        }
    }

    public YelpRestTransportOptions(RequestOptions options) {this.options = addBuiltinHeaders(options.toBuilder()).build();}

    public RequestOptions restClientRequestOptions() {
        return this.options;
    }

    @Override
    public Collection<Map.Entry<String, String>> headers() {

        return options.getHeaders().stream()
                .map(h -> new AbstractMap.SimpleImmutableEntry<>(h.getName(), h.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Function<List<String>, Boolean> onWarnings() {
        final WarningsHandler handler = options.getWarningsHandler();
        if (handler == null) {
            return null;
        }

        return warnings -> options.getWarningsHandler().warningsShouldFailRequest(warnings);
    }

    @Override
    public Map<String, String> queryParameters() {
        return options.getParameters();
    }

    static YelpRestTransportOptions initialOptions() {
        return new YelpRestTransportOptions(RequestOptions.DEFAULT);
    }

    @Override
    public Builder toBuilder() {

        return PrintUtils.println("Yelp Rest Transport Options: " , new Builder(options.toBuilder()));
    }


    // class TestYelpRestOptions.Builder implements com.example.client.transport.TransportOptions.Builder
    public static class Builder implements TransportOptions.Builder {

        private final RequestOptions.Builder builder; //  RequestOptions

        // initializes RequestOptions.Builder
        public Builder(RequestOptions.Builder builder) {
            this.builder = builder;
        }

        // Get the wrapped Rest Client request options builder.
        public RequestOptions.Builder restClientRequestOptionsBuilder() {
            return this.builder;
        }
        // add headers to RequestOptions.Builder
        @Override
        public Builder addHeader(String name, String value) {
            builder.addHeader(name, value);
            return this;
        }

        // set parameters RequestOptions.Builder
        @Override
        public Builder setParameter(String name, String value) {
            builder.addParameter(name, value);
            return this;
        }

        // warnings for RequestOptions.Builder
        @Override
        public Builder onWarnings(Function<List<String>, Boolean> listener) {
            if (listener == null) {
                builder.setWarningsHandler(null);
            } else {
                builder.setWarningsHandler(w -> {
                    if (w != null && !w.isEmpty()) {
                        return listener.apply(w);
                    } else {
                        return false;
                    }
                });
            }

            return this;
        }

        @Override
        public YelpRestTransportOptions build() {
            return new YelpRestTransportOptions(addBuiltinHeaders(builder).build());
        }
    }

    private static RequestOptions.Builder addBuiltinHeaders(RequestOptions.Builder builder) {
        if (builder.getHeaders().stream().noneMatch(h -> h.getName().equalsIgnoreCase("Accept"))) {
            builder.addHeader("Accept", String.valueOf(YelpRestTransport.JsonContentType));
        }
        return builder;
    }

}
