package org.example.elasticsearch.client.json;

import jakarta.json.stream.*;
import org.example.elasticsearch.client.util.*;


import java.util.*;
import java.util.function.*;

public class ObjectDeserializer<ObjectType> implements JsonpDeserializer<ObjectType> {

    public abstract static class FieldDeserializer<ObjectType> {
        protected final String name;

        public FieldDeserializer(String name) {
            this.name = name;
        }

        public abstract EnumSet<JsonParser.Event> acceptedEvents();

        public abstract void deserialize(JsonParser parser, JsonpMapper mapper, String fieldName, ObjectType object);

        public abstract void deserialize(JsonParser parser, JsonpMapper mapper, String fieldName, ObjectType object, JsonParser.Event event);
    }



    public static class FieldObjectDeserializer<ObjectType, FieldType> extends FieldDeserializer<ObjectType> {
        private final BiConsumer<ObjectType, FieldType> setter;
        private final JsonpDeserializer<FieldType> deserializer;

        public FieldObjectDeserializer(
                BiConsumer<ObjectType, FieldType> setter, JsonpDeserializer<FieldType> deserializer,
                String name
        ) {
            super(name);
            this.setter = setter;
            this.deserializer = deserializer;
        }

        public String name() {
            return this.name;
        }

        @Override
        public EnumSet<JsonParser.Event> acceptedEvents() {
            return deserializer.acceptedEvents();
        }

        public void deserialize(JsonParser parser, JsonpMapper mapper, String fieldName, ObjectType object) {
            FieldType fieldValue = deserializer.deserialize(parser, mapper);
            setter.accept(object, fieldValue);
        }

        public void deserialize(JsonParser parser, JsonpMapper mapper, String fieldName, ObjectType object, JsonParser.Event event) {
            JsonpUtils.ensureAccepts(deserializer, parser, event);
            FieldType fieldValue = deserializer.deserialize(parser, mapper, event);
            setter.accept(object, fieldValue);
        }
    }

    private static final FieldDeserializer<?> IGNORED_FIELD = new FieldDeserializer<Object>("-") {

        @Override
        public void deserialize(JsonParser parser, JsonpMapper mapper, String fieldName, Object object) {
            JsonpUtils.skipValue(parser);
        }

        @Override
        public void deserialize(JsonParser parser, JsonpMapper mapper, String fieldName, Object object, JsonParser.Event event) {
            JsonpUtils.skipValue(parser, event);
        }

        @Override
        public EnumSet<JsonParser.Event> acceptedEvents() {
            return EnumSet.allOf(JsonParser.Event.class);
        }
    };

    //---------------------------------------------------------------------------------------------
    private static final EnumSet<JsonParser.Event> EventSetObject = EnumSet.of(JsonParser.Event.START_OBJECT, JsonParser.Event.KEY_NAME);
    private static final EnumSet<JsonParser.Event> EventSetObjectAndString = EnumSet.of(JsonParser.Event.START_OBJECT, JsonParser.Event.VALUE_STRING, JsonParser.Event.KEY_NAME);

    private EnumSet<JsonParser.Event> acceptedEvents = EventSetObject; // May be changed in `shortcutProperty()`
    private final Supplier<ObjectType> constructor;
    protected final Map<String, FieldDeserializer<ObjectType>> fieldDeserializers;
    private FieldDeserializer<ObjectType> singleKey;
    private String typeProperty;
    private String defaultType;
    private FieldDeserializer<ObjectType> shortcutProperty;
    private QuadConsumer<ObjectType, String, JsonParser, JsonpMapper> unknownFieldHandler;

    public ObjectDeserializer(Supplier<ObjectType> constructor) {
        this.constructor = constructor;
        this.fieldDeserializers = new HashMap<>();
    }


    public Set<String> fieldNames() {
        return Collections.unmodifiableSet(fieldDeserializers.keySet());
    }

    public String shortcutProperty() {
        return this.shortcutProperty == null ? null : this.shortcutProperty.name;
    }

    @Override
    public EnumSet<JsonParser.Event> nativeEvents() {
        // May also return string if we have a shortcut property. This is needed to identify ambiguous unions.
        return acceptedEvents;
    }

    @Override
    public EnumSet<JsonParser.Event> acceptedEvents() {
        return acceptedEvents;
    }

