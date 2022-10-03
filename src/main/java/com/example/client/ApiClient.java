package com.example.client;

import co.elastic.clients.transport.*;
import com.example.client.transport.*;
import com.example.client.transport.Transport;
import com.example.client.transport.TransportOptions;

public abstract class ApiClient<T extends Transport, Self extends ApiClient<T, Self>> {

    protected final T transport;
    protected final TransportOptions transportOptions;

    protected ApiClient(T transport, TransportOptions transportOptions) {
        this.transport = transport;
        this.transportOptions = transportOptions;
    }

    public abstract Self withTransportOptions(TransportOptions transportOptions);

    public T _transport() {
        return this.transport;
    }

    public TransportOptions _transportOptions() {
        return this.transportOptions == null ? transport.options() : transportOptions;
    }
}
