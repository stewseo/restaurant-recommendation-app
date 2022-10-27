package org.example.lowlevel.restclient;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.nio.*;
import org.apache.http.nio.entity.*;
import org.apache.http.nio.protocol.*;
import org.apache.http.nio.util.*;
import org.apache.http.protocol.*;

import java.io.*;

public class HeapBufferedAsyncResponseConsumer extends AbstractAsyncResponseConsumer<HttpResponse> {

    private final int bufferLimitBytes;
    private volatile HttpResponse response;
    private volatile SimpleInputBuffer buf;


    public HeapBufferedAsyncResponseConsumer(int bufferLimit) {
        if (bufferLimit <= 0) {
            throw new IllegalArgumentException("bufferLimit must be greater than 0");
        }
        this.bufferLimitBytes = bufferLimit;
    }


    public int getBufferLimit() {
        return bufferLimitBytes;
    }

    @Override
    protected void onResponseReceived(HttpResponse httpResponse) throws HttpException, IOException {
        this.response = httpResponse;
    }

    @Override
    protected void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException {
        long len = entity.getContentLength();
        if (len > bufferLimitBytes) {
            throw new ContentTooLongException(
                    "entity content is too long [" + len + "] for the configured buffer limit [" + bufferLimitBytes + "]"
            );
        }
        if (len < 0) {
            len = 4096;
        }
        this.buf = new SimpleInputBuffer((int) len, getByteBufferAllocator());
        this.response.setEntity(new ContentBufferEntity(entity, this.buf));
    }

    protected ByteBufferAllocator getByteBufferAllocator() {
        return HeapByteBufferAllocator.INSTANCE;
    }

    @Override
    protected void onContentReceived(ContentDecoder decoder, IOControl ioctrl) throws IOException {
        this.buf.consumeContent(decoder);
    }

    @Override
    protected HttpResponse buildResult(HttpContext context) throws Exception {
        return response;
    }

    @Override
    protected void releaseResources() {
        response = null;
    }
}
