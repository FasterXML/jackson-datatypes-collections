package tools.jackson.datatype.eclipsecollections.ser.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import tools.jackson.datatype.primitive_collections_base.ser.map.PrimitiveMapSerializer;

import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.map.primitive.*;

/**
 * @author yawkat
 */
public final class PrimitivePrimitiveMapSerializers {
    private PrimitivePrimitiveMapSerializers() {
    }

    public static Map<Class<? extends PrimitiveIterable>, PrimitiveMapSerializer<?>> getInstances() {
        return INSTANCES;
    }

    private static final Map<Class<? extends PrimitiveIterable>, PrimitiveMapSerializer<?>> INSTANCES;

    /* with
        byte|char|short|int|long|float|double key
        short|byte|char|int|long|float|double|boolean value
    */
    private static final PrimitiveMapSerializer<ByteShortMap> BYTE_SHORT =
            new PrimitiveMapSerializer<ByteShortMap>(ByteShortMap.class) {
                @Override
                protected void serializeEntries(ByteShortMap value, JsonGenerator g, SerializationContext ctxt)
                {
                    value.forEachKeyValue((k, v) -> {
                        g.writeName(String.valueOf(k));
                        /* if !(char|boolean value) */
                        g.writeNumber(v);
                        /* elif char value //
                        g.writeString(new char[]{v}, 0, 1);
                        /* elif boolean value //
                        g.writeBoolean(v);
                        // endif */
                    });
                }

                @Override
                public boolean isEmpty(SerializationContext ctxt, ByteShortMap value) {
                    return value.isEmpty();
                }
            };
    /* endwith */

    static {
        Map<Class<? extends PrimitiveIterable>, PrimitiveMapSerializer<?>> instances =
                new HashMap<>();
        /* with
            byte|char|short|int|long|float|double key
            short|byte|char|int|long|float|double|boolean value
        */
        instances.put(ByteShortMap.class, BYTE_SHORT);
        /* endwith */
        INSTANCES = Collections.unmodifiableMap(instances);
    }
}
