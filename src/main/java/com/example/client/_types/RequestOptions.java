package com.example.client._types;


import java.util.*;
import java.util.concurrent.*;

public final class RequestOptions {
    public static final RequestOptions DEFAULT = new Builder(
            Collections.emptyMap(),
            Collections.emptyMap(),
            Collections.emptyMap()
    ).build();

    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final Map<String, String> requestConfig;

    private RequestOptions(Builder builder) {
        this.headers = Collections.unmodifiableMap((builder.headers));
        this.parameters = Collections.unmodifiableMap(builder.parameters);
        this.requestConfig = builder.requestConfig;
    }
    public Builder toBuilder() {
        return new Builder(headers, parameters, requestConfig);
    }

    public Map<String, String> getHeaders() {
        if(headers != null) {
            return headers;
        }
        else {
            try {
                throw new Exception("headers null");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getRequestConfig() {
        return requestConfig;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("RequestOptions{");
        boolean comma = false;
        if (headers.size() > 0) {
            b.append("headers=");
            comma = true;
            for (int h = 0; h < headers.size(); h++) {
                if (h != 0) {
                    b.append(',');
                }
                b.append(headers.get(h).toString());
            }
        }return b.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (obj.getClass() != getClass())) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        RequestOptions other = (RequestOptions) obj;
        return headers.equals(other.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    public static class Builder {
        private final Map<String, String> headers;
        private final Map<String, String> parameters;
        private Map<String, String> requestConfig;

        private Builder(
                Map<String, String> headers,
                Map<String, String> parameters,
                Map<String, String> requestConfig
        ) {
            this.headers = new LinkedHashMap<>(headers);
            this.parameters = new ConcurrentHashMap<>(parameters);
            this.requestConfig = new HashMap<>(requestConfig);
        }

        public RequestOptions build() {
            return new RequestOptions(this);
        }

        public Builder addHeader(String name, String value) {
            Objects.requireNonNull(name, "header name cannot be null");
            Objects.requireNonNull(value, "header value cannot be null");
            headers.put(name, value);
            return this;
        }

        public Map<String, String> getHeaders() {
            return this.headers;
        }

        public Builder addParameter(String key, String value) {
            Objects.requireNonNull(key, "parameter key cannot be null");
            Objects.requireNonNull(value, "parameter value cannot be null");
            this.parameters.merge(key, value, (existingValue, newValue) -> String.join(",", existingValue, newValue));
            return this;
        }
        
    }
    
}
