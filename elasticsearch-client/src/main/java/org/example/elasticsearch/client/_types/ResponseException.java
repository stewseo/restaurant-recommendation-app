package org.example.elasticsearch.client._types;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.util.*;
import org.example.lowlevel.restclient.*;

import java.io.*;
import java.util.*;

public final class ResponseException extends IOException {

    private final Response response;

    public ResponseException(Response response) throws IOException {
        super(buildMessage(response));
        this.response = response;
    }

    public static String buildMessage(Response response) throws IOException {
        String message = String.format(
                Locale.ROOT,
                "method [%s], host [%s], URI [%s], status line [%s]",
                response.getRequestLine().getMethod(),
                response.getHost(),
                response.getRequestLine().getUri(),
                response.getStatusLine().toString()
        );

        if (response.hasWarnings()) {
            message += "\n" + "Warnings: " + response.getWarnings();
        }

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            if (entity.isRepeatable() == false) {
                entity = new BufferedHttpEntity(entity);
                response.getHttpResponse().setEntity(entity);
            }
            message += "\n" + EntityUtils.toString(entity);
        }
        return message;
    }

    public Response getResponse() {
        return response;
    }


}
