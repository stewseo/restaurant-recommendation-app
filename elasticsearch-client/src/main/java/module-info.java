module org.example.elasticsearch.client {
    requires com.fasterxml.jackson.databind;
    requires jakarta.json;
    requires jakarta.json.bind;
    requires org.example.lowlevel.restclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.slf4j;
    exports org.example.elasticsearch.client.json.jackson;
    exports org.example.elasticsearch.client.json;
    exports org.example.elasticsearch.client.transport;
    exports org.example.elasticsearch.client.util;
    exports org.example.elasticsearch.client._types;
    exports org.example.elasticsearch.client.transport.restclient;
    exports org.example.elasticsearch.client.transport.endpoints;
    exports org.example.elasticsearch.client;
}