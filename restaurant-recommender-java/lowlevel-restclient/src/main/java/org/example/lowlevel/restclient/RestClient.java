package org.example.lowlevel.restclient;

import org.apache.commons.logging.*;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.*;
import org.apache.http.client.config.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.*;
import org.apache.http.client.utils.*;
import org.apache.http.concurrent.*;
import org.apache.http.conn.*;
import org.apache.http.impl.auth.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.nio.client.*;
import org.apache.http.nio.client.methods.*;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.zip.*;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Collections.*;

public class RestClient implements Closeable {
    private static final Log logger = LogFactory.getLog(RestClient.class);

    public static RequestOptions COMMON_OPTIONS;
    private final CloseableHttpAsyncClient client;
    // We don't rely on default headers supported by HttpAsyncClient as those cannot be replaced.
// These are package private for tests.
    final List<Header> defaultHeaders;
    private final String pathPrefix;
    private final AtomicInteger lastNodeIndex = new AtomicInteger(0);
    private final ConcurrentMap<HttpHost, DeadHostState> blacklist = new ConcurrentHashMap<>();
    private final FailureListener failureListener;
    private final NodeSelector nodeSelector;
    private volatile NodeTuple<List<Node>> nodeTuple;
    private final WarningsHandler warningsHandler;
    private final boolean compressionEnabled;
    private final boolean metaHeaderEnabled;

    RestClient(
            CloseableHttpAsyncClient client,
            Header[] defaultHeaders,
            List<Node> nodes,
            String pathPrefix,
            FailureListener failureListener,
            NodeSelector nodeSelector,
            boolean strictDeprecationMode,
            boolean compressionEnabled,
            boolean metaHeaderEnabled
    ) {
        this.client = client;
        this.defaultHeaders = Collections.unmodifiableList(Arrays.asList(defaultHeaders));
        this.failureListener = failureListener;
        this.pathPrefix = pathPrefix;
        this.nodeSelector = nodeSelector;
        this.warningsHandler = strictDeprecationMode ? WarningsHandler.STRICT : WarningsHandler.PERMISSIVE;
        this.compressionEnabled = compressionEnabled;
        this.metaHeaderEnabled = metaHeaderEnabled;
        setNodes(nodes);
    }

    public static RestClientBuilder builder(String cloudId) {
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

    public static RestClientBuilder builder(Node... nodes) {
        return new RestClientBuilder(nodes == null ? null : Arrays.asList(nodes));
    }


    public static RestClientBuilder builder(HttpHost... hosts) {
        if (hosts == null || hosts.length == 0) {
            throw new IllegalArgumentException("hosts must not be null nor empty");
        }
        List<Node> nodes = Arrays.stream(hosts) // stream all input parameter hosts
                .map(Node::new) //Returns a stream consisting of the results of applying the given function to the elements of this stream.
                .collect(Collectors.toList());   // Performs a mutable reduction operation on the elements of this stream using a Collector.
                                                // A Collector encapsulates the functions used as arguments to collect(Supplier, BiConsumer, BiConsumer),
                                               // allowing for reuse of collection strategies and composition of collect operations such as multiple-level grouping or partitioning.

        return new RestClientBuilder(nodes);
    }

    public HttpAsyncClient getHttpClient() {
        return this.client;
    }

    public synchronized void setNodes(Collection<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("nodes must not be null or empty");
        }
        AuthCache authCache = new BasicAuthCache();

        Map<HttpHost, Node> nodesByHost = new LinkedHashMap<>();
        for (Node node : nodes) {
            Objects.requireNonNull(node, "node cannot be null");
            // TODO should we throw an IAE if we have two nodes with the same host?
            nodesByHost.put(node.getHost(), node);
            authCache.put(node.getHost(), new BasicScheme());
        }
        this.nodeTuple = new NodeTuple<>(Collections.unmodifiableList(new ArrayList<>(nodesByHost.values())), authCache);
        this.blacklist.clear();
    }

    public List<Node> getNodes() {
        return nodeTuple.nodes;
    }
    
