package com.example.client.json.jackson;

import jakarta.json.stream.*;

public class JacksonJsonpLocation implements JsonLocation {

    private final com.fasterxml.jackson.core.JsonLocation location;

    JacksonJsonpLocation(com.fasterxml.jackson.core.JsonLocation location) {
        this.location = location;
    }

    JacksonJsonpLocation(com.fasterxml.jackson.core.JsonParser parser) {
        this(parser.getTokenLocation());
    }

    @Override
    public long getLineNumber() {
        return location.getLineNr();
    }

    @Override
    public long getColumnNumber() {
        return location.getColumnNr();
    }

    @Override
    public long getStreamOffset() {
        long charOffset = location.getCharOffset();
        return charOffset == -1 ? location.getByteOffset() : charOffset;
    }

    @Override
    public String toString() {
        return "(line no="+location.getLineNr()+", column no="+location.getColumnNr()+", offset="+ location.getCharOffset() +")";
    }
}
