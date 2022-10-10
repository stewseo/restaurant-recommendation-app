package com.example.client.yelp_fusion.model;

import com.example.client._types.Request;
import com.example.client._types.Response;
import com.example.client._types.ResponseException;
import org.apache.commons.logging.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.config.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.*;
import org.apache.http.client.utils.*;
import org.apache.http.conn.*;
import org.apache.http.impl.auth.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.nio.client.*;
import org.apache.http.nio.client.methods.*;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.*;
import org.elasticsearch.client.*;


import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;
import java.util.zip.*;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Collections.singletonList;

public class TestRestClient implements Closeable {

    private static final Log logger = LogFactory.getLog(TestRestClient.class);

    private final CloseableHttpAsyncClient client;
    // We don't rely on default headers supported by HttpAsyncClient as those cannot be replaced.
    // These are package private for tests.
    final List<Header> defaultHeaders;
    private final String pathPrefix;

    private final AtomicInteger lastNodeIndex = new AtomicInteger(0);
    private volatile TestRestClient.NodeTuple<List<Node>> nodeTuple;
    private final TestWarningsHandler warningsHandler;
    private final boolean compressionEnabled;
    private final boolean metaHeaderEnabled;

    TestRestClient(
            CloseableHttpAsyncClient client,
            Header[] defaultHeaders,
            List<Node> nodes,
            String pathPrefix,
            boolean strictDeprecationMode,
            boolean compressionEnabled,
            boolean metaHeaderEnabled
    ) {
        this.client = client;
        this.defaultHeaders = Collections.unmodifiableList(Arrays.asList(defaultHeaders));
        this.pathPrefix = pathPrefix;
        this.warningsHandler = strictDeprecationMode ? TestWarningsHandler.STRICT : TestWarningsHandler.PERMISSIVE;
        this.compressionEnabled = compressionEnabled;
        this.metaHeaderEnabled = metaHeaderEnabled;
        setNodes(nodes);

    }

    public static TestRestClientBuilder builder(String cloudId) {
        // there is an optional first portion of the cloudId that is a human readable string, but it is not used.
        if (cloudId.contains(":")) {
            if (cloudId.indexOf(":") == cloudId.length() - 1) {
                throw new IllegalStateException("cloudId " + cloudId + " must begin with a human readable identifier followed by a colon");
            }
            cloudId = cloudId.substring(cloudId.indexOf(":") + 1);
        }

        String decoded = new String(Base64.getDecoder().decode(cloudId), UTF_8);
        // once decoded the parts are separated by a $ character.
        // they are respectively domain name and optional port, elasticsearch id, kibana id
        String[] decodedParts = decoded.split("\\$");
        if (decodedParts.length != 3) {
            throw new IllegalStateException("cloudId " + cloudId + " did not decode to a cluster identifier correctly");
        }

        // domain name and optional port
        String[] domainAndMaybePort = decodedParts[0].split(":", 2);
        String domain = domainAndMaybePort[0];
        int port;

        if (domainAndMaybePort.length == 2) {
            try {
                port = Integer.parseInt(domainAndMaybePort[1]);
            } catch (NumberFormatException nfe) {
                throw new IllegalStateException("cloudId " + cloudId + " does not contain a valid port number");
            }
        } else {
            port = 443;
        }

        String url = decodedParts[1] + "." + domain;
        return builder(new HttpHost(url, port, "https"));
    }

    public static TestRestClientBuilder builder(Node... nodes) {
        return new TestRestClientBuilder(nodes == null ? null : Arrays.asList(nodes));
    }

