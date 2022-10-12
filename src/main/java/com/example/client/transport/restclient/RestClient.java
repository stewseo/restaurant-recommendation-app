///*
// * Licensed to Elasticsearch B.V. under one or more contributor
// * license agreements. See the NOTICE file distributed with
// * this work for additional information regarding copyright
// * ownership. Elasticsearch B.V. licenses this file to you under
// * the Apache License, Version 2.0 (the "License"); you may
// * not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//package com.example.client.transport.restclient;
//
//import org.apache.commons.logging.*;
//import org.apache.http.*;
//import org.apache.http.client.*;
//import org.apache.http.client.entity.*;
//import org.apache.http.client.methods.*;
//import org.apache.http.client.protocol.*;
//import org.apache.http.client.utils.*;
//import org.apache.http.conn.*;
//import org.apache.http.impl.client.*;
//import org.apache.http.impl.nio.client.*;
//import org.apache.http.nio.client.*;
//import org.apache.http.nio.client.methods.*;
//import org.apache.http.nio.protocol.*;
//import org.apache.http.protocol.*;
//import org.apache.http.util.*;
//import org.elasticsearch.client.Node;
//import org.elasticsearch.client.*;
//
//import javax.net.ssl.*;
//import java.io.*;
//import java.net.*;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.*;
//import java.util.stream.*;
//import java.util.zip.*;
//
//import static java.nio.charset.StandardCharsets.*;
//
//
//public class RestClient implements Closeable {
//    private static final Log logger = LogFactory.getLog(RestClient.class);
//
//    private final CloseableHttpAsyncClient client;
//    // We don't rely on default headers supported by HttpAsyncClient as those cannot be replaced.
//    // These are package private for tests.
//    final List<Header> defaultHeaders;
//    private final String pathPrefix;
//
//    private final AtomicInteger lastNodeIndex = new AtomicInteger(0);
//    private final NodeSelector nodeSelector;
//
//    private final WarningsHandler warningsHandler;
//    private final boolean compressionEnabled;
//    private final boolean metaHeaderEnabled;
//
//    RestClient(
//            CloseableHttpAsyncClient client,
//            Header[] defaultHeaders,
//            List<Node> nodes,
//            String pathPrefix,
//            NodeSelector nodeSelector,
//            boolean strictDeprecationMode,
//            boolean compressionEnabled,
//            boolean metaHeaderEnabled
//    ) {
//        this.client = client;
//        this.defaultHeaders = Collections.unmodifiableList(Arrays.asList(defaultHeaders));
//        this.pathPrefix = pathPrefix;
//        this.nodeSelector = nodeSelector;
//        this.warningsHandler = strictDeprecationMode ? WarningsHandler.STRICT : WarningsHandler.PERMISSIVE;
//        this.compressionEnabled = compressionEnabled;
//        this.metaHeaderEnabled = metaHeaderEnabled;
//    }
//
//
//    public static RestClientBuilder builder(String cloudId) {
//        // there is an optional first portion of the cloudId that is a human readable string, but it is not used.
//        if (cloudId.contains(":")) {
//            if (cloudId.indexOf(":") == cloudId.length() - 1) {
//                throw new IllegalStateException("cloudId " + cloudId + " must begin with a human readable identifier followed by a colon");
//            }
//            cloudId = cloudId.substring(cloudId.indexOf(":") + 1);
//        }
//
//        String decoded = new String(Base64.getDecoder().decode(cloudId), UTF_8);
//        // once decoded the parts are separated by a $ character.
//        // they are respectively domain name and optional port, elasticsearch id, kibana id
//        String[] decodedParts = decoded.split("\\$");
//        if (decodedParts.length != 3) {
//            throw new IllegalStateException("cloudId " + cloudId + " did not decode to a cluster identifier correctly");
//        }
//
//        // domain name and optional port
//        String[] domainAndMaybePort = decodedParts[0].split(":", 2);
//        String domain = domainAndMaybePort[0];
//        int port;
//
//        if (domainAndMaybePort.length == 2) {
//            try {
//                port = Integer.parseInt(domainAndMaybePort[1]);
//            } catch (NumberFormatException nfe) {
//                throw new IllegalStateException("cloudId " + cloudId + " does not contain a valid port number");
//            }
//        } else {
//            port = 443;
//        }
//
//        String url = decodedParts[1] + "." + domain;
//        return builder(new HttpHost(url, port, "https"));
//    }
//
//
//    public static RestClientBuilder builder(HttpHost... hosts) {
//        if (hosts == null || hosts.length == 0) {
//            throw new IllegalArgumentException("hosts must not be null nor empty");
//        }
//        List<Node> nodes = Arrays.stream(hosts).map(Node::new).collect(Collectors.toList());
//        return new RestClientBuilder(nodes);
//    }
//
//    public HttpAsyncClient getHttpClient() {
//        return this.client;
//    }
//
//    public Response performRequest(com.example.client._types.Request request) throws IOException {
//        return performRequest(request, null);
//    }
//
//    private Response performRequest(com.example.client._types.Request request, Exception previousException)
//            throws IOException {
//        HttpResponse httpResponse;
//
//        try {
//
//            httpResponse = client.execute(context.requestProducer, context.asyncResponseConsumer, context.context, null).get();
//        } catch (Exception e) {
//
//            throw new IllegalStateException("unexpected exception type: must be either RuntimeException or IOException");
//        }
//
//        ResponseOrResponseException responseOrResponseException = convertResponse(request, httpResponse);
//        if (responseOrResponseException.responseException == null) {
//            return responseOrResponseException.response;
//        }
//        throw responseOrResponseException.responseException;
//    }
//
//
//    private static Set<Integer> getIgnoreErrorCodes(String ignoreString, String requestMethod) {
//        Set<Integer> ignoreErrorCodes;
//        if (ignoreString == null) {
//            if (HttpHead.METHOD_NAME.equals(requestMethod)) {
//                // 404 never causes error if returned for a HEAD request
//                ignoreErrorCodes = Collections.singleton(404);
//            } else {
//                ignoreErrorCodes = Collections.emptySet();
//            }
//        } else {
//            String[] ignoresArray = ignoreString.split(",");
//            ignoreErrorCodes = new HashSet<>();
//            if (HttpHead.METHOD_NAME.equals(requestMethod)) {
//                // 404 never causes error if returned for a HEAD request
//                ignoreErrorCodes.add(404);
//            }
//            for (String ignoreCode : ignoresArray) {
//                try {
//                    ignoreErrorCodes.add(Integer.valueOf(ignoreCode));
//                } catch (NumberFormatException e) {
//                    throw new IllegalArgumentException("ignore value should be a number, found [" + ignoreString + "] instead", e);
//                }
//            }
//        }
//        return ignoreErrorCodes;
//    }
//
//
//    private ResponseOrResponseException convertResponse(Request request, HttpResponse httpResponse) throws IOException {
//        int statusCode = httpResponse.getStatusLine().getStatusCode();
//
//        HttpEntity entity = httpResponse.getEntity();
//        if (entity != null) {
//            Header header = entity.getContentEncoding();
//            if (header != null && "gzip".equals(header.getValue())) {
//                // Decompress and cleanup response headers
//                httpResponse.setEntity(new GzipDecompressingEntity(entity));
//                httpResponse.removeHeaders(HTTP.CONTENT_ENCODING);
//                httpResponse.removeHeaders(HTTP.CONTENT_LEN);
//            }
//        }
//
//        com.example.client._types.Response response = new com.example.client._types.Response(request.httpRequest.getRequestLine(), node.getHost(), httpResponse);
//        if (isSuccessfulResponse(statusCode) || request.ignoreErrorCodes.contains(response.getStatusLine().getStatusCode())) {
//            onResponse(node);
//            if (request.warningsHandler.warningsShouldFailRequest(response.getWarnings())) {
//                throw new WarningFailureException(response);
//            }
//            return new ResponseOrResponseException(response);
//        }
//        ResponseException responseException = new ResponseException(response);
//        if (isRetryStatus(statusCode)) {
//            // mark host dead and retry against next one
//            onFailure(node);
//            return new ResponseOrResponseException(responseException);
//        }
//        // mark host alive and don't retry, as the error should be a request problem
//        onResponse(node);
//        throw responseException;
//    }
//
//
//
//    private static HttpRequestBase addRequestBody(HttpRequestBase httpRequest, HttpEntity entity, boolean compressionEnabled) {
//        if (entity != null) {
//            if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
//                if (compressionEnabled) {
//                    entity = new ContentCompressingEntity(entity);
//                }
//                ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(entity);
//            } else {
//                throw new UnsupportedOperationException(httpRequest.getMethod() + " with body is not supported");
//            }
//        }
//        return httpRequest;
//    }
//
//    static URI buildUri(String pathPrefix, String path, Map<String, String> params) {
//        Objects.requireNonNull(path, "path must not be null");
//        try {
//            String fullPath;
//            if (pathPrefix != null && pathPrefix.isEmpty() == false) {
//                if (pathPrefix.endsWith("/") && path.startsWith("/")) {
//                    fullPath = pathPrefix.substring(0, pathPrefix.length() - 1) + path;
//                } else if (pathPrefix.endsWith("/") || path.startsWith("/")) {
//                    fullPath = pathPrefix + path;
//                } else {
//                    fullPath = pathPrefix + "/" + path;
//                }
//            } else {
//                fullPath = path;
//            }
//
//            URIBuilder uriBuilder = new URIBuilder(fullPath);
//            for (Map.Entry<String, String> param : params.entrySet()) {
//                uriBuilder.addParameter(param.getKey(), param.getValue());
//            }
//            return uriBuilder.build();
//        } catch (URISyntaxException e) {
//            throw new IllegalArgumentException(e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public void close() throws IOException {
//
//    }
//
//
//    private static class ResponseOrResponseException {
//        private final Response response;
//        private final ResponseException responseException;
//
//        ResponseOrResponseException(Response response) {
//            this.response = Objects.requireNonNull(response);
//            this.responseException = null;
//        }
//
//        ResponseOrResponseException(ResponseException responseException) {
//            this.responseException = Objects.requireNonNull(responseException);
//            this.response = null;
//        }
//    }
//
//
//    private static Exception extractAndWrapCause(Exception exception) {
//        if (exception instanceof InterruptedException) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException("thread waiting for the response was interrupted", exception);
//        }
//        if (exception instanceof ExecutionException) {
//            ExecutionException executionException = (ExecutionException) exception;
//            Throwable t = executionException.getCause() == null ? executionException : executionException.getCause();
//            if (t instanceof Error) {
//                throw (Error) t;
//            }
//            exception = (Exception) t;
//        }
//        if (exception instanceof ConnectTimeoutException) {
//            ConnectTimeoutException e = new ConnectTimeoutException(exception.getMessage());
//            e.initCause(exception);
//            return e;
//        }
//        if (exception instanceof SocketTimeoutException) {
//            SocketTimeoutException e = new SocketTimeoutException(exception.getMessage());
//            e.initCause(exception);
//            return e;
//        }
//        if (exception instanceof ConnectionClosedException) {
//            ConnectionClosedException e = new ConnectionClosedException(exception.getMessage());
//            e.initCause(exception);
//            return e;
//        }
//        if (exception instanceof SSLHandshakeException) {
//            SSLHandshakeException e = new SSLHandshakeException(exception.getMessage());
//            e.initCause(exception);
//            return e;
//        }
//        if (exception instanceof ConnectException) {
//            ConnectException e = new ConnectException(exception.getMessage());
//            e.initCause(exception);
//            return e;
//        }
//        if (exception instanceof IOException) {
//            return new IOException(exception.getMessage(), exception);
//        }
//        if (exception instanceof RuntimeException) {
//            return new RuntimeException(exception.getMessage(), exception);
//        }
//        return new RuntimeException("error while performing request", exception);
//    }
//
//
//    public static class ContentCompressingEntity extends GzipCompressingEntity {
//
//        public ContentCompressingEntity(HttpEntity entity) {
//            super(entity);
//        }
//
//        @Override
//        public InputStream getContent() throws IOException {
//            ByteArrayInputOutputStream out = new ByteArrayInputOutputStream(1024);
//            try (GZIPOutputStream gzipOut = new GZIPOutputStream(out)) {
//                wrappedEntity.writeTo(gzipOut);
//            }
//            return out.asInput();
//        }
//    }
//
//    private static class ByteArrayInputOutputStream extends ByteArrayOutputStream {
//        ByteArrayInputOutputStream(int size) {
//            super(size);
//        }
//
//        public InputStream asInput() {
//            return new ByteArrayInputStream(this.buf, 0, this.count);
//        }
//    }
//}
