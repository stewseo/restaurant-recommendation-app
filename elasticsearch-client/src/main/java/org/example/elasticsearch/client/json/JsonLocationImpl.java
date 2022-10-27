package org.example.elasticsearch.client.json;

import jakarta.json.stream.*;

class JsonLocationImpl implements JsonLocation {

    private final long columnNo;
    private final long lineNo;
    private final long offset;

    JsonLocationImpl(long lineNo, long columnNo, long streamOffset) {
        this.lineNo = lineNo;
        this.columnNo = columnNo;
        this.offset = streamOffset;
    }

    @Override
    public long getLineNumber() {
        return lineNo;
    }

    @Override
    public long getColumnNumber() {
        return columnNo;
    }

    @Override
    public long getStreamOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "(line no=" + lineNo + ", column no=" + columnNo + ", offset=" + offset + ")";
    }
}