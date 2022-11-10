package org.example.elasticsearch.client;

import org.example.elasticsearch.client.json.JsonpMapper;

import org.example.elasticsearch.client.transport.Transport;
import org.example.elasticsearch.client.transport.TransportOptions;

import org.example.elasticsearch.client.json.JsonpDeserializer;
import org.example.elasticsearch.client.json.JsonpMapperBase;


import javax.annotation.Nullable;
import java.lang.reflect.Type;

import java.util.function.*;

public abstract class ApiClient<T extends Transport, Self extends ApiClient<T, Self>> {

    protected final T transport;
    protected final TransportOptions transportOptions;

    protected ApiClient(T transport, TransportOptions transportOptions) {
        this.transport = transport;
        this.transportOptions = transportOptions;
    }

    protected <V> JsonpDeserializer<V> getDeserializer(Type type) {
        // Try the built-in deserializers first to avoid repeated lookups in the Jsonp mapper for client-defined classes
        JsonpDeserializer<V> result = JsonpMapperBase.findDeserializer(type);
        if (result != null) {
            return result;
        }

        return JsonpDeserializer.of(type);
    }


    // Creates a new client with some request options

    public abstract Self withTransportOptions(@Nullable TransportOptions transportOptions);

    // Creates a new client with additional request options

    // @param fn a lambda expression that takes the current options as input

    public Self withTransportOptions(Function<TransportOptions.Builder, TransportOptions.Builder> fn) {
        return withTransportOptions(fn.apply(_transportOptions().toBuilder()).build());
    }

    // Get the transport used by this client to handle communication with the server.

    public T _transport() {
        return this.transport;
    }


    //  Get the transport options used for this client. If the client has no custom options, falls back to the transport's options.

    public TransportOptions _transportOptions() {
        return this.transportOptions == null ? transport.options() : transportOptions;
    }

    // Get the JSON mapper used to map API objects to/from JSON.
    // Shortcut for _transport().jsonpMapper()
    public JsonpMapper _jsonpMapper() {
        return transport.jsonpMapper();
    }
}