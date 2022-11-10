package org.example.elasticsearch.client.transport.endpoints;

import java.io.*;

public interface BinaryResponse extends AutoCloseable {

    String contentType();


    long contentLength();

    InputStream content() throws IOException;

    @Override
    void close() throws IOException;
}