package org.example.lowlevel.restclient;

import org.apache.commons.logging.*;
import org.apache.http.*;
import org.apache.http.client.config.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.util.*;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.util.*;


public final class RestClientBuilder {

    private static final Log logger = LogFactory.getLog(RestClientBuilder.class);
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 30000;
    public static final int DEFAULT_MAX_CONN_PER_ROUTE = 10;
    public static final int DEFAULT_MAX_CONN_TOTAL = 30;

    public static final String VERSION;
    static final String META_HEADER_NAME = "X-Elastic-Client-Meta";
    static final String META_HEADER_VALUE;
    private static final String USER_AGENT_HEADER_VALUE;

    private static final Header[] EMPTY_HEADERS = new Header[0];

    private final List<Node> nodes;
    private Header[] defaultHeaders = EMPTY_HEADERS;
    private RestClient.FailureListener failureListener;
    private RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback;
    private RestClientBuilder.RequestConfigCallback requestConfigCallback;
    private String pathPrefix;
    private NodeSelector nodeSelector = NodeSelector.ANY;
    private boolean strictDeprecationMode = false;
    private boolean compressionEnabled = false;
    private boolean metaHeaderEnabled = true;

    static {

        // Never fail on unknown version, even if an environment messed up their classpath enough that we can't find it.
        // Better have incomplete telemetry than crashing user applications.
        String version = null;
        try (InputStream is = RestClient.class.getResourceAsStream("version.properties")) {
            if (is != null) {
                Properties versions = new Properties();
                versions.load(is);
                version = versions.getProperty("elasticsearch-client");
            }
        } catch (IOException e) {
            // Keep version unknown
        }

        if (version == null) {
            version = ""; // unknown values are reported as empty strings in X-Elastic-Client-Meta
        }

        VERSION = version;

        USER_AGENT_HEADER_VALUE = String.format(
                Locale.ROOT,
                "elasticsearch-java/%s (Java/%s)",
                VERSION.isEmpty() ? "Unknown" : VERSION,
                System.getProperty("java.version")
        );

        VersionInfo httpClientVersion = null;
        try {
            //noinspection removal
            httpClientVersion = AccessController.doPrivileged(
                    (PrivilegedAction<VersionInfo>) () -> VersionInfo.loadVersionInfo(
                            "org.apache.http.nio.client",
                            HttpAsyncClientBuilder.class.getClassLoader()
                    )
            );
        } catch (Exception e) {
            // Keep unknown
        }

        // Use a single 'p' suffix for all prerelease versions (snapshot, beta, etc).
        String metaVersion = version;
        int dashPos = metaVersion.indexOf('-');
        if (dashPos > 0) {
            metaVersion = metaVersion.substring(0, dashPos) + "p";
        }

        // service, language, transport, followed by additional information
        META_HEADER_VALUE = "es="
                + metaVersion
                + ",jv="
                + System.getProperty("java.specification.version")
                + ",t="
                + metaVersion
                + ",hc="
                + (httpClientVersion == null ? "" : httpClientVersion.getRelease());
    }

