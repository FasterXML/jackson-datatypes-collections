package com.fasterxml.jackson.datatype.eclipsecollections.deser.map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.map.KeyHandler;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.map.MapDeserializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.map.TypeHandlerPair;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.map.ValueHandler;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.primitive.PrimitiveObjectMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author yawkat
 */
public final class EclipseMapDeserializers {
    private static final Map<Class<?>, Entry<?, ?, ?, ?>> ENTRIES = new HashMap<>();

    private EclipseMapDeserializers() {
    }

    public static MapDeserializer<?, ?, ?, ?> createDeserializer(JavaType type) {
        Class<?> rawClass = type.getRawClass();
        Entry<?, ?, ?, ?> entry = ENTRIES.get(rawClass);
        if (entry == null) { return null; }

        return entry.createDeserializer(type);
    }

    @SuppressWarnings("unused") // Used from TypeHandlerPairs
    static <T, K extends KeyHandler<K>, V extends ValueHandler<V>> void add(
            Class<T> type,
            TypeHandlerPair<? extends T, K, V> handlerPair
    ) {
        ENTRIES.put(type, new Entry<>(handlerPair, null));
    }

    @SuppressWarnings("unused") // Used from TypeHandlerPairs
    static <T, I> void add(
            Class<T> type,
            TypeHandlerPair<I, ?, ?> handlerPair,
            Function<I, T> finish
    ) {
        ENTRIES.put(type, new Entry<>(handlerPair, finish));
    }

    private static final class Entry<T, I, K extends KeyHandler<K>, V extends ValueHandler<V>> {
        final TypeHandlerPair<I, K, V> typeHandlerPair;
        // null if this is the identity function
        final Function<I, T> finish;

        Entry(TypeHandlerPair<I, K, V> typeHandlerPair, Function<I, T> finish) {
            this.typeHandlerPair = typeHandlerPair;
            this.finish = finish;
        }

        MapDeserializer<T, I, K, V> createDeserializer(JavaType type) {
            Class<?> rawClass = type.getRawClass();
            List<JavaType> typeParameters = type.getBindings().getTypeParameters();
            boolean refValue = PrimitiveObjectMap.class.isAssignableFrom(rawClass) ||
                               MapIterable.class.isAssignableFrom(rawClass);
            boolean refKey = refValue ? (typeParameters.size() == 2) : (typeParameters.size() == 1);

            K keyHandler = typeHandlerPair.keyHandler(refKey ? typeParameters.get(0) : null);
            V valueHandler = typeHandlerPair.valueHandler(refValue ? typeParameters.get(typeParameters.size() - 1) : null);

            return new MapDeserializer<>(keyHandler, valueHandler, typeHandlerPair, finish);
        }
    }

    static {
        TypeHandlerPairs.addDeserializers();
    }
}
