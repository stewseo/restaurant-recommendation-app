package com.example.client.transport.restclient;


import co.elastic.clients.transport.*;
import co.elastic.clients.util.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.util.*;
import org.elasticsearch.client.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

// set RequestOptions
public class TestYelpRestClientOptions implements com.example.client.transport.TransportOptions {

    private RequestOptions options; // gets shared between multiple requests in an application

    private static final String CLIENT_META_HEADER = "X-Elastic-Client-Meta";
    private static final String USER_AGENT_HEADER = "User-Agent";

    @VisibleForTesting
    static final String CLIENT_META_VALUE = getClientMeta();
    @VisibleForTesting
    static final String USER_AGENT_VALUE = getUserAgent();

    static TestYelpRestClientOptions of(com.example.client.transport.TransportOptions options) {
        // if options have been set, return Transport Options
        if (options instanceof TestYelpRestClientOptions) {
            return (TestYelpRestClientOptions)options; // return the parameter options

        } else {
            final TestYelpRestClientOptions.Builder builder = new TestYelpRestClientOptions.Builder(RequestOptions.DEFAULT.toBuilder());
            options.headers().forEach(h -> builder.addHeader(h.getKey(), h.getValue()));
            options.queryParameters().forEach(builder::setParameter);
            builder.onWarnings(options.onWarnings());
            return builder.build(); // return TestYelpRestClientOptions with headers, query parameters, warnings
        }
    }
    public TestYelpRestClientOptions(RequestOptions options) {this.options = addBuiltinHeaders(options.toBuilder()).build();}

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

    static TestYelpRestClientOptions initialOptions() {
        return new TestYelpRestClientOptions(RequestOptions.DEFAULT);
    }
    @Override
    public Builder toBuilder() {
        return new Builder(options.toBuilder());
    }

    // class TestYelpRestOptions.Builder implements com.example.client.transport.TransportOptions.Builder
    public static class Builder implements com.example.client.transport.TransportOptions.Builder {

        // The portion of an HTTP request to Elasticsearch that can be manipulated without changing Elasticsearch's behavior.
        private RequestOptions.Builder builder; //  org.elasticsearch.client.RequestOptions

        // initializes org.elasticsearch.client.RequestOptions.Builder
        public Builder(org.elasticsearch.client.RequestOptions.Builder builder) {
            this.builder = builder;
        }

        // Get the wrapped Rest Client request options builder.
        public org.elasticsearch.client.RequestOptions.Builder restClientRequestOptionsBuilder() {
            return this.builder;
        }
        // add headers to org.elasticsearch.client.RequestOptions.Builder
        @Override
        public Builder addHeader(String name, String value) {
            if (name.equalsIgnoreCase(CLIENT_META_HEADER)) {
                // Not overridable
                return this;
            }
            if (name.equalsIgnoreCase(USER_AGENT_HEADER)) {
                // We must remove our own user-agent from the options, or we'll end up with multiple values for the header
                builder.removeHeader(USER_AGENT_HEADER);
            }
            builder.addHeader(name, value);
            return this;
        }

        // set parameters org.elasticsearch.client.RequestOptions.Builder
        @Override
        public Builder setParameter(String name, String value) {
            builder.addParameter(name, value);
            return this;
        }

        // warnings for org.elasticsearch.client.RequestOptions.Builder
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

        // returns an initialized TestYelpRestClientOptions with params:
        // an org.elasticsearch.client.RequestOptions.Builder instance
        // as parameters for addBuiltinHeaders(builder)
        @Override
        public TestYelpRestClientOptions build() {
            return new TestYelpRestClientOptions(addBuiltinHeaders(builder).build());
        }
    }

    private static RequestOptions.Builder addBuiltinHeaders(RequestOptions.Builder builder) {
        builder.removeHeader(CLIENT_META_HEADER);
        builder.addHeader(CLIENT_META_HEADER, CLIENT_META_VALUE);
        if (builder.getHeaders().stream().noneMatch(h -> h.getName().equalsIgnoreCase(USER_AGENT_HEADER))) {
            builder.addHeader(USER_AGENT_HEADER, USER_AGENT_VALUE);
        }
        if (builder.getHeaders().stream().noneMatch(h -> h.getName().equalsIgnoreCase("Accept"))) {
            builder.addHeader("Accept", TestYelpRestClientTransport.JsonContentType.toString());
        }
        return builder;
    }

    private static String getUserAgent() {
        return String.format(
                Locale.ROOT,
                "elastic-java/%s (Java/%s)",
                Version.VERSION == null ? "Unknown" : Version.VERSION.toString(),
                System.getProperty("java.version")
        );
    }

    private static String getClientMeta() {
        VersionInfo httpClientVersion = null;
        try {
            httpClientVersion = VersionInfo.loadVersionInfo(
                    "org.apache.http.nio.client",
                    HttpAsyncClientBuilder.class.getClassLoader()
            );
        } catch (Exception e) {
            // Keep unknown
        }

        // Use a single 'p' suffix for all prerelease versions (snapshot, beta, etc).
        String metaVersion = Version.VERSION == null ? "" : Version.VERSION.toString();
        int dashPos = metaVersion.indexOf('-');
        if (dashPos > 0) {
            metaVersion = metaVersion.substring(0, dashPos) + "p";
        }

        // service, language, transport, followed by additional information
        return "es="
                + metaVersion
                + ",jv="
                + System.getProperty("java.specification.version")
                + ",hl=2"
                + ",t="
                + metaVersion
                + ",hc="
                + (httpClientVersion == null ? "" : httpClientVersion.getRelease());
    }
}
