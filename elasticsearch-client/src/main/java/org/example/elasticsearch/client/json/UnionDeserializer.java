package org.example.elasticsearch.client.json;

import jakarta.json.*;
import jakarta.json.stream.*;
import jakarta.json.stream.JsonParser.*;
import org.example.elasticsearch.client.util.*;

import java.util.*;
import java.util.function.*;
public class UnionDeserializer<Union, Kind, Member> implements JsonpDeserializer<Union> {

    public static class AmbiguousUnionException extends RuntimeException {
        public AmbiguousUnionException(String message) {
            super(message);
        }
    }

    private abstract static class EventHandler<Union, Kind, Member> {
        abstract Union deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event, BiFunction<Kind, Member, Union> buildFn);
        abstract EnumSet<JsonParser.Event> nativeEvents();
    }

    private static class SingleMemberHandler<Union, Kind, Member> extends EventHandler<Union, Kind, Member> {
        private final JsonpDeserializer<? extends Member> deserializer;
        private final Kind tag;
        // ObjectDeserializers provide the list of fields they know about
        private final Set<String> fields;

        SingleMemberHandler(Kind tag, JsonpDeserializer<? extends Member> deserializer) {
            this(tag, deserializer, null);
        }

        SingleMemberHandler(Kind tag, JsonpDeserializer<? extends Member> deserializer, Set<String> fields) {
            this.deserializer = deserializer;
            this.tag = tag;
            this.fields = fields;
        }

        @Override
        EnumSet<JsonParser.Event> nativeEvents() {
            return deserializer.nativeEvents();
        }

        @Override
        Union deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event, BiFunction<Kind, Member, Union> buildFn) {
            return buildFn.apply(tag, deserializer.deserialize(parser, mapper, event));
        }
    }

    private static class MultiMemberHandler<Union, Kind, Member> extends EventHandler<Union, Kind, Member> {
        private List<SingleMemberHandler<Union, Kind, Member>> handlers;

        @Override
        EnumSet<JsonParser.Event> nativeEvents() {
            EnumSet<JsonParser.Event> result = EnumSet.noneOf(JsonParser.Event.class);
            for (SingleMemberHandler<Union, Kind, Member> smh: handlers) {
                result.addAll(smh.deserializer.nativeEvents());
            }
            return result;
        }

        @Override
        Union deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event, BiFunction<Kind, Member, Union> buildFn) {
            RuntimeException exception = null;
            for (EventHandler<Union, Kind, Member> d: handlers) {
                try {
                    return d.deserialize(parser, mapper, event, buildFn);
                } catch(RuntimeException ex) {
                    exception = ex;
                }
            }
            throw JsonpMappingException.from(exception, null, null, parser);
        }
    }

    public static class Builder<Union, Kind, Member> implements ObjectBuilder<JsonpDeserializer<Union>> {

        private final BiFunction<Kind, Member, Union> buildFn;

        private final List<UnionDeserializer.SingleMemberHandler<Union, Kind, Member>> objectMembers = new ArrayList<>();
        private final Map<JsonParser.Event, EventHandler<Union, Kind, Member>> otherMembers = new HashMap<>();
        private final boolean allowAmbiguousPrimitive;

        public Builder(BiFunction<Kind, Member, Union> buildFn, boolean allowAmbiguities) {
            // If we allow ambiguities, multiple handlers for a given JSON value event will be allowed
            this.allowAmbiguousPrimitive = allowAmbiguities;
            this.buildFn = buildFn;
        }

        private void addAmbiguousDeserializer(JsonParser.Event e, Kind tag, JsonpDeserializer<? extends Member> deserializer) {
            EventHandler<Union, Kind, Member> m = otherMembers.get(e);
            MultiMemberHandler<Union, Kind, Member> mmh;
            if (m instanceof MultiMemberHandler<?, ?, ?>) {
                mmh = (MultiMemberHandler<Union, Kind, Member>) m;
            } else {
                mmh = new MultiMemberHandler<>();
                mmh.handlers = new ArrayList<>(2);
                mmh.handlers.add((SingleMemberHandler<Union, Kind, Member>) m);
                otherMembers.put(e, mmh);
            }
            mmh.handlers.add(new SingleMemberHandler<>(tag, deserializer));
            // Sort handlers by number of accepted events, which gives their specificity
            mmh.handlers.sort(Comparator.comparingInt(a -> a.deserializer.acceptedEvents().size()));
        }

        private void addMember(JsonParser.Event e, Kind tag, UnionDeserializer.SingleMemberHandler<Union, Kind, Member> member) {
            if (otherMembers.containsKey(e)) {
                if (!allowAmbiguousPrimitive || e == JsonParser.Event.START_OBJECT || e == JsonParser.Event.START_ARRAY) {
                    throw new AmbiguousUnionException("Union member '" + tag + "' conflicts with other members");
                } else {
                    // Allow ambiguities on value event
                    addAmbiguousDeserializer(e, tag, member.deserializer);
                }
            } else {
                // Note: we accept START_OBJECT here. It can be a user-provided type, and will be used
                // as a fallback if no element of objectMembers matches.
                otherMembers.put(e, member);
            }
        }

        public Builder<Union, Kind, Member> addMember(Kind tag, JsonpDeserializer<? extends Member> deserializer) {

            JsonpDeserializer<?> unwrapped = DelegatingDeserializer.unwrap(deserializer);
            if (unwrapped instanceof ObjectDeserializer) {
                ObjectDeserializer<?> od = (ObjectDeserializer<?>) unwrapped;
                Set<String> allFields = od.fieldNames();
                Set<String> fields = new HashSet<>(allFields); // copy to update
                for (UnionDeserializer.SingleMemberHandler<Union, Kind, Member> member: objectMembers) {
                    // Remove respective fields on both sides to keep specific ones
                    fields.removeAll(member.fields);
                    member.fields.removeAll(allFields);
                }
                UnionDeserializer.SingleMemberHandler<Union, Kind, Member> member = new SingleMemberHandler<>(tag, deserializer, fields);
                objectMembers.add(member);
                if (od.shortcutProperty() != null) {
                    // also add it as a string
                    addMember(Event.VALUE_STRING, tag, member);
                }
            } else {
                UnionDeserializer.SingleMemberHandler<Union, Kind, Member> member = new SingleMemberHandler<>(tag, deserializer);
                for (Event e: deserializer.nativeEvents()) {
                    addMember(e, tag, member);
                }
            }

            return this;
        }

        @Override
        public JsonpDeserializer<Union> build() {
            // Check that no object member had all its fields removed
            for (UnionDeserializer.SingleMemberHandler<Union, Kind, Member> member: objectMembers) {
                if (member.fields.isEmpty()) {
                    throw new AmbiguousUnionException("All properties of '" + member.tag + "' also exist in other object members");
                }
            }

            if (objectMembers.size() == 1 && !otherMembers.containsKey(Event.START_OBJECT)) {
                // A single deserializer handles objects: promote it to otherMembers as we don't need property-based disambiguation
                otherMembers.put(Event.START_OBJECT, objectMembers.remove(0));
            }

//            if (objectMembers.size() > 1) {
//                System.out.println("multiple objects in " + buildFn);
//            }

            return new UnionDeserializer<>(objectMembers, otherMembers, buildFn);
        }
    }

    private final BiFunction<Kind, Member, Union> buildFn;
    private final EnumSet<JsonParser.Event> nativeEvents;
    private final Map<String, EventHandler<Union, Kind, Member>> objectMembers;
    private final Map<JsonParser.Event, EventHandler<Union, Kind, Member>> otherMembers;
    private final EventHandler<Union, Kind, Member> fallbackObjectMember;

    public UnionDeserializer(
            List<SingleMemberHandler<Union, Kind, Member>> objectMembers,
            Map<JsonParser.Event, EventHandler<Union, Kind, Member>> otherMembers,
            BiFunction<Kind, Member, Union> buildFn
    ) {
        this.buildFn = buildFn;

        // Build a map of (field name -> member) for all fields to speed up lookup
        if (objectMembers.isEmpty()) {
            this.objectMembers = Collections.emptyMap();
        } else {
            this.objectMembers = new HashMap<>();
            for (SingleMemberHandler<Union, Kind, Member> member: objectMembers) {
                for (String field: member.fields) {
                    this.objectMembers.put(field, member);
                }
            }
        }

        this.otherMembers = otherMembers;

        this.nativeEvents = EnumSet.noneOf(Event.class);
        for (EventHandler<Union, Kind, Member> member: otherMembers.values()) {
            this.nativeEvents.addAll(member.nativeEvents());
        }

        if (objectMembers.isEmpty()) {
            fallbackObjectMember = null;
        } else {
            fallbackObjectMember = this.otherMembers.remove(Event.START_OBJECT);
            this.nativeEvents.add(Event.START_OBJECT);
        }
    }

    @Override
    public EnumSet<JsonParser.Event> nativeEvents() {
        return nativeEvents;
    }

    @Override
    public EnumSet<JsonParser.Event> acceptedEvents() {
        // In a union we want the real thing
        return nativeEvents;
    }

    @Override
    public boolean accepts(JsonParser.Event event) {
        return JsonpDeserializer.super.accepts(event);
    }

    @Override
    public Union deserialize(JsonParser parser, JsonpMapper mapper) {
        Event event = parser.next();
        JsonpUtils.ensureAccepts(this, parser, event);
        return deserialize(parser, mapper, event);
    }


    @Override
    public Union deserialize(JsonParser parser, JsonpMapper mapper, Event event) {
        EventHandler<Union, Kind, Member> member = otherMembers.get(event);

        if (member == null && event == Event.START_OBJECT && !objectMembers.isEmpty()) {
            // Parse as an object to find matching field names
            JsonObject object = parser.getObject();

            for (String field: object.keySet()) {
                member = objectMembers.get(field);
                if (member != null) {
                    break;
                }
            }

            if (member == null) {
                member = fallbackObjectMember;
            }

            if (member != null) {
                // Traverse the object we have inspected
                parser = JsonpUtils.objectParser(object, mapper);
                event = parser.next();
            }
        }

        if (member == null) {
            throw new JsonpMappingException("Cannot determine what union member to deserialize", parser.getLocation());
        }

        return member.deserialize(parser, mapper, event, buildFn);
    }
}