    public boolean isRunning() {
        return client.isRunning();
    }

    public String add(String string, Function<String, String> fn) {
        return fn.apply(string);
    }

    public Response performRequest(Request request) throws IOException {
        PrintUtils.green(String.format("performRequest %s", request));
        InternalRequest internalRequest = new InternalRequest(request);

        if(internalRequest.httpRequest.getAllHeaders().length == 0) {
            PrintUtils.green("httpRequest getAllHeaders == 0");
        }
        return performRequest(nextNodes(), internalRequest, null);
    }


    private Response performRequest(final NodeTuple<Iterator<Node>> tuple, final InternalRequest request, Exception previousException)
            throws IOException {

        RequestContext context = request.createContextForNextAttempt(tuple.nodes.next(), tuple.authCache);

        PrintUtils.red(String.format("context.requestProducer = %s%n context.asyncResponseConsumer %s%n context.node = %s"+
                context.requestProducer.toString() + " " +
                context.asyncResponseConsumer.toString()  + " " +
                context.node.getName()));

        HttpResponse httpResponse;


        try {
            httpResponse = client.execute(context.requestProducer, context.asyncResponseConsumer, context.context, null).get();
            PrintUtils.red(String.format("httpResponse.statusLine = %s%n headers = %s",
                    httpResponse.getStatusLine(),
                    Arrays.toString(httpResponse.getAllHeaders())
            ));

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            PrintUtils.cyan(String.format("context.requestProduce = %s%n context.asyncResponseConsumer = %s%n context = %s",
                    context.requestProducer,
                    context.asyncResponseConsumer,
                    context.context ));
            
            httpResponse = client.execute(context.requestProducer, context.asyncResponseConsumer, context.context, null).get();
            PrintUtils.red(String.format("httpResponse.statusLine = %s%n headers = %s",
                    httpResponse.getStatusLine(),
                    Arrays.toString(httpResponse.getAllHeaders())
                   ));

        } catch (Exception e) {
            RequestLogger.logFailedRequest(logger, request.httpRequest, context.node, e);
            onFailure(context.node);
            Exception cause = extractAndWrapCause(e);
            addSuppressedException(previousException, cause);
            if (isRetryableException(e) && tuple.nodes.hasNext()) {
                return performRequest(tuple, request, cause);
            }
            if (cause instanceof IOException) {
                PrintUtils.green("IO Exception: " );
                throw (IOException) cause;
            }
            if (cause instanceof RuntimeException) {
                PrintUtils.green("RuntimeException: ");
                throw (RuntimeException) cause;
            }
            throw new IllegalStateException("unexpected exception type: must be either RuntimeException or IOException", cause);
        }
        ResponseOrResponseException responseOrResponseException = convertResponse(request, context.node, httpResponse);
        if (responseOrResponseException.responseException == null) {
            PrintUtils.red(String.format("httpResponse.statusLine = %s%n headers = %s",
                    httpResponse.getStatusLine(),
                    Arrays.toString(httpResponse.getAllHeaders())
            ));
            return responseOrResponseException.response;
        }
        addSuppressedException(previousException, responseOrResponseException.responseException);
        if (tuple.nodes.hasNext()) {
            return performRequest(tuple, request, responseOrResponseException.responseException);
        }
        throw responseOrResponseException.responseException;
    }

    private ResponseOrResponseException convertResponse(InternalRequest request, Node node, HttpResponse httpResponse) throws IOException {
        RequestLogger.logResponse(logger, request.httpRequest, node.getHost(), httpResponse);
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

        Response response = new Response(request.httpRequest.getRequestLine(), node.getHost(), httpResponse);
        if (isSuccessfulResponse(statusCode) || request.ignoreErrorCodes.contains(response.getStatusLine().getStatusCode())) {
            onResponse(node);
            if (request.warningsHandler.warningsShouldFailRequest(response.getWarnings())) {
                throw new WarningFailureException(response);
            }
            return new ResponseOrResponseException(response);
        }
        ResponseException responseException = new ResponseException(response);
        if (isRetryStatus(statusCode)) {
            // mark host dead and retry against next one
            onFailure(node);
            return new ResponseOrResponseException(responseException);
        }
        // mark host alive and don't retry, as the error should be a request problem
        onResponse(node);
        throw responseException;
    }


