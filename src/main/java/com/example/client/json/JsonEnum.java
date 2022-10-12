package com.example.client.json;

import jakarta.json.stream.*;

import java.util.*;

public interface JsonEnum extends JsonpSerializable {
    String jsonValue();

    default String[] aliases() {
        return null;
    }

    @Override
    default void serialize(JsonGenerator generator, JsonpMapper params) {
        generator.write(jsonValue());
    }

    class Deserializer<T extends JsonEnum> extends JsonpDeserializerBase<T> {

        private static final EnumSet<JsonParser.Event> acceptedEvents = EnumSet.of(JsonParser.Event.VALUE_STRING, JsonParser.Event.KEY_NAME);
        private static final EnumSet<JsonParser.Event> nativeEvents = EnumSet.of(JsonParser.Event.VALUE_STRING);

        private final Map<String, T> lookupTable;

        public Deserializer(T[] values) {
            this(values, acceptedEvents);
        }

        protected Deserializer(T[] values, EnumSet<JsonParser.Event> acceptedEvents) {
            super(acceptedEvents, nativeEvents);

            // Use the same size calculation as in java.lang.Enum.enumConstantDirectory
            this.lookupTable = new HashMap<>((int)(values.length / 0.75f) + 1);
            for (T member : values) {
                String jsonValue = member.jsonValue();
                if (jsonValue != null) { // _Custom enum members have a null jsonValue
                    this.lookupTable.put(jsonValue, member);
                }
                String[] aliases = member.aliases();
                if (aliases != null) {
                    for (String alias: aliases) {
                        this.lookupTable.put(alias, member);
                    }
                }
            }
        }

        @Override
        public T deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
            String value = parser.getString();
            return deserialize(value, parser);
        }

        /**
         * Get the enum member for a JSON string value
         *
         * @param value the JSON value
         * @param parser parsing context
         * @return the enum member
         * @throws JsonParsingException if no matching enum was found
         */
        public T deserialize(String value, JsonParser parser) {
            T result = this.lookupTable.get(value);
            if (result == null) {
                throw new JsonpMappingException("Invalid enum '" + value + "'", parser.getLocation());
            }
            return result;
        }

        /**
         * Get the enum member for a JSON string value
         *
         * @param value the JSON value
         * @return the enum member
         * @throws IllegalArgumentException if no matching enum was found
         */
        public T parse(String value) {
            T result = this.lookupTable.get(value);
            if (result == null) {
                throw new NoSuchElementException("Invalid enum '" + value + "'");
            }
            return result;
        }

        /**
         * An enum deserializer that also accepts boolean values. Used for a few properties that started as two-state booleans
         * and evolved into enums over time.
         */
        public static class AllowingBooleans<T extends JsonEnum> extends Deserializer<T> {

            private static final EnumSet<JsonParser.Event> acceptedEventsAndBoolean =
                    EnumSet.of(JsonParser.Event.VALUE_STRING, JsonParser.Event.KEY_NAME, JsonParser.Event.VALUE_TRUE, JsonParser.Event.VALUE_FALSE);

            public AllowingBooleans(T[] values) {
                super(values, acceptedEventsAndBoolean);
            }

            @Override
            public T deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
                String value;
                if (event == JsonParser.Event.VALUE_TRUE) {
                    value = "true";
                } else if (event == JsonParser.Event.VALUE_FALSE) {
                    value = "false";
                } else {
                    value = parser.getString();
                }

                return deserialize(value, parser);
            }
        }
    }
}