package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.ser.map.PrimitiveRefMapSerializer;
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
        public Char(JavaType type, BeanProperty property, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(CharObjectMap<V> value, JsonGenerator gen, SerializerProvider serializers)
        {
            value.forEachKeyValue((k, v) -> {
                gen.writeFieldName(String.valueOf(k));
                _serializeValue(v, gen, serializers);
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<CharObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                JsonSerializer<Object> valueSerializer
        ) {
            return new Char<>(_type, property, vts, valueSerializer);
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, CharObjectMap<V> value) {
            return value.isEmpty();
        }
    }

    /* endwith */
}
