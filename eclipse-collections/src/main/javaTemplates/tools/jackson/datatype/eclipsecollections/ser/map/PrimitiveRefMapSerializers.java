package tools.jackson.datatype.eclipsecollections.ser.map;

import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.datatype.primitive_collections_base.ser.map.PrimitiveRefMapSerializer;
import org.eclipse.collections.api.map.primitive.*;

/**
 * @author yawkat
 */
@SuppressWarnings({ "Duplicates", "NewClassNamingConvention" })
public final class PrimitiveRefMapSerializers
{
    private PrimitiveRefMapSerializers() {
    }

    /* with char|byte|short|int|float|long|double key */

    public static class Char<V> extends PrimitiveRefMapSerializer<CharObjectMap<V>, V>
    {
        public Char(JavaType type, BeanProperty property, TypeSerializer vts, ValueSerializer<Object> valueSerializer) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(CharObjectMap<V> value, JsonGenerator g, SerializationContext ctxt) {
            value.forEachKeyValue((k, v) -> {
                g.writeName(String.valueOf(k));
                _serializeValue(v, g, ctxt);
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<CharObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                ValueSerializer<Object> valueSerializer
        ) {
            return new Char<>(_type, property, vts, valueSerializer);
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, CharObjectMap<V> value) {
            return value.isEmpty();
        }
    }

    /* endwith */
}
