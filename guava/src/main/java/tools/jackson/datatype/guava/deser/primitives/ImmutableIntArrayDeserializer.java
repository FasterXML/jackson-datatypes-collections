package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.util.AccessPattern;
import com.google.common.primitives.ImmutableIntArray;

public final class ImmutableIntArrayDeserializer extends StdDeserializer<ImmutableIntArray>
{
    private final ValueDeserializer<int[]> _intArrayDeserializer;

    @SuppressWarnings("unchecked")
    public ImmutableIntArrayDeserializer() {
        super(ImmutableIntArray.class);
        _intArrayDeserializer = (ValueDeserializer<int[]>) PrimitiveArrayDeserializers.forType(int.class);
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
    public ImmutableIntArray getEmptyValue(DeserializationContext ctxt) {
        return ImmutableIntArray.of();
    }

    @Override
    public ImmutableIntArray deserialize(JsonParser p, DeserializationContext ctxt) {
        return ImmutableIntArray.copyOf(_intArrayDeserializer.deserialize(p, ctxt));
    }
}
