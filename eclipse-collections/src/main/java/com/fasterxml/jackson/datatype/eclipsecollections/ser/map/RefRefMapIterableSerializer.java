package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.ser.map.RefRefMapSerializer;
import org.eclipse.collections.api.map.MapIterable;

import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author yawkat
 */
public final class RefRefMapIterableSerializer extends RefRefMapSerializer<MapIterable<?, ?>>
{
    private static final long serialVersionUID = 4L;

    public RefRefMapIterableSerializer(
            JavaType type,
            JsonSerializer<Object> keySerializer, TypeSerializer vts, JsonSerializer<Object> valueSerializer,
            Set<String> ignoredEntries
    ) {
        super(type, MapIterable.class, keySerializer, vts, valueSerializer, ignoredEntries);
    }

    protected RefRefMapIterableSerializer(
            RefRefMapIterableSerializer src, BeanProperty property,
            JsonSerializer<?> keySerializer, TypeSerializer vts, JsonSerializer<?> valueSerializer,
            Set<String> ignoredEntries
    ) {
        super(src, property, keySerializer, vts, valueSerializer, ignoredEntries);
    }

    @Override
    protected RefRefMapIterableSerializer withResolved(
            BeanProperty property,
            JsonSerializer<?> keySer, TypeSerializer vts, JsonSerializer<?> valueSer,
            Set<String> ignored
    ) {
        return new RefRefMapIterableSerializer(this, property, keySer, vts, valueSer, ignored);
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer) {
        return new RefRefMapIterableSerializer(this, _property, _keySerializer,
                                       typeSer, _valueSerializer, _ignoredEntries);
    }

    @Override
    public boolean hasSingleElement(MapIterable<?, ?> map) {
        return map.size() == 1;
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, MapIterable<?, ?> value) {
        return value.isEmpty();
    }

    @Override
    protected void forEachKeyValue(MapIterable<?, ?> value, BiConsumer<Object, Object> action) {
        value.forEachKeyValue(action::accept);
    }
}
