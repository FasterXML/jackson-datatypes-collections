package tools.jackson.datatype.fastutil.deser.map;

import java.util.ArrayList;
import java.util.List;

import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapType;
import tools.jackson.datatype.fastutil.deser.ContainerFamily;
import tools.jackson.datatype.primitive_collections_base.deser.map.KeyHandler;
import tools.jackson.datatype.primitive_collections_base.deser.map.MapDeserializer;
import tools.jackson.datatype.primitive_collections_base.deser.map.RefKeyHandler;
import tools.jackson.datatype.primitive_collections_base.deser.map.RefValueHandler;
import tools.jackson.datatype.primitive_collections_base.deser.map.TypeHandlerPair;
import tools.jackson.datatype.primitive_collections_base.deser.map.ValueHandler;

/**
 * Deserializers for fastutil maps that have primitive keys, primitive values or both
 * ({@code Int2LongMap}, {@code Object2IntMap}, {@code Long2ObjectMap} and so on).
 * Maps with both reference keys and values are handled by standard Jackson
 * {@code Map} deserializers.
 */
public final class FastutilMapDeserializers
{
    private static final List<Entry<?, ?, ?>> ENTRIES = new ArrayList<>();

    static {
        TypeHandlerPairs.register();
    }

    private FastutilMapDeserializers() { }

    static <M, K extends KeyHandler<K>, V extends ValueHandler<V>> void add(
            boolean refKey, boolean refValue,
            ContainerFamily<M> family, TypeHandlerPair<M, K, V> handlerPair)
    {
        ENTRIES.add(new Entry<>(refKey, refValue, family, handlerPair));
    }

    /**
     * @return Whether given type is a fastutil map with primitive keys and/or values
     *    that can be constructed
     */
    public static boolean handles(Class<?> rawType) {
        Entry<?, ?, ?> entry = _findEntry(rawType);
        return (entry != null) && (entry.family().findCreator(rawType) != null);
    }

    /**
     * @return Deserializer for given type if it is a fastutil map with primitive keys
     *    and/or values that can be constructed; {@code null} otherwise
     */
    public static ValueDeserializer<?> findDeserializer(MapType type,
            KeyDeserializer keyDeserializer,
            TypeDeserializer valueTypeDeserializer, ValueDeserializer<?> valueDeserializer)
    {
        Entry<?, ?, ?> entry = _findEntry(type.getRawClass());
        if (entry == null) {
            return null;
        }
        return entry.createDeserializer(type, keyDeserializer,
                valueTypeDeserializer, valueDeserializer);
    }

    private static Entry<?, ?, ?> _findEntry(Class<?> rawType) {
        for (Entry<?, ?, ?> entry : ENTRIES) {
            if (entry.family().handles(rawType)) {
                return entry;
            }
        }
        return null;
    }

    private record Entry<M, K extends KeyHandler<K>, V extends ValueHandler<V>>(
            boolean refKey, boolean refValue,
            ContainerFamily<M> family, TypeHandlerPair<M, K, V> typeHandlerPair)
    {
        @SuppressWarnings("unchecked")
        ValueDeserializer<?> createDeserializer(MapType type, KeyDeserializer keyDeserializer,
                TypeDeserializer valueTypeDeserializer, ValueDeserializer<?> valueDeserializer)
        {
            final ContainerFamily.Creator<M> creator = family.findCreator(type.getRawClass());
            if (creator == null) {
                return null;
            }
            K keyHandler = refKey
                    ? (K) new RefKeyHandler(type.getKeyType(), keyDeserializer)
                    : typeHandlerPair.keyHandler(type.getKeyType());
            V valueHandler = refValue
                    ? (V) new RefValueHandler(type.getContentType(), valueDeserializer, valueTypeDeserializer)
                    : typeHandlerPair.valueHandler(type.getContentType());
            return new FastutilMapDeserializer<>(type.getRawClass(), keyHandler, valueHandler,
                    typeHandlerPair, creator);
        }
    }

    /**
     * Creates maps using {@link ContainerFamily.Creator} (instead of
     * {@link TypeHandlerPair#createEmpty}) since the map type to create depends on
     * the requested type, not just on key and value types.
     */
    static final class FastutilMapDeserializer<M, K extends KeyHandler<K>, V extends ValueHandler<V>>
        extends MapDeserializer<M, M, K, V>
    {
        private final TypeHandlerPair<M, K, V> _typeHandlerPair;
        private final ContainerFamily.Creator<M> _creator;

        FastutilMapDeserializer(Class<?> mapType, K keyHandler, V valueHandler,
                TypeHandlerPair<M, K, V> typeHandlerPair, ContainerFamily.Creator<M> creator) {
            super(mapType, keyHandler, valueHandler, typeHandlerPair,
                    (ctxt, intermediate) -> creator.finish(intermediate));
            _typeHandlerPair = typeHandlerPair;
            _creator = creator;
        }

        @Override
        protected M createIntermediate() {
            return _creator.create();
        }

        @Override
        protected MapDeserializer<M, ?, ?, ?> withResolved(K keyHandler, V valueHandler) {
            return new FastutilMapDeserializer<>(handledType(), keyHandler, valueHandler,
                    _typeHandlerPair, _creator);
        }
    }
}
