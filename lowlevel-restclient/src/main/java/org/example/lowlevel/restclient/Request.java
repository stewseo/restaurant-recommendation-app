package org.example.lowlevel.restclient;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.nio.entity.*;

import java.util.*;

import static java.util.Collections.*;

public final class Request {
    private final String method;
    private final String endpoint;
    private final Map<String, String> parameters = new HashMap<>();

    private HttpEntity entity;
    private RequestOptions options = RequestOptions.DEFAULT;

    public Request(String method, String endpoint) {
        this.method = Objects.requireNonNull(method, "method cannot be null");
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint cannot be null");
    }


    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void addParameter(String name, String value) {
        Objects.requireNonNull(name, "url parameter name cannot be null");
        if (parameters.containsKey(name)) {
            throw new IllegalArgumentException("url parameter [" + name + "] has already been set to [" + parameters.get(name) + "]");
        } else {
            parameters.put(name, value);
        }
    }

    public void addParameters(Map<String, String> paramSource) {
        paramSource.forEach(this::addParameter);
    }


    public Map<String, String> getParameters() {
        return unmodifiableMap(parameters);
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }


    public void setJsonEntity(String body) {
        setEntity(body == null ? null : new NStringEntity(body, ContentType.APPLICATION_JSON));
    }

    public HttpEntity getEntity() {
        return entity;
    }


    public void setOptions(RequestOptions options) {
        Objects.requireNonNull(options, "options cannot be null");
        this.options = options;
    }


    public void setOptions(RequestOptions.Builder options) {
        Objects.requireNonNull(options, "options cannot be null");
        this.options = options.build();
    }

    public RequestOptions getOptions() {
        return options;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Request{");
        b.append("method='").append(method).append('\'');
        b.append(", endpoint='").append(endpoint).append('\'');
        if (false == parameters.isEmpty()) {
            b.append(", params=").append(parameters);
        }
        if (entity != null) {
            b.append(", entity=").append(entity);
        }
        b.append(", options=").append(options);
        return b.append('}').toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (obj.getClass() != getClass())) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Request other = (Request) obj;
        return method.equals(other.method)
                && endpoint.equals(other.endpoint)
                && parameters.equals(other.parameters)
                && Objects.equals(entity, other.entity)
                && options.equals(other.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, endpoint, parameters, entity, options);
    }

}