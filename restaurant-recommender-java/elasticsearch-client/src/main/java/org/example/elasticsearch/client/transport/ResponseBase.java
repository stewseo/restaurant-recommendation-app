package org.example.elasticsearch.client.transport;

public abstract class ResponseBase {

    public abstract TransportInfo _transportInfo();

    public abstract void _transportInfo(TransportInfo info);

}