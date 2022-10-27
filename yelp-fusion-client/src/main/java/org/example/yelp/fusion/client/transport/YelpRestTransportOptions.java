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

    static{
        PrintUtils.green("YelpRestTransportOptions");
    }
    private final RequestOptions options; // gets shared between multiple requests in an application

    private static final String CLIENT_META_HEADER = "X-Elastic-Client-Meta";
    private static final String USER_AGENT_HEADER = "User-Agent";

    @VisibleForTesting
    static final String CLIENT_META_VALUE = getClientMeta();
    @VisibleForTesting
    static final String USER_AGENT_VALUE = getUserAgent();

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

        // The portion of an HTTP request to Elasticsearch that can be manipulated without changing Elasticsearch's behavior.
        private RequestOptions.Builder builder; //  RequestOptions

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
            if (name.equalsIgnoreCase(CLIENT_META_HEADER)) {
                PrintUtils.green("Add Header CLIENT_META_HEADER YelpRestTransportOptions");
                // Not overridable
                return this;
            }
            if (name.equalsIgnoreCase(USER_AGENT_HEADER)) {
                PrintUtils.green("Add Header USER_AGENT_HEADER YelpRestTransportOptions");
                // We must remove our own user-agent from the options, or we'll end up with multiple values for the header
                builder.removeHeader(USER_AGENT_HEADER);
            }
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

        // returns an initialized TestYelpRestClientOptions with params:
        // an RequestOptions.Builder instance
        // as parameters for addBuiltinHeaders(builder)
        @Override
        public YelpRestTransportOptions build() {
            return new YelpRestTransportOptions(addBuiltinHeaders(builder).build());
        }
    }

    private static RequestOptions.Builder addBuiltinHeaders(RequestOptions.Builder builder) {
        builder.removeHeader(CLIENT_META_HEADER);
        builder.addHeader(CLIENT_META_HEADER, CLIENT_META_VALUE);
        if (builder.getHeaders().stream().noneMatch(h -> h.getName().equalsIgnoreCase(USER_AGENT_HEADER))) {
            builder.addHeader(USER_AGENT_HEADER, USER_AGENT_VALUE);
        }
        if (builder.getHeaders().stream().noneMatch(h -> h.getName().equalsIgnoreCase("Accept"))) {
            builder.addHeader("Accept", String.valueOf(YelpRestTransport.JsonContentType));
        }
        return builder;
    }

    private static String getUserAgent() {
        return String.format(
                Locale.ROOT,
                "elastic-java/ (Java/%s)",
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

        // service, language, transport, followed by additional information
        return "es="
                + ",jv="
                + System.getProperty("java.specification.version")
                + ",hl=2"
                + ",t="
                + ",hc="
                + (httpClientVersion == null ? "" : httpClientVersion.getRelease());
    }
}
