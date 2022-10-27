package org.example.elasticsearch.client._types;

import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;
import org.example.elasticsearch.client.util.*;

import java.util.function.*;

@JsonpDeserializable
public class Time implements TaggedUnion<Time.Kind, Object>, JsonpSerializable {

    public enum Kind {
        Offset, Time
    }

    private final Kind _kind;
    private final Object _value;
    @Override
    public final Kind _kind() {
        return _kind;
    }

    @Override
    public final Object _get() {
        return _value;
    }

    private Time(Kind kind, Object value) {
        this._kind = kind;
        this._value = value;
    }

    public String _toJsonString() {
        return switch (_kind) {
            case Offset -> String.valueOf(this.offset());
            case Time -> this.time();
            default -> throw new IllegalStateException("Unknown kind " + _kind);
        };
    }

    private Time(Builder builder) {

        this._kind = ApiTypeHelper.requireNonNull(builder._kind, builder, "<variant kind>");
        this._value = ApiTypeHelper.requireNonNull(builder._value, builder, "<variant value>");

    }

    public static Time of(Function<Builder, ObjectBuilder<Time>> fn) {
        return fn.apply(new Builder()).build();
    }

    public boolean isOffset() {
        return _kind == Kind.Offset;
    }

    public Integer offset() {
        return TaggedUnionUtils.get(this, Kind.Offset);
    }

    public boolean isTime() {
        return _kind == Kind.Time;
    }
    public String time() {
        return TaggedUnionUtils.get(this, Kind.Time);
    }

    @Override
    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        if (_value instanceof JsonpSerializable) {
            ((JsonpSerializable) _value).serialize(generator, mapper);
        } else {
            switch (_kind) {
                case Offset -> generator.write(((Integer) this._value));
                case Time -> generator.write(((String) this._value));
            }
        }

    }

    @Override
    public String toString() {
        return JsonpUtils.toString(this);
    }

    public static class Builder extends ObjectBuilderBase implements ObjectBuilder<Time> {
        private Kind _kind;
        private Object _value;

        public ObjectBuilder<Time> offset(Integer v) {
            this._kind = Kind.Offset;
            this._value = v;
            return this;
        }

        public ObjectBuilder<Time> time(String v) {
            this._kind = Kind.Time;
            this._value = v;
            return this;
        }

        public Time build() {
            _checkSingleUse();
            return new Time(this);
        }

    }

    private static JsonpDeserializer<Time> buildTimeDeserializer() {
        return new UnionDeserializer.Builder<Time, Kind, Object>(Time::new, false)
                .addMember(Kind.Offset, JsonpDeserializer.integerDeserializer())
                .addMember(Kind.Time, JsonpDeserializer.stringDeserializer()).build();
    }

    public static final JsonpDeserializer<Time> _DESERIALIZER = JsonpDeserializer.lazy(Time::buildTimeDeserializer);
}