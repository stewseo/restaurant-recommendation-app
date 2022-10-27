package org.example.lowlevel.restclient;

import org.apache.http.*;
import org.apache.http.nio.protocol.*;

import static org.example.lowlevel.restclient.HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory.*;


public interface HttpAsyncResponseConsumerFactory {


    HttpAsyncResponseConsumerFactory DEFAULT = new HeapBufferedResponseConsumerFactory(DEFAULT_BUFFER_LIMIT);

    HttpAsyncResponseConsumer<HttpResponse> createHttpAsyncResponseConsumer();


    class HeapBufferedResponseConsumerFactory implements HttpAsyncResponseConsumerFactory {

        // default buffer limit is 100MB
        static final int DEFAULT_BUFFER_LIMIT = 100 * 1024 * 1024;

        private final int bufferLimit;

        public HeapBufferedResponseConsumerFactory(int bufferLimitBytes) {
            this.bufferLimit = bufferLimitBytes;
        }

        @Override
        public HttpAsyncResponseConsumer<HttpResponse> createHttpAsyncResponseConsumer() {
            return new HeapBufferedAsyncResponseConsumer(bufferLimit);
        }
    }
}

