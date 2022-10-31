package org.example.elasticsearch.client._types;


import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.transport.*;
import org.example.elasticsearch.client.util.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

public abstract class RequestBase {
    public RequestBase() {
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append(": ");

        try {
            @SuppressWarnings("unchecked")
            Endpoint<RequestBase, ?, ?> endpoint = (Endpoint<RequestBase, ?, ?>) this.getClass()
                    .getDeclaredField("_ENDPOINT").get(null);

            sb.append(endpoint.method(this)).append(" ").append(endpoint.requestUrl(this));

            Map<String, String> params = endpoint.queryParameters(this);
            String delim = "?";
            for (Map.Entry<String, String> param : params.entrySet()) {
                sb.append(delim);
                delim = "&";
                sb.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // No endpoint, ignore
        }

        if (this instanceof JsonpSerializable) {
            sb.append(' ');
            JsonpUtils.toString((JsonpSerializable) this, sb);
        }

        return sb.toString();
    }

    protected abstract static class AbstractBuilder<BuilderT extends AbstractBuilder<BuilderT>>
            extends
            WithJsonObjectBuilderBase<BuilderT> {
        protected abstract BuilderT self();

    }

}