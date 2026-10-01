package tools.jackson.datatype.fastutil.ser.map;

import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.datatype.primitive_collections_base.ser.map.PrimitiveRefMapSerializer;

import it.unimi.dsi.fastutil.bytes.*;
import it.unimi.dsi.fastutil.chars.*;
import it.unimi.dsi.fastutil.doubles.*;
import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.shorts.*;

/**
 * Serializers for fastutil maps with primitive keys and reference values
 * (like {@code Int2ObjectMap}).
 */
@SuppressWarnings({ "Duplicates", "NewClassNamingConvention" })
public final class PrimitiveRefMapSerializers
{
    private PrimitiveRefMapSerializers() {
    }

    /* with char|byte|short|int|float|long|double key */

    public static class Char<V> extends PrimitiveRefMapSerializer<Char2ObjectMap<V>, V>
    {
        public Char(JavaType type, BeanProperty property, TypeSerializer vts, ValueSerializer<Object> valueSerializer) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(Char2ObjectMap<V> value, JsonGenerator g, SerializationContext ctxt) {
            Char2ObjectMaps.fastForEach(value, e -> {
                /* if !(int|long key) */
                g.writeName(String.valueOf(e.getCharKey()));
                /* elif int|long key //
                // as Jackson does for `Integer` and `Long` keys of `java.util.Map`s
                g.writePropertyId(e.getCharKey());
                // endif */
                V v = e.getValue();
                if (v == null) {
                    ctxt.defaultSerializeNullValue(g);
                } else {
                    _serializeValue(v, g, ctxt);
                }
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<Char2ObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                ValueSerializer<Object> valueSerializer
        ) {
            return new Char<>(_type, property, vts, valueSerializer);
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, Char2ObjectMap<V> value) {
            return value.isEmpty();
        }
    }

    /* endwith */
}
