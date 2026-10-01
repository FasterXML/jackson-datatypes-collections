package tools.jackson.datatype.fastutil.ser.map;

import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.*;

import tools.jackson.datatype.primitive_collections_base.ser.map.RefPrimitiveMapSerializer;

import it.unimi.dsi.fastutil.objects.*;

/**
 * Serializers for fastutil maps with reference keys and primitive values
 * (like {@code Object2IntMap}).
 */
@SuppressWarnings({ "Duplicates", "NewClassNamingConvention" })
public final class RefPrimitiveMapSerializers
{
    private RefPrimitiveMapSerializers() {
    }

    /* with char|boolean|byte|short|int|float|long|double value */

    public static final class Char<K> extends RefPrimitiveMapSerializer<Object2CharMap<K>, K>
    {
        public Char(JavaType type, BeanProperty property, ValueSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<Object2CharMap<K>, K> withResolved(
                BeanProperty property, ValueSerializer<Object> keySerializer
        ) {
            return new Char<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(Object2CharMap<K> value, JsonGenerator g, SerializationContext ctxt)
        {
            Object2CharMaps.fastForEach(value, e -> {
                _serializeKey(e.getKey(), g, ctxt);
                /* if !(char|boolean value) //
                g.writeNumber(e.getCharValue());
                /* elif char value */
                g.writeString(new char[]{e.getCharValue()}, 0, 1);
                /* elif boolean value //
                g.writeBoolean(e.getCharValue());
                // endif */
            });
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, Object2CharMap<K> value) {
            return value.isEmpty();
        }
    }

    /* endwith */
}