    public Cancellable performRequestAsync(Request request, ResponseListener responseListener) {
        try {
            FailureTrackingResponseListener failureTrackingResponseListener = new FailureTrackingResponseListener(responseListener);
            InternalRequest internalRequest = new InternalRequest(request);
            performRequestAsync(nextNodes(), internalRequest, failureTrackingResponseListener);
            return internalRequest.cancellable;
        } catch (Exception e) {
            responseListener.onFailure(e);
            return Cancellable.NO_OP;
        }
    }

    private void performRequestAsync(
            final NodeTuple<Iterator<Node>> tuple,
            final InternalRequest request,
            final FailureTrackingResponseListener listener
    ) {
        request.cancellable.runIfNotCancelled(() -> {
            final RequestContext context = request.createContextForNextAttempt(tuple.nodes.next(), tuple.authCache);
            PrintUtils.green(String.format("       request.createContextForNextAttempt(tuple.nodes.next() = %s    tuple.authCache = %s ) ", tuple.nodes.next(), tuple.authCache));
            PrintUtils.cyan(String.format("        client.execute request producer, async response consumer, context, FutureCallBack<HttpResponse> requestProducer = %s", context.requestProducer));
            client.execute(
                    context.requestProducer,
                    context.asyncResponseConsumer,
                    context.context,
                    new FutureCallback<HttpResponse>() { // A callback interface that gets invoked upon completion of a java.util.concurrent.Future.

                @Override
                public void completed(HttpResponse httpResponse) {
                    try {
                        ResponseOrResponseException responseOrResponseException = convertResponse(request, context.node, httpResponse);
                        if (responseOrResponseException.responseException == null) {
                            listener.onSuccess(responseOrResponseException.response);
                        } else {
                            if (tuple.nodes.hasNext()) {
                                listener.trackFailure(responseOrResponseException.responseException);
                                performRequestAsync(tuple, request, listener);
                            } else {
                                listener.onDefinitiveFailure(responseOrResponseException.responseException);
                            }
                        }
                    } catch (Exception e) {
                        listener.onDefinitiveFailure(e);
                    }
                }

                @Override
                public void failed(Exception failure) {
                    try {
                        RequestLogger.logFailedRequest(logger, request.httpRequest, context.node, failure);
                        onFailure(context.node);
                        if (isRetryableException(failure) && tuple.nodes.hasNext()) {
                            listener.trackFailure(failure);
                            performRequestAsync(tuple, request, listener);
                        } else {
                            listener.onDefinitiveFailure(failure);
                        }
                    } catch (Exception e) {
                        listener.onDefinitiveFailure(e);
                    }
                }
                @Override
                public void cancelled() {
                    listener.onDefinitiveFailure(Cancellable.newCancellationException());
                }
            });
        });
    }


    private NodeTuple<Iterator<Node>> nextNodes() throws IOException {
        NodeTuple<List<Node>> tuple = this.nodeTuple;
        Iterable<Node> hosts = selectNodes(tuple, blacklist, lastNodeIndex, nodeSelector);
        return new NodeTuple<>(hosts.iterator(), tuple.authCache);
    }


