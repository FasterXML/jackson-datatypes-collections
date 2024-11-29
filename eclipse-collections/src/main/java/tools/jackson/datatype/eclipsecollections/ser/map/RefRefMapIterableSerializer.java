package tools.jackson.datatype.eclipsecollections.ser.map;

import java.util.Set;
import java.util.function.BiConsumer;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdContainerSerializer;
import tools.jackson.datatype.primitive_collections_base.ser.map.RefRefMapSerializer;

import org.eclipse.collections.api.map.MapIterable;

/**
 * @author yawkat
 */
public final class RefRefMapIterableSerializer extends RefRefMapSerializer<MapIterable<?, ?>>
{
    public RefRefMapIterableSerializer(
            JavaType type,
            ValueSerializer<Object> keySerializer, TypeSerializer vts, ValueSerializer<Object> valueSerializer,
            Set<String> ignoredEntries
    ) {
        super(type, MapIterable.class, keySerializer, vts, valueSerializer, ignoredEntries);
    }

    protected RefRefMapIterableSerializer(
            RefRefMapIterableSerializer src, BeanProperty property,
            ValueSerializer<?> keySerializer, TypeSerializer vts, ValueSerializer<?> valueSerializer,
            Set<String> ignoredEntries
    ) {
        super(src, property, keySerializer, vts, valueSerializer, ignoredEntries);
    }

    @Override
    protected RefRefMapIterableSerializer withResolved(
            BeanProperty property,
            ValueSerializer<?> keySer, TypeSerializer vts, ValueSerializer<?> valueSer,
            Set<String> ignored
    ) {
        return new RefRefMapIterableSerializer(this, property, keySer, vts, valueSer, ignored);
    }

    @Override
    protected StdContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer) {
        return new RefRefMapIterableSerializer(this, _property, _keySerializer,
                                       typeSer, _valueSerializer, _ignoredEntries);
    }

    @Override
    public boolean hasSingleElement(MapIterable<?, ?> map) {
        return map.size() == 1;
    }

    @Override
    public boolean isEmpty(SerializationContext ctxt, MapIterable<?, ?> value) {
        return value.isEmpty();
    }

    @Override
    protected void forEachKeyValue(MapIterable<?, ?> value, BiConsumer<Object, Object> action) {
        value.forEachKeyValue(action::accept);
    }
}
