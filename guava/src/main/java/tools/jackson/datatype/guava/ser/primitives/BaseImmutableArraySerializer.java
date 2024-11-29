package tools.jackson.datatype.guava.ser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.datatype.guava.util.ImmutablePrimitiveTypes;

public abstract class BaseImmutableArraySerializer<ImmutableArray> extends StdSerializer<ImmutableArray> {
    protected BaseImmutableArraySerializer(ImmutablePrimitiveTypes.ImmutablePrimitiveArrays immutableArrayType) {
        super(immutableArrayType.type());
    }

    @Override
    public final void serialize(ImmutableArray immutableArray, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (immutableArray == null) {
            ctxt.defaultSerializeNullValue(gen);
        } else {
            writeArray(immutableArray, gen);
        }
    }

    protected abstract void writeArray(ImmutableArray immutableArray, JsonGenerator gen);
}