    static Iterable<Node> selectNodes(
            NodeTuple<List<Node>> nodeTuple,
            Map<HttpHost, DeadHostState> blacklist,
            AtomicInteger lastNodeIndex,
            NodeSelector nodeSelector
    ) throws IOException {
        /*
         * Sort the nodes into living and dead lists.
         */
        List<Node> livingNodes = new ArrayList<>(Math.max(0, nodeTuple.nodes.size() - blacklist.size()));
        List<DeadNode> deadNodes = new ArrayList<>(blacklist.size());
        for (Node node : nodeTuple.nodes) {
            DeadHostState deadness = blacklist.get(node.getHost());
            if (deadness == null || deadness.shallBeRetried()) {
                livingNodes.add(node);
            } else {
                deadNodes.add(new DeadNode(node, deadness));
            }
        }

        if (!livingNodes.isEmpty()) {
            /*
             * Normal state: there is at least one living node. If the
             * selector is ok with any over the living nodes then use them
             * for the request.
             */
            List<Node> selectedLivingNodes = new ArrayList<>(livingNodes);
            nodeSelector.select(selectedLivingNodes);
            if (!selectedLivingNodes.isEmpty()) {
                /*
                 * Rotate the list using a global counter as the distance so subsequent
                 * requests will try the nodes in a different order.
                 */
                Collections.rotate(selectedLivingNodes, lastNodeIndex.getAndIncrement());
                return selectedLivingNodes;
            }
        }

        /*
         * Last resort: there are no good nodes to use, either because
         * the selector rejected all the living nodes or because there aren't
         * any living ones. Either way, we want to revive a single dead node
         * that the NodeSelectors are OK with. We do this by passing the dead
         * nodes through the NodeSelector so it can have its say in which nodes
         * are ok. If the selector is ok with any of the nodes then we will take
         * the one in the list that has the lowest revival time and try it.
         */
        if (!deadNodes.isEmpty()) {
            final List<DeadNode> selectedDeadNodes = new ArrayList<>(deadNodes);

            nodeSelector.select(() -> new DeadNodeIteratorAdapter(selectedDeadNodes.iterator()));
            if (false == selectedDeadNodes.isEmpty()) {
                return singletonList(Collections.min(selectedDeadNodes).node);
            }
        }
        throw new IOException(
                "NodeSelector [" + nodeSelector + "] rejected all nodes, " + "living " + livingNodes + " and dead " + deadNodes
        );
    }


    private void onResponse(Node node) {
        DeadHostState removedHost = this.blacklist.remove(node.getHost());
        if (logger.isDebugEnabled() && removedHost != null) {
            logger.debug("removed [" + node + "] from blacklist");
        }
    }

    /**
     * Called after each failed attempt.
     * Receives as an argument the host that was used for the failed attempt.
     */
    private void onFailure(Node node) {
        while (true) {
            DeadHostState previousDeadHostState = blacklist.putIfAbsent(
                    node.getHost(),
                    new DeadHostState(DeadHostState.DEFAULT_TIME_SUPPLIER)
            );
            if (previousDeadHostState == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("added [" + node + "] to blacklist");
                }
                break;
            }
            if (blacklist.replace(node.getHost(), previousDeadHostState, new DeadHostState(previousDeadHostState))) {
                if (logger.isDebugEnabled()) {
                    logger.debug("updated [" + node + "] already in blacklist");
                }
                break;
            }
        }
        failureListener.onFailure(node);
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    private static boolean isSuccessfulResponse(int statusCode) {
        return statusCode < 300;
    }

    /**
     * Should an exception cause retrying the request?
     */
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
                    entity = new ContentCompressingEntity(entity);
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
        RequestLogger.logResponse(logger, pathPrefix, path, params);

