package org.example.elasticsearch.client.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.*;
import jakarta.json.*;
import jakarta.json.spi.*;
import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;


import java.io.*;
import java.math.*;

class JsonValueParser {
    private final JsonProvider provider = JsonpUtils.provider();

    public JsonObject parseObject(JsonParser parser) throws IOException {

        JsonObjectBuilder ob = provider.createObjectBuilder();

        JsonToken token;
        while((token = parser.nextToken()) != JsonToken.END_OBJECT) {
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParsingException("Expected a property name", new JacksonJsonpLocation(parser));
            }
            String name = parser.getCurrentName();
            parser.nextToken();
            ob.add(name, parseValue(parser));
        }
        return ob.build();
    }

    public JsonArray parseArray(JsonParser parser) throws IOException {
        JsonArrayBuilder ab = provider.createArrayBuilder();

        while(parser.nextToken() != JsonToken.END_ARRAY) {
            ab.add(parseValue(parser));
        }
        return ab.build();
    }

    public JsonValue parseValue(JsonParser parser) throws IOException {
        JsonToken jsonToken = parser.currentToken();
        switch (parser.currentToken()) {
            case START_OBJECT:
                return parseObject(parser);

            case START_ARRAY:
                return parseArray(parser);

            case VALUE_TRUE:
                return JsonValue.TRUE;

            case VALUE_FALSE:
                return JsonValue.FALSE;

            case VALUE_NULL:
                return JsonValue.NULL;

            case VALUE_STRING:
                return provider.createValue(parser.getText());

            case VALUE_NUMBER_FLOAT:
            case VALUE_NUMBER_INT:
                switch(parser.getNumberType()) {
                    case INT:
                        return provider.createValue(parser.getIntValue());
                    case LONG:
                        return provider.createValue(parser.getLongValue());
                    case FLOAT:
                    case DOUBLE:
                        // Use double also for floats, as JSON-P has no support for float
                        return new DoubleNumber(parser.getDoubleValue());
                    case BIG_DECIMAL:
                        return provider.createValue(parser.getDecimalValue());
                    case BIG_INTEGER:
                        return provider.createValue(parser.getBigIntegerValue());
                }

            default:
                throw new JsonParsingException("Unexpected token '" + parser.currentToken() + "'", new JacksonJsonpLocation(parser));

        }
    }

    private static class DoubleNumber implements JsonNumber {

        private final double value;

        DoubleNumber(double value) {
            this.value = value;
        }

        @Override
        public boolean isIntegral() {
            return false;
        }

        @Override
        public int intValue() {
            return (int) value;
        }

        @Override
        public int intValueExact() {
            int result = (int) value;

            if ((double)result == value) {
                return result;
            } else {
                throw new ArithmeticException();
            }
        }

        @Override
        public long longValue() {
            return (long) value;
        }

        @Override
        public long longValueExact() {
            long result = (long) value;

            if ((double)result == value) {
                return result;
            } else {
                throw new ArithmeticException();
            }
        }

        @Override
        public BigInteger bigIntegerValue() {
            return bigDecimalValue().toBigInteger();
        }

        @Override
        public BigInteger bigIntegerValueExact() {
            return bigDecimalValue().toBigIntegerExact();
        }

        @Override
        public double doubleValue() {
            return value;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return new BigDecimal(value);
        }

        @Override
        public ValueType getValueType() {
            return ValueType.NUMBER;
        }

        @Override
        public Number numberValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public int hashCode() {
            return Double.hashCode(value);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DoubleNumber && ((DoubleNumber)obj).value == value;
        }
    }
}