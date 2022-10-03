package com.example.client._types;

import co.elastic.clients.json.*;
import com.example.client.transport.*;
import com.example.client.util.*;

import java.io.*;
import java.net.*;
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
                sb.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // No endpoint, ignore
        } catch (UnsupportedEncodingException e) {
            // Should not happen (UTF-8 is builtin)
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