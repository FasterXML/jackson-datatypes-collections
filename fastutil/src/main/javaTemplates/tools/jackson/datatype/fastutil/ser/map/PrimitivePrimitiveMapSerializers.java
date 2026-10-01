package tools.jackson.datatype.fastutil.ser.map;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import tools.jackson.datatype.primitive_collections_base.ser.map.PrimitiveMapSerializer;

import it.unimi.dsi.fastutil.bytes.*;
import it.unimi.dsi.fastutil.chars.*;
import it.unimi.dsi.fastutil.doubles.*;
import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.shorts.*;

/**
 * Serializers for fastutil maps with primitive keys and primitive values
 * (like {@code Int2LongMap}).
 */
public final class PrimitivePrimitiveMapSerializers {
    private PrimitivePrimitiveMapSerializers() {
    }

    /**
     * @return Serializers keyed by the fastutil map interface they handle
     */
    public static Map<Class<?>, PrimitiveMapSerializer<?>> getInstances() {
        return INSTANCES;
    }

    private static final Map<Class<?>, PrimitiveMapSerializer<?>> INSTANCES;

    /* with
        byte|char|short|int|long|float|double key
        short|byte|char|int|long|float|double|boolean value
    */
    private static final PrimitiveMapSerializer<Byte2ShortMap> BYTE_SHORT =
            new PrimitiveMapSerializer<Byte2ShortMap>(Byte2ShortMap.class) {
                @Override
                protected void serializeEntries(Byte2ShortMap value, JsonGenerator g, SerializationContext ctxt)
                {
                    Byte2ShortMaps.fastForEach(value, e -> {
                        g.writeName(String.valueOf(e.getByteKey()));
                        /* if !(char|boolean value) */
                        g.writeNumber(e.getShortValue());
                        /* elif char value //
                        g.writeString(new char[]{e.getShortValue()}, 0, 1);
                        /* elif boolean value //
                        g.writeBoolean(e.getShortValue());
                        // endif */
                    });
                }

                @Override
                public boolean isEmpty(SerializationContext ctxt, Byte2ShortMap value) {
                    return value.isEmpty();
                }
            };
    /* endwith */

    static {
        Map<Class<?>, PrimitiveMapSerializer<?>> instances = new LinkedHashMap<>();
        /* with
            byte|char|short|int|long|float|double key
            short|byte|char|int|long|float|double|boolean value
        */
        instances.put(Byte2ShortMap.class, BYTE_SHORT);
        /* endwith */
        INSTANCES = Collections.unmodifiableMap(instances);
    }
}
