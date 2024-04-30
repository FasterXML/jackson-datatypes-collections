package tools.jackson.datatype.guava.ser.primitives;

import tools.jackson.core.JsonGenerator;
import tools.jackson.datatype.guava.util.ImmutablePrimitiveTypes;

import com.google.common.primitives.ImmutableLongArray;

public class ImmutableLongArraySerializer extends BaseImmutableArraySerializer<ImmutableLongArray> {

    public ImmutableLongArraySerializer() {
        super(ImmutablePrimitiveTypes.ImmutablePrimitiveArrays.LONG);
    }

    @Override
    protected void writeArray(ImmutableLongArray immutableArray, JsonGenerator gen) {
        if (!immutableArray.isEmpty()) {
            gen.writeArray(immutableArray.toArray(), 0, immutableArray.length());
        }
    }

}