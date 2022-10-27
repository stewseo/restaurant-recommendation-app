package org.example.elasticsearch.client.json;


import jakarta.json.stream.*;

import java.io.*;

public interface WithJson<T> {

    default T withJson(InputStream input) {
        JsonpMapper mapper = SimpleJsonpMapper.INSTANCE_REJECT_UNKNOWN_FIELDS;
        return withJson(mapper.jsonProvider().createParser(input), mapper);
    }


    default T withJson(Reader input) {
        JsonpMapper mapper = SimpleJsonpMapper.INSTANCE_REJECT_UNKNOWN_FIELDS;
        return withJson(mapper.jsonProvider().createParser(input), mapper);
    }

    T withJson(JsonParser parser, JsonpMapper mapper);
}
