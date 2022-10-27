module org.example.lowlevel.restclient {
    requires org.slf4j;
    requires org.apache.httpcomponents.httpasyncclient;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpcore.nio;
    requires org.apache.commons.logging;
    exports org.example.lowlevel.restclient;
}