        PrintUtils.green(String.format("URI buildUri(String pathPrefix %s%n, String path %s%n, Map<String, String> params %s%n", pathPrefix, path,params ));
        try {
            String fullPath;
            if (pathPrefix != null && !pathPrefix.isEmpty()) {
                if (pathPrefix.endsWith("/") && path.startsWith("/")) {
                    PrintUtils.green(String.format("pathPrefix.endsWith(\"/\") && path.startsWith(\"/\"): %s%s ", pathPrefix, path));
                    fullPath = pathPrefix.substring(0, pathPrefix.length() - 1) + path; // remove additional "/"
                } else if (pathPrefix.endsWith("/") || path.startsWith("/")) {
                    fullPath = pathPrefix + path;
                    PrintUtils.green(String.format("(pathPrefix.endsWith(\"/\") || path.startsWith(\"/\"): %s ", fullPath));
                } else {
                    fullPath = pathPrefix + "/" + path;
                    PrintUtils.green(String.format("pathPrefix + \"/\" + path %s ", fullPath));
                }
            } else {
                fullPath = path;
                PrintUtils.green(String.format("fullPath = path: %s ", fullPath));
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


    static class FailureTrackingResponseListener {
        private final ResponseListener responseListener;
        private volatile Exception exception;

        FailureTrackingResponseListener(ResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        /**
         * Notifies the caller of a response through the wrapped listener
         */
        void onSuccess(Response response) {
            responseListener.onSuccess(response);
        }

        /**
         * Tracks one last definitive failure and returns to the caller by notifying the wrapped listener
         */
        void onDefinitiveFailure(Exception e) {
            trackFailure(e);
            responseListener.onFailure(this.exception);
        }

        /**
         * Tracks an exception, which caused a retry hence we should not return yet to the caller
         */
        void trackFailure(Exception e) {
            addSuppressedException(this.exception, e);
            this.exception = e;
        }
    }


    public static class FailureListener {
        /**
         * Notifies that the node provided as argument has just failed
         */
        public void onFailure(Node node) {
        }
    }


    static class NodeTuple<T> {
        final T nodes;
        final AuthCache authCache;

        NodeTuple(final T nodes, final AuthCache authCache) {
            this.nodes = nodes;
            this.authCache = authCache;
        }
    }


    private static class DeadNode implements Comparable<RestClient.DeadNode> {
        final Node node;
        final DeadHostState deadness;

        DeadNode(Node node, DeadHostState deadness) {
            this.node = node;
            this.deadness = deadness;
        }

        @Override
        public String toString() {
            return node.toString();
        }

        @Override
        public int compareTo(RestClient.DeadNode rhs) {
            return deadness.compareTo(rhs.deadness);
        }
    }


    private static class DeadNodeIteratorAdapter implements Iterator<Node> {
        private final Iterator<RestClient.DeadNode> itr;

        private DeadNodeIteratorAdapter(Iterator<RestClient.DeadNode> itr) {
            this.itr = itr;
        }

        @Override
        public boolean hasNext() {
            return itr.hasNext();
        }

        @Override
        public Node next() {
            return itr.next().node;
        }

        @Override
        public void remove() {
            itr.remove();
        }
    }

    private class InternalRequest {
        private final Request request;
        private final Set<Integer> ignoreErrorCodes;
        private final HttpRequestBase httpRequest;
        private final Cancellable cancellable;
        private final WarningsHandler warningsHandler;

        InternalRequest(Request request) {
            this.request = request;
            Map<String, String> params = new HashMap<>(request.getParameters());
            params.putAll(request.getOptions().getParameters());
            // ignore is a special parameter supported by the clients, shouldn't be sent to es
            String ignoreString = params.remove("ignore");
            this.ignoreErrorCodes = getIgnoreErrorCodes(ignoreString, request.getMethod());

            URI uri = buildUri(pathPrefix, request.getEndpoint(), params);
            PrintUtils.green(String.format("uri =  %s", uri));

            this.httpRequest = createHttpRequest(request.getMethod(), uri, request.getEntity(), compressionEnabled);
            PrintUtils.green(String.format("httpRequest = %s", httpRequest));

            this.cancellable = Cancellable.fromRequest(httpRequest);
            PrintUtils.red(String.format("this.cancellable = Cancellable.fromRequest(httpRequest) %s", cancellable));

            setHeaders(httpRequest, request.getOptions().getHeaders());
            PrintUtils.green(String.format("setHeaders(httpRequest, request.getOptions().getHeaders()); %s", httpRequest));

            setRequestConfig(httpRequest, request.getOptions().getRequestConfig());
            PrintUtils.cyan(String.format(" setRequestConfig(httpRequest, request.getOptions().getRequestConfig()) = %s", request.getOptions().getRequestConfig()));

            this.warningsHandler = request.getOptions().getWarningsHandler() == null
                    ? RestClient.this.warningsHandler
                    : request.getOptions().getWarningsHandler();
        }

        private void setHeaders(HttpRequest req, Collection<Header> requestHeaders) {
            // request headers override default headers, so we don't add default headers if they exist as request headers
            final Set<String> requestNames = new HashSet<>(requestHeaders.size());
            for (Header requestHeader : requestHeaders) {
                req.addHeader(requestHeader);
                requestNames.add(requestHeader.getName());
                PrintUtils.red(String.format("HttpRequest addHeader: %s%n requestNames .add: %s", requestHeader, requestHeader.getName()));
            }
            for (Header defaultHeader : defaultHeaders) {
                if (!requestNames.contains(defaultHeader.getName())) {
                    req.addHeader(defaultHeader);
                    PrintUtils.red(String.format("HttpRequest adding defaultHeader: %s%n ", defaultHeader));
                }
            }
            if (compressionEnabled) {
                req.addHeader("Accept-Encoding", "gzip");
                PrintUtils.red("compressionEnabled addHeader : %s%n, AcceptEncoding gzip");
            }
            if (metaHeaderEnabled) {
                if (!req.containsHeader(RestClientBuilder.META_HEADER_NAME)) {
                    req.setHeader(RestClientBuilder.META_HEADER_NAME, RestClientBuilder.META_HEADER_VALUE);
                    PrintUtils.red("metaHeaderEnabled addHeader : RestClientBuilder.META_HEADER_NAME");
                }
            } else {
                req.removeHeaders(RestClientBuilder.META_HEADER_NAME);
            }
        }

        private void setRequestConfig(HttpRequestBase requestBase, RequestConfig requestConfig) {
            if (requestConfig != null) {
                requestBase.setConfig(requestConfig);
            }
        }

        RestClient.RequestContext createContextForNextAttempt(Node node, AuthCache authCache) {
            this.httpRequest.reset();
            try {
                return new RequestContext(this, node, authCache);
            } catch (HttpException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class RequestContext {
        private final Node node;
        private final HttpAsyncRequestProducer requestProducer;
        private final HttpAsyncResponseConsumer<HttpResponse> asyncResponseConsumer;

        // Adaptor class that provides convenience type safe setters and getters for common HttpContext attributes used in the course of HTTP request execution.
        private final HttpClientContext context;

        RequestContext(RestClient.InternalRequest request, Node node, AuthCache authCache) throws HttpException {
            this.node = node;
            PrintUtils.red(String.format("node = %s", node));
            // we stream the request body if the entity allows for it
            this.requestProducer = HttpAsyncMethods.create(node.getHost(), request.httpRequest);
            PrintUtils.red(String.format("requestProducer = HttpAsyncMethods.create(node.getHost = %s%n request.httpRquest = %s", node.getHost(), request.httpRequest));
            this.asyncResponseConsumer = request.request.getOptions()
                    .getHttpAsyncResponseConsumerFactory()
                    .createHttpAsyncResponseConsumer();
            PrintUtils.red(String.format("requestProducer = HttpAsyncMethods.create(node.getHost = %s%n request.httpRquest = %s", node.getHost(), request.httpRequest));

            PrintUtils.green(String.format("       asyncResponseConsumer = request.request.getOptions().getHttpAsyncResponseConsumerFactory().createHttpAsyncResponseConsumer() %n%s, request.httpRequest = %s) ", node.getHost(), request.httpRequest));
            this.context = HttpClientContext.create();

            try {
                PrintUtils.red(String.format("this.requestProducer.generateRequest().getRequestLine = %s", this.requestProducer.generateRequest().getRequestLine()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    public static class ContentCompressingEntity extends GzipCompressingEntity {

        public ContentCompressingEntity(HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException {
            RestClient.ByteArrayInputOutputStream out = new RestClient.ByteArrayInputOutputStream(1024);
            try (GZIPOutputStream gzipOut = new GZIPOutputStream(out)) {
                wrappedEntity.writeTo(gzipOut);
            }
            return out.asInput();
        }
    }


    private static class ByteArrayInputOutputStream extends ByteArrayOutputStream {
        ByteArrayInputOutputStream(int size) {
            super(size);
        }

        public InputStream asInput() {
            return new ByteArrayInputStream(this.buf, 0, this.count);
        }
    }
}
