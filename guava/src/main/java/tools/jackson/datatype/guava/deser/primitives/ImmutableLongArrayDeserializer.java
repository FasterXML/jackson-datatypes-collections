package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.AccessPattern;

import com.google.common.primitives.ImmutableLongArray;

public final class ImmutableLongArrayDeserializer extends StdDeserializer<ImmutableLongArray>
{
    private final ValueDeserializer<long[]> _longArrayDeserializer;

    public ImmutableLongArrayDeserializer() {
        super(ImmutableLongArray.class);
        @SuppressWarnings("unchecked")
        ValueDeserializer<long[]> deser = (ValueDeserializer<long[]>) PrimitiveArrayDeserializers.forType(long.class);
        _longArrayDeserializer = deser;
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.Array;
    }

    @Override
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override
    public ImmutableLongArray getEmptyValue(DeserializationContext ctxt) {
        return ImmutableLongArray.of();
    }

    @Override
    public ImmutableLongArray deserialize(JsonParser p, DeserializationContext ctxt) {
        return ImmutableLongArray.copyOf(_longArrayDeserializer.deserialize(p, ctxt));
    }
}