    public ObjectType deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
        return deserialize(constructor.get(), parser, mapper, event);
    }

    public ObjectType deserialize(ObjectType value, JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
        if (event == JsonParser.Event.VALUE_NULL) {
            return null;
        }

        String keyName = null;
        String fieldName = null;

        try {

            if (singleKey != null) {
                // There's a wrapping property whose name is the key value
                if (event == JsonParser.Event.START_OBJECT) {
                    event = JsonpUtils.expectNextEvent(parser, JsonParser.Event.KEY_NAME);
                }
                singleKey.deserialize(parser, mapper, null, value, event);
                event = parser.next();
            }

            if (shortcutProperty != null && event != JsonParser.Event.START_OBJECT && event != JsonParser.Event.KEY_NAME) {
                // This is the shortcut property (should be a value event, this will be checked by its deserializer)
                shortcutProperty.deserialize(parser, mapper, shortcutProperty.name, value, event);

            } else if (typeProperty == null) {
                if (event != JsonParser.Event.START_OBJECT && event != JsonParser.Event.KEY_NAME) {
                    // Report we're waiting for a start_object, since this is the most common beginning for object parser
                    JsonpUtils.expectEvent(parser, JsonParser.Event.START_OBJECT, event);
                }

                if (event == JsonParser.Event.START_OBJECT) {
                    event = parser.next();
                }
                // Regular object: read all properties until we reach the end of the object
                while (event != JsonParser.Event.END_OBJECT) {
                    JsonpUtils.expectEvent(parser, JsonParser.Event.KEY_NAME, event);
                    fieldName = parser.getString();

                    FieldDeserializer<ObjectType> fieldDeserializer = fieldDeserializers.get(fieldName);
                    if (fieldDeserializer == null) {
                        parseUnknownField(parser, mapper, fieldName, value);
                    } else {
                        fieldDeserializer.deserialize(parser, mapper, fieldName, value);
                    }
                    event = parser.next();
                }
                fieldName = null;
            } else {
                // Union variant: find the property to find the proper deserializer
                // We cannot start with a key name here.
                JsonpUtils.expectEvent(parser, JsonParser.Event.START_OBJECT, event);
                Map.Entry<String, JsonParser> unionInfo = JsonpUtils.lookAheadFieldValue(typeProperty, defaultType, parser, mapper);
                String variant = unionInfo.getKey();
                JsonParser innerParser = unionInfo.getValue();

                FieldDeserializer<ObjectType> fieldDeserializer = fieldDeserializers.get(variant);
                if (fieldDeserializer == null) {
                    parseUnknownField(innerParser, mapper, variant, value);
                } else {
                    fieldDeserializer.deserialize(innerParser, mapper, variant, value);
                }
            }

            if (singleKey != null) {
                JsonpUtils.expectNextEvent(parser, JsonParser.Event.END_OBJECT);
            }
        } catch (Exception e) {
            // Add key name (for single key dicts) and field name if present
            throw JsonpMappingException.from(e, value, fieldName, parser).prepend(value, keyName);
        }

        return value;
    }

    protected void parseUnknownField(JsonParser parser, JsonpMapper mapper, String fieldName, ObjectType object) {
        if (this.unknownFieldHandler != null) {
            this.unknownFieldHandler.accept(object, fieldName, parser, mapper);

        } else if (mapper.ignoreUnknownFields()) {
            JsonpUtils.skipValue(parser);

        } else {
            // Context is added by the caller
            throw new JsonpMappingException("Unknown field '" + fieldName + "'", parser.getLocation());
        }
    }

    public void setUnknownFieldHandler(QuadConsumer<ObjectType, String, JsonParser, JsonpMapper> unknownFieldHandler) {
        this.unknownFieldHandler = unknownFieldHandler;
    }

    @SuppressWarnings("unchecked")
    public void ignore(String name) {
        this.fieldDeserializers.put(name, (FieldDeserializer<ObjectType>) IGNORED_FIELD);
    }

    public void shortcutProperty(String name) {
        this.shortcutProperty = this.fieldDeserializers.get(name);
        if (this.shortcutProperty == null) {
            throw new NoSuchElementException("No deserializer was setup for '" + name + "'");
        }

        acceptedEvents = EnumSet.copyOf(acceptedEvents);
        acceptedEvents.addAll(shortcutProperty.acceptedEvents());
        acceptedEvents = EventSetObjectAndString;
    }

    //----- Object types

    public <FieldType> void add(
            BiConsumer<ObjectType, FieldType> setter,
            JsonpDeserializer<FieldType> deserializer,
            String name
    ) {
        FieldObjectDeserializer<ObjectType, FieldType> fieldDeserializer =
                new FieldObjectDeserializer<>(setter, deserializer, name);
        this.fieldDeserializers.put(name, fieldDeserializer);
    }

    public <FieldType> void add(
            BiConsumer<ObjectType, FieldType> setter,
            JsonpDeserializer<FieldType> deserializer,
            String name, String... aliases
    ) {
        FieldObjectDeserializer<ObjectType, FieldType> fieldDeserializer =
                new FieldObjectDeserializer<>(setter, deserializer, name);
        this.fieldDeserializers.put(name, fieldDeserializer);
        for (String alias: aliases) {
            this.fieldDeserializers.put(alias, fieldDeserializer);
        }
    }

    public <FieldType> void setKey(BiConsumer<ObjectType, FieldType> setter, JsonpDeserializer<FieldType> deserializer) {
        this.singleKey = new FieldObjectDeserializer<>(setter, deserializer, null);
    }

    public void setTypeProperty(String name, String defaultType) {
        this.typeProperty = name;
        this.defaultType = defaultType;
        if (this.unknownFieldHandler == null) {
            this.unknownFieldHandler = (o, value, parser, mapper) -> {
                // Context is added by the caller
                throw new JsonpMappingException("Unknown '" + name + "' value: '" + value + "'", parser.getLocation());
            };
        }
    }

    //----- Primitive types

    public void add(ObjIntConsumer<ObjectType> setter, String name, String... deprecatedNames) {

        add(setter::accept, JsonpDeserializer.integerDeserializer(), name, deprecatedNames);
    }


//    public void add(
//        ObjIntConsumer<ObjectType> setter,
//        JsonpIntParser vp,
//        String name, String... deprecatedNames
//    ) {
//        this.fieldDeserializers.put(name, new FieldDeserializer<ObjectType>(name, deprecatedNames) {
//            @Override
//            public void deserialize(JsonParser parser, JsonpMapper mapper, String fieldName, ObjectType object) {
//                JsonpUtils.expectNextEvent(parser, JsonParser.Event.VALUE_NUMBER);
//                setter.accept(object, vp.parse(parser));
//            }
//        });
//    }

    public static class JsonpIntParser {
        public int parse(JsonParser parser) {
            JsonpUtils.expectNextEvent(parser, JsonParser.Event.VALUE_NUMBER);
            return parser.getInt();
        }
    }

}