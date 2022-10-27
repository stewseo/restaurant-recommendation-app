package org.example.elasticsearch.client.transport.restclient;


import org.apache.http.util.*;
import org.example.elasticsearch.client.transport.*;
import org.example.lowlevel.restclient.*;

import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class RestClientOptions implements TransportOptions {


    static{
        PrintUtils.green("YelpRestTransportOptions");
    }
    private final RequestOptions options;
    private static final String CLIENT_META_HEADER = "X-Elastic-Client-Meta";
    private static final String USER_AGENT_HEADER = "User-Agent";
//    static final String CLIENT_META_VALUE = getClientMeta();
    static final String USER_AGENT_VALUE = getUserAgent();

    public static RestClientOptions of(TransportOptions options) {
        if (options instanceof RestClientOptions) {
            return (RestClientOptions)options;

        } else {
            final Builder builder = new Builder(RequestOptions.DEFAULT.toBuilder());
            options.headers().forEach(h -> builder.addHeader(h.getKey(), h.getValue()));
            options.queryParameters().forEach(builder::setParameter);
            builder.onWarnings(options.onWarnings());
            return builder.build();
        }
    }

    private static String getBase64EncodedApiKey(String id, String secret) {
        String utf16EsAuthentication = id + ":" + secret;
        return
                Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                        .encodeToString((utf16EsAuthentication) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                                .getBytes(StandardCharsets.UTF_8));


    }

    public RestClientOptions(RequestOptions options) {
        this.options = options.toBuilder().build();
    }

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
    public Map<String, String> queryParameters() {
        return options.getParameters();
    }

    @Override
    public Function<List<String>, Boolean> onWarnings() {
        return null;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(options.toBuilder());
    }


    @Override
    public TransportOptions with(Consumer<TransportOptions.Builder> fn) {
        return TransportOptions.super.with(fn);
    }

    public static class Builder implements TransportOptions.Builder {
        private RequestOptions.Builder builder;

        public Builder(RequestOptions.Builder builder) {
            this.builder = builder;
        }

        public RequestOptions.Builder restClientRequestOptionsBuilder() {
            return this.builder;
        }

        @Override
        public TransportOptions.Builder addHeader(String name, String value) {
            builder.addHeader(name, value);
            return this;
        }

        @Override
        public TransportOptions.Builder setParameter(String name, String value) {
            builder.addParameter(name, value);
            return this;
        }

        @Override
        public TransportOptions.Builder onWarnings(Function<List<String>, Boolean> listener) {
            return null;
        }

        @Override
        public RestClientOptions build() {
            return new RestClientOptions(addBuiltInHeaders(builder).build());
        }

    }

    static RestClientOptions initialOptions() {
        return new RestClientOptions(RequestOptions.DEFAULT);
    }


    private static RequestOptions.Builder addBuiltInHeaders(RequestOptions.Builder builder) {

        builder.removeHeader(CLIENT_META_HEADER);
//        builder.addHeader(CLIENT_META_HEADER, CLIENT_META_VALUE);
        if (builder.getHeaders().stream().noneMatch(h -> h.getName().equalsIgnoreCase(USER_AGENT_HEADER))) {
            builder.addHeader(USER_AGENT_HEADER, USER_AGENT_VALUE);
        }
        if (builder.getHeaders().stream().noneMatch(h -> h.getName().equalsIgnoreCase("Accept"))) {
            builder.addHeader("Accept", RestClientTransport.JsonContentType.toString());
        }


//        if (builder.getHeaders().get("Accept") == null) {
//            System.out.println("\n"+USER_AGENT_HEADER + "\n" + RestClientTransport.jsonContentType.toString());
//            builder.addHeader("Accept", RestClientTransport.jsonContentType.toString());
//        }

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

//    private static String getClientMeta() {
//        VersionInfo httpClientVersion = null;
//        try {
//            httpClientVersion = VersionInfo.loadVersionInfo(
//
////                    "org.apache.http.nio.client" ,
////                    );
//
//        } catch (Exception e) {
//            // Keep unknown
//        }

        // Use a single 'p' suffix for all prerelease versions (snapshot, beta, etc).
//        String metaVersion = Version.VERSION == null ? "" : Version.VERSION.toString();
//        int dashPos = metaVersion.indexOf('-');
//        if (dashPos > 0) {
//            metaVersion = metaVersion.substring(0, dashPos) + "p";
//        }
//
//        // service, language, transport, followed by additional information
//        return "es="
//                + metaVersion
//                + ",jv="
//                + System.getProperty("java.specification.version")
//                + ",hl=2"
//                + ",t="
//                + metaVersion
//                + ",hc="
//                + (httpClientVersion == null ? "" : httpClientVersion.getRelease());
////                + co.elastic.clients.transport.rest_client.LanguageRuntimeVersions.getRuntimeMetadata();
//    }

}