    public static TestRestClientBuilder builder(HttpHost... hosts) {
        if (hosts == null || hosts.length == 0) {
            throw new IllegalArgumentException("hosts must not be null nor empty");
        }
        List<Node> nodes = Arrays.stream(hosts).map(Node::new).collect(Collectors.toList());
        return new TestRestClientBuilder(nodes);
    }
    public synchronized void setNodes(Collection<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("nodes must not be null or empty");
        }
        AuthCache authCache = new BasicAuthCache();
        Map<HttpHost, Node> nodesByHost = new LinkedHashMap<>();
        for (Node node : nodes) {
            Objects.requireNonNull(node, "node cannot be null");
            nodesByHost.put(node.getHost(), node);
            authCache.put(node.getHost(), new BasicScheme());
        }
        this.nodeTuple = new TestRestClient.NodeTuple<>(Collections.unmodifiableList(new ArrayList<>(nodesByHost.values())), authCache);
    }

    /**
     * Get the underlying HTTP client.
     */
    public HttpAsyncClient getHttpClient() {
        return this.client;
    }


    public Response performRequest(Request request) throws IOException {
        TestRestClient.InternalRequest internalRequest = new TestRestClient.InternalRequest(request);
        return performRequest(nextNodes(), internalRequest, null);
    }

    private Response performRequest(final TestRestClient.NodeTuple<Iterator<Node>> tuple, final TestRestClient.InternalRequest request, Exception previousException)
            throws IOException {
        //            RequestLogger.logFailedRequest(logger, request.httpRequest, context.node, e);
//            onFailure(context.node);


        TestRestClient.RequestContext context = request.createContextForNextAttempt(tuple.nodes.next(), tuple.authCache);

        HttpResponse httpResponse;
        try {
            httpResponse = client.execute(context.requestProducer, context.asyncResponseConsumer, context.context, null).get();
        } catch (Exception e) {
            Exception cause = extractAndWrapCause(e);
            addSuppressedException(previousException, cause);
            if (isRetryableException(e) && tuple.nodes.hasNext()) {
                return performRequest(tuple, request, cause);
            }
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new IllegalStateException("unexpected exception type: must be either RuntimeException or IOException", cause);
        }
        TestRestClient.ResponseOrResponseException responseOrResponseException = convertResponse(request, context.node, httpResponse);

        if (responseOrResponseException.responseException == null) {
            return responseOrResponseException.response;
        }
        addSuppressedException(previousException, responseOrResponseException.responseException);
        if (tuple.nodes.hasNext()) {
            return performRequest(tuple, request, responseOrResponseException.responseException);
        }
        throw responseOrResponseException.responseException;
    }

    private TestRestClient.ResponseOrResponseException convertResponse(TestRestClient.InternalRequest request, Node node, HttpResponse httpResponse) throws IOException {
        TestRequestLogger.logResponse(logger, request.httpRequest, node.getHost(), httpResponse);
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            Header header = entity.getContentEncoding();
            if (header != null && "gzip".equals(header.getValue())) {
                // Decompress and cleanup response headers
                httpResponse.setEntity(new GzipDecompressingEntity(entity));
                httpResponse.removeHeaders(HTTP.CONTENT_ENCODING);
                httpResponse.removeHeaders(HTTP.CONTENT_LEN);
            }
        }

        com.example.client._types.Response response = new com.example.client._types.Response(request.httpRequest.getRequestLine(), node.getHost(), httpResponse);
        if (isSuccessfulResponse(statusCode)) {
            if (request.warningsHandler.warningsShouldFailRequest(response.getWarnings())) {
                throw new TestWarningFailureException(response);
            }
            return new TestRestClient.ResponseOrResponseException(response);
        }
        ResponseException responseException = new ResponseException(response);
        if (isRetryStatus(statusCode)) {
            return new TestRestClient.ResponseOrResponseException(responseException);
        }
        throw responseException;
    }

    @Override
    public void close() throws IOException {

    }

    private class InternalRequest {
        private final Request request;
        private final HttpRequestBase httpRequest;
        private final com.example.client.yelp_fusion.model.TestCancelable cancellable;
        private final TestWarningsHandler warningsHandler;

        InternalRequest(com.example.client._types.Request request) {
            this.request = request;
            Map<String, String> params = new HashMap<>(request.getParameters());
            params.putAll(request.getOptions().getParameters());

            URI uri = buildUri(pathPrefix, request.getEndpoint(), params);
            
            this.httpRequest = createHttpRequest(request.getMethod(), uri, request.getEntity(), compressionEnabled);
            this.cancellable = TestCancelable.fromRequest(httpRequest);
            setHeaders(httpRequest, request.getOptions().getHeaders());
            setRequestConfig(httpRequest, request.getOptions().getRequestConfig());
            this.warningsHandler = TestRestClient.this.warningsHandler;
        }

        private void setHeaders(HttpRequest req, Collection<Header> requestHeaders) {
            // request headers override default headers, so we don't add default headers if they exist as request headers
            final Set<String> requestNames = new HashSet<>(requestHeaders.size());
            for (Header requestHeader : requestHeaders) {
                req.addHeader(requestHeader);
                requestNames.add(requestHeader.getName());
            }
            for (Header defaultHeader : defaultHeaders) {
                if (requestNames.contains(defaultHeader.getName()) == false) {
                    req.addHeader(defaultHeader);
                }
            }
            if (compressionEnabled) {
                req.addHeader("Accept-Encoding", "gzip");
            }
            if (metaHeaderEnabled) {
                if (req.containsHeader(TestRestClientBuilder.META_HEADER_NAME) == false) {
                    req.setHeader(TestRestClientBuilder.META_HEADER_NAME, TestRestClientBuilder.META_HEADER_VALUE);
                }
            } else {
                req.removeHeaders(TestRestClientBuilder.META_HEADER_NAME);
            }
        }

        private void setRequestConfig(HttpRequestBase requestBase, RequestConfig requestConfig) {
            if (requestConfig != null) {
                requestBase.setConfig(requestConfig);
            }
        }

        TestRestClient.RequestContext createContextForNextAttempt(Node node, AuthCache authCache) {
            this.httpRequest.reset();
            return new TestRestClient.RequestContext(this, node, authCache);
        }
    }

    private static HttpRequestBase createHttpRequest(String method, URI uri, HttpEntity entity, boolean compressionEnabled) {
        switch (method.toUpperCase(Locale.ROOT)) {
            case HttpDeleteWithEntity.METHOD_NAME:
                return addRequestBody(new HttpDeleteWithEntity(uri), entity, compressionEnabled);
            case HttpGetWithEntity.METHOD_NAME:
                return addRequestBody(new HttpGetWithEntity(uri), entity, compressionEnabled);
            case HttpHead.METHOD_NAME:
                return addRequestBody(new HttpHead(uri), entity, compressionEnabled);
            case HttpOptions.METHOD_NAME:
                return addRequestBody(new HttpOptions(uri), entity, compressionEnabled);
            case HttpPatch.METHOD_NAME:
                return addRequestBody(new HttpPatch(uri), entity, compressionEnabled);
            case HttpPost.METHOD_NAME:
                HttpPost httpPost = new HttpPost(uri);
                addRequestBody(httpPost, entity, compressionEnabled);
                return httpPost;
            case HttpPut.METHOD_NAME:
                return addRequestBody(new HttpPut(uri), entity, compressionEnabled);
            case HttpTrace.METHOD_NAME:
                return addRequestBody(new HttpTrace(uri), entity, compressionEnabled);
            default:
                throw new UnsupportedOperationException("http method not supported: " + method);
        }
    }

    private static HttpRequestBase addRequestBody(HttpRequestBase httpRequest, HttpEntity entity, boolean compressionEnabled) {
        if (entity != null) {
            if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
                if (compressionEnabled) {
                    entity = new TestRestClient.ContentCompressingEntity(entity);
                }
                ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(entity);
            } else {
                throw new UnsupportedOperationException(httpRequest.getMethod() + " with body is not supported");
            }
        }
        return httpRequest;
    }

    static URI buildUri(String pathPrefix, String path, Map<String, String> params) {
        Objects.requireNonNull(path, "path must not be null");
        try {
            String fullPath;
            if (pathPrefix != null && pathPrefix.isEmpty() == false) {
                if (pathPrefix.endsWith("/") && path.startsWith("/")) {
                    fullPath = pathPrefix.substring(0, pathPrefix.length() - 1) + path;
                } else if (pathPrefix.endsWith("/") || path.startsWith("/")) {
                    fullPath = pathPrefix + path;
                } else {
                    fullPath = pathPrefix + "/" + path;
                }
            } else {
                fullPath = path;
            }

            URIBuilder uriBuilder = new URIBuilder(fullPath);
            for (Map.Entry<String, String> param : params.entrySet()) {
                uriBuilder.addParameter(param.getKey(), param.getValue());
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private static class RequestContext {
        private final Node node;
        private final HttpAsyncRequestProducer requestProducer;
        private final HttpAsyncResponseConsumer<HttpResponse> asyncResponseConsumer;
        private final HttpClientContext context;

        RequestContext(TestRestClient.InternalRequest request, Node node, AuthCache authCache) {
            this.node = node;
            // we stream the request body if the entity allows for it
            this.requestProducer = HttpAsyncMethods.create(node.getHost(), request.httpRequest);
            this.asyncResponseConsumer = request.request.getOptions()
                    .getHttpAsyncResponseConsumerFactory()
                    .createHttpAsyncResponseConsumer();
            this.context = HttpClientContext.create();
            context.setAuthCache(authCache);
        }
    }

    private static Set<Integer> getIgnoreErrorCodes(String ignoreString, String requestMethod) {
        Set<Integer> ignoreErrorCodes;
        if (ignoreString == null) {
            if (HttpHead.METHOD_NAME.equals(requestMethod)) {
                // 404 never causes error if returned for a HEAD request
                ignoreErrorCodes = Collections.singleton(404);
            } else {
                ignoreErrorCodes = Collections.emptySet();
            }
        } else {
            String[] ignoresArray = ignoreString.split(",");
            ignoreErrorCodes = new HashSet<>();
            if (HttpHead.METHOD_NAME.equals(requestMethod)) {
                // 404 never causes error if returned for a HEAD request
                ignoreErrorCodes.add(404);
            }
            for (String ignoreCode : ignoresArray) {
                try {
                    ignoreErrorCodes.add(Integer.valueOf(ignoreCode));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("ignore value should be a number, found [" + ignoreString + "] instead", e);
                }
            }
        }
        return ignoreErrorCodes;
    }


    private static Exception extractAndWrapCause(Exception exception) {
        if (exception instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("thread waiting for the response was interrupted", exception);
        }
        if (exception instanceof ExecutionException) {
            ExecutionException executionException = (ExecutionException) exception;
            Throwable t = executionException.getCause() == null ? executionException : executionException.getCause();
            if (t instanceof Error) {
                throw (Error) t;
            }
            exception = (Exception) t;
        }
        if (exception instanceof ConnectTimeoutException) {
            ConnectTimeoutException e = new ConnectTimeoutException(exception.getMessage());
            e.initCause(exception);
            return e;
        }
        if (exception instanceof SocketTimeoutException) {
            SocketTimeoutException e = new SocketTimeoutException(exception.getMessage());
            e.initCause(exception);
            return e;
        }
        if (exception instanceof ConnectionClosedException) {
            ConnectionClosedException e = new ConnectionClosedException(exception.getMessage());
            e.initCause(exception);
            return e;
        }
        if (exception instanceof SSLHandshakeException) {
            SSLHandshakeException e = new SSLHandshakeException(exception.getMessage());
            e.initCause(exception);
            return e;
        }
        if (exception instanceof ConnectException) {
            ConnectException e = new ConnectException(exception.getMessage());
            e.initCause(exception);
            return e;
        }
        if (exception instanceof IOException) {
            return new IOException(exception.getMessage(), exception);
        }
        if (exception instanceof RuntimeException) {
            return new RuntimeException(exception.getMessage(), exception);
        }
        return new RuntimeException("error while performing request", exception);
    }

    /**
     * A gzip compressing entity that also implements {@code getContent()}.
     */
    public static class ContentCompressingEntity extends GzipCompressingEntity {

        public ContentCompressingEntity(HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException {
            TestRestClient.ByteArrayInputOutputStream out = new TestRestClient.ByteArrayInputOutputStream(1024);
            try (GZIPOutputStream gzipOut = new GZIPOutputStream(out)) {
                wrappedEntity.writeTo(gzipOut);
            }
            return out.asInput();
        }
    }

    /**
     * A ByteArrayOutputStream that can be turned into an input stream without copying the underlying buffer.
     */
    private static class ByteArrayInputOutputStream extends ByteArrayOutputStream {
        ByteArrayInputOutputStream(int size) {
            super(size);
        }

        public InputStream asInput() {
            return new ByteArrayInputStream(this.buf, 0, this.count);
        }
    }


    private TestRestClient.NodeTuple<Iterator<Node>> nextNodes() throws IOException {
        TestRestClient.NodeTuple<List<Node>> tuple = this.nodeTuple;
        Iterable<Node> hosts = selectNodes(tuple, lastNodeIndex);
        return new TestRestClient.NodeTuple<>(hosts.iterator(), tuple.authCache);
    }
    static Iterable<Node> selectNodes(
            TestRestClient.NodeTuple<List<Node>> nodeTuple,
            AtomicInteger lastNodeIndex
    ) throws IOException {
        for (Node node : nodeTuple.nodes) {

        }
        return null;
    }

    static class FailureTrackingResponseListener {
        private final com.example.client.yelp_fusion.model.ResponseListener responseListener;
        private volatile Exception exception;

        FailureTrackingResponseListener(ResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        void onSuccess(Response response) {
            responseListener.onSuccess(response);
        }

        void onDefinitiveFailure(Exception e) {
            trackFailure(e);
            responseListener.onFailure(this.exception);
        }

        void trackFailure(Exception e) {
            addSuppressedException(this.exception, e);
            this.exception = e;
        }
    }


    public static class FailureListener {
        /**
         * Notifies that the node provided as argument has just failed
         */
        public void onFailure(Node node) {}
    }

    static class NodeTuple<T> {
        final T nodes;
        final AuthCache authCache;

        NodeTuple(final T nodes, final AuthCache authCache) {
            this.nodes = nodes;
            this.authCache = authCache;
        }
    }

    private static boolean isSuccessfulResponse(int statusCode) {
        return statusCode < 300;
    }

    private static boolean isRetryableException(Throwable e) {
        if (e instanceof ExecutionException) {
            e = e.getCause();
        }
        if (e instanceof ContentTooLongException) {
            return false;
        }
        return true;
    }

    private static boolean isRetryStatus(int statusCode) {
        switch (statusCode) {
            case 502:
            case 503:
            case 504:
                return true;
        }
        return false;
    }

    private static void addSuppressedException(Exception suppressedException, Exception currentException) {
        if (suppressedException != null && suppressedException != currentException) {
            currentException.addSuppressed(suppressedException);
        }
    }

    private static class ResponseOrResponseException {
        private final Response response;
        private final ResponseException responseException;

        ResponseOrResponseException(Response response) {
            this.response = Objects.requireNonNull(response);
            this.responseException = null;
        }

        ResponseOrResponseException(ResponseException responseException) {
            this.responseException = Objects.requireNonNull(responseException);
            this.response = null;
        }
    }


}