    RestClientBuilder(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("nodes must not be null or empty");
        }
        for (Node node : nodes) {
            if (node == null) {
                throw new IllegalArgumentException("node cannot be null");
            }
        }
        this.nodes = nodes;
    }


    public RestClientBuilder setDefaultHeaders(Header[] defaultHeaders) {
        Objects.requireNonNull(defaultHeaders, "defaultHeaders must not be null");
        for (Header defaultHeader : defaultHeaders) {
            Objects.requireNonNull(defaultHeader, "default header must not be null");
        }
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public RestClientBuilder setFailureListener(RestClient.FailureListener failureListener) {
        Objects.requireNonNull(failureListener, "failureListener must not be null");
        this.failureListener = failureListener;
        return this;
    }


    public RestClientBuilder setHttpClientConfigCallback(RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback) {
        Objects.requireNonNull(httpClientConfigCallback, "httpClientConfigCallback must not be null");
        this.httpClientConfigCallback = httpClientConfigCallback;
        return this;
    }

    public RestClientBuilder setRequestConfigCallback(RestClientBuilder.RequestConfigCallback requestConfigCallback) {
        Objects.requireNonNull(requestConfigCallback, "requestConfigCallback must not be null");
        this.requestConfigCallback = requestConfigCallback;
        return this;
    }


    public RestClientBuilder setPathPrefix(String pathPrefix) {
        this.pathPrefix = cleanPathPrefix(pathPrefix);
        return this;
    }

    public static String cleanPathPrefix(String pathPrefix) {
        Objects.requireNonNull(pathPrefix, "pathPrefix must not be null");

        if (pathPrefix.isEmpty()) {
            throw new IllegalArgumentException("pathPrefix must not be empty");
        }

        String cleanPathPrefix = pathPrefix;
        if (cleanPathPrefix.startsWith("/") == false) {
            cleanPathPrefix = "/" + cleanPathPrefix;
        }

        // best effort to ensure that it looks like "/base/path" rather than "/base/path/"
        if (cleanPathPrefix.endsWith("/") && cleanPathPrefix.length() > 1) {
            cleanPathPrefix = cleanPathPrefix.substring(0, cleanPathPrefix.length() - 1);

            if (cleanPathPrefix.endsWith("/")) {
                throw new IllegalArgumentException("pathPrefix is malformed. too many trailing slashes: [" + pathPrefix + "]");
            }
        }
        return cleanPathPrefix;
    }

    public RestClientBuilder setNodeSelector(NodeSelector nodeSelector) {
        Objects.requireNonNull(nodeSelector, "nodeSelector must not be null");
        this.nodeSelector = nodeSelector;
        return this;
    }

    public RestClientBuilder setStrictDeprecationMode(boolean strictDeprecationMode) {
        this.strictDeprecationMode = strictDeprecationMode;
        return this;
    }

    public RestClientBuilder setCompressionEnabled(boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
        return this;
    }

    public RestClientBuilder setMetaHeaderEnabled(boolean metadataEnabled) {
        this.metaHeaderEnabled = metadataEnabled;
        return this;
    }

    @SuppressWarnings("removal")
    public RestClient build() {

        if (failureListener == null) {
            failureListener = new RestClient.FailureListener();
        }
        CloseableHttpAsyncClient httpClient = AccessController.doPrivileged(
                (PrivilegedAction<CloseableHttpAsyncClient>) this::createHttpClient
        );
        RestClient restClient = new RestClient(
                httpClient,
                defaultHeaders,
                nodes,
                pathPrefix,
                failureListener,
                nodeSelector,
                strictDeprecationMode,
                compressionEnabled,
                metaHeaderEnabled
        );

               PrintUtils.cyan(String.format("http client = %s%n default headers = %s%n nodes = %s%n pathPrefix = %s%n",
                       httpClient,
                       Arrays.toString(defaultHeaders),
                       nodes,
                       pathPrefix));

        httpClient.start();
        return restClient;
    }

    @SuppressWarnings("removal")
    private CloseableHttpAsyncClient createHttpClient() {
        // default timeouts are all infinite
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT_MILLIS);
        if (requestConfigCallback != null) {
            requestConfigBuilder = requestConfigCallback.customizeRequestConfig(requestConfigBuilder);
        }

        try {
            HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClientBuilder.create()
                    .setDefaultRequestConfig(requestConfigBuilder.build())
                    // default settings for connection pooling may be too constraining
                    .setMaxConnPerRoute(DEFAULT_MAX_CONN_PER_ROUTE)
                    .setMaxConnTotal(DEFAULT_MAX_CONN_TOTAL)
                    .setSSLContext(SSLContext.getDefault())
                    .setUserAgent(USER_AGENT_HEADER_VALUE)
                    .setTargetAuthenticationStrategy(new PersistentCredentialsAuthenticationStrategy());
            if (httpClientConfigCallback != null) {
                httpClientBuilder = httpClientConfigCallback.customizeHttpClient(httpClientBuilder);
            }

            final HttpAsyncClientBuilder finalBuilder = httpClientBuilder;
            return AccessController.doPrivileged((PrivilegedAction<CloseableHttpAsyncClient>) finalBuilder::build);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("could not create the default ssl context", e);
        }
    }


    public interface RequestConfigCallback {
        RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder);
    }

    public interface HttpClientConfigCallback {
        HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder);
    }
}
