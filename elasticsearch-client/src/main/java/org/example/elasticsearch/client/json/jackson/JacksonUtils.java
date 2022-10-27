package org.example.elasticsearch.client.json.jackson;


import com.fasterxml.jackson.core.*;
import jakarta.json.*;
import jakarta.json.stream.*;
import jakarta.json.stream.JsonGenerationException;

import java.io.*;

public class JacksonUtils {
    public static JsonException convertException(IOException ioe) {
        if (ioe instanceof com.fasterxml.jackson.core.JsonGenerationException) {
            return new JsonGenerationException(ioe.getMessage(), ioe);

        } else if (ioe instanceof com.fasterxml.jackson.core.JsonParseException) {
            JsonParseException jpe = (JsonParseException) ioe;
            return new JsonParsingException(ioe.getMessage(), jpe, new JacksonJsonpLocation(jpe.getLocation()));

        } else {
            return new JsonException("Jackson exception", ioe);
        }
    }
}