package org.example.elasticsearch.client.transport.restclient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.example.elasticsearch.client.transport.endpoints.*;

import java.io.*;

class HttpClientBinaryResponse implements BinaryResponse {
    private final HttpEntity entity;
    private boolean consumed = false;

    HttpClientBinaryResponse(HttpEntity entity) {
        this.entity = entity;
    }

    @Override
    public String contentType() {
        Header h = entity.getContentType();
        return h == null ? "application/octet-stream" : h.getValue();
    }

    @Override
    public long contentLength() {
        long len = entity.getContentLength();
        return len < 0 ? -1 : entity.getContentLength();
    }

    @Override
    public InputStream content() throws IOException {
        if (consumed) {
            throw new IllegalStateException("Response content has already been consumed");
        }
        consumed = true;
        return entity.getContent();
    }

    @Override
    public void close() throws IOException {
        consumed = true;
        EntityUtils.consume(entity);
    }
}
