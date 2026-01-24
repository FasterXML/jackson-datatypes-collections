package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;

import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.AccessPattern;

import com.google.common.primitives.ImmutableDoubleArray;

public final class ImmutableDoubleArrayDeserializer extends StdDeserializer<ImmutableDoubleArray>
{
    private final ValueDeserializer<double[]> _doubleArrayDeserializer;

    @SuppressWarnings("unchecked")
    public ImmutableDoubleArrayDeserializer() {
        super(ImmutableDoubleArray.class);
        _doubleArrayDeserializer =
            (ValueDeserializer<double[]>) PrimitiveArrayDeserializers.forType(double.class);
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
    public ImmutableDoubleArray getEmptyValue(DeserializationContext ctxt) {
        return ImmutableDoubleArray.of();
    }

    @Override
    public ImmutableDoubleArray deserialize(JsonParser p, DeserializationContext ctxt) {
        return ImmutableDoubleArray.copyOf(_doubleArrayDeserializer.deserialize(p, ctxt));
    }
}
