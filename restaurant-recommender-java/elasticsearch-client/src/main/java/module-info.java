module org.example.elasticsearch.client {
    requires com.fasterxml.jackson.databind;
    requires jakarta.json;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.slf4j;
    requires jakarta.json.bind;


    exports org.example.elasticsearch.client._types;
    exports org.example.elasticsearch.client.elasticsearch.core;
    exports org.example.elasticsearch.client.json;
    exports org.example.elasticsearch.client.json.jackson;
    exports org.example.elasticsearch.client.json.jsonb;

    exports org.example.elasticsearch.client.transport;
    exports org.example.elasticsearch.client.transport.restclient;
    exports org.example.elasticsearch.client.transport.endpoints;

    exports org.example.elasticsearch.client.util;

    exports org.example.elasticsearch.client;
}