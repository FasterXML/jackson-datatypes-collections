package tools.jackson.datatype.eclipsecollections.deser.map;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.datatype.primitive_collections_base.deser.map.KeyHandler;
import tools.jackson.datatype.primitive_collections_base.deser.map.MapDeserializer;
import tools.jackson.datatype.primitive_collections_base.deser.map.TypeHandlerPair;
import tools.jackson.datatype.primitive_collections_base.deser.map.ValueHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class EclipseMapDeserializers
{
    private static final Map<Class<?>, Entry<?, ?, ?, ?>> ENTRIES = new HashMap<>();

    public static MapDeserializer<?, ?, ?, ?> createDeserializer(JavaType type) {
        Class<?> rawClass = type.getRawClass();
        Entry<?, ?, ?, ?> entry = ENTRIES.get(rawClass);
        if (entry == null) {
            return null;
        }

        return entry.createDeserializer(type);
    }

    @SuppressWarnings("unused") // Used from TypeHandlerPairs
    static <T, I> void add(
            boolean refKey, boolean refValue, Class<T> type,
            TypeHandlerPair<I, ?, ?> handlerPair, Function<I, T> finish
    ) {
        if (ENTRIES.putIfAbsent(type, new Entry<>(refKey, refValue, handlerPair, finish)) != null) {
            throw new IllegalStateException();
        }
    }

    private static final class Entry<T, I, K extends KeyHandler<K>, V extends ValueHandler<V>> {
        private final boolean refKey;
        private final boolean refValue;
        final TypeHandlerPair<I, K, V> typeHandlerPair;
        // null if this is the identity function
        final Function<I, T> finish;

        Entry(boolean refKey, boolean refValue,
                TypeHandlerPair<I, K, V> typeHandlerPair, Function<I, T> finish) {
            this.refKey = refKey;
            this.refValue = refValue;
            this.typeHandlerPair = Objects.requireNonNull(typeHandlerPair);
            this.finish = finish;
        }

        MapDeserializer<T, I, K, V> createDeserializer(JavaType type) {
            List<JavaType> typeParameters = null;
            TypeBindings bindings = type.getBindings();
            if (bindings != null) {
                typeParameters = bindings.getTypeParameters();
            }
            JavaType keyType;
            JavaType valueType;
            if (typeParameters != null && !typeParameters.isEmpty()) {
                if (refKey && refValue) {
                    if (typeParameters.size() != 2) {
                        throw new IllegalStateException(
                                "type parameters: " + typeParameters + ", expected exactly two");
                    }
                } else if (refKey || refValue) {
                    if (typeParameters.size() != 1) {
                        throw new IllegalStateException(
                                "type parameters: " + typeParameters + ", expected exactly one");
                    }
                }
                keyType = refKey ? typeParameters.get(0) : null;
                valueType = refValue ? typeParameters.get(typeParameters.size() - 1) : null;
            } else {
                // `type` is a raw type
                keyType = TypeFactory.unknownType();
                valueType = TypeFactory.unknownType();
            }
            return MapDeserializer.create(keyType, valueType, typeHandlerPair, finish);
        }
    }

    static {
        TypeHandlerPairs.addDeserializers();
    }

    private EclipseMapDeserializers() {
    }
}
