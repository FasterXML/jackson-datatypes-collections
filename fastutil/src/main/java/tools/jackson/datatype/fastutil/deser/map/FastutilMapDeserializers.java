package tools.jackson.datatype.fastutil.deser.map;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.type.MapLikeType;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.datatype.fastutil.deser.BoxedDelegatingDeserializer;
import tools.jackson.datatype.fastutil.deser.ContainerFamily;
import tools.jackson.datatype.fastutil.util.ContentNulls;
import tools.jackson.datatype.fastutil.util.EmptyStrings;
import tools.jackson.datatype.fastutil.util.Merging;
import tools.jackson.datatype.fastutil.util.OptionalTypes;
import tools.jackson.datatype.primitive_collections_base.deser.map.KeyHandler;
import tools.jackson.datatype.primitive_collections_base.deser.map.MapDeserializer;
import tools.jackson.datatype.primitive_collections_base.deser.map.RefKeyHandler;
import tools.jackson.datatype.primitive_collections_base.deser.map.RefValueHandler;
import tools.jackson.datatype.primitive_collections_base.deser.map.TypeHandlerPair;
import tools.jackson.datatype.primitive_collections_base.deser.map.ValueHandler;

import it.unimi.dsi.fastutil.objects.*;

/**
 * Deserializers for fastutil maps that have primitive keys, primitive values or both
 * ({@code Int2LongMap}, {@code Object2IntMap}, {@code Long2ObjectMap} and so on).
 * Maps with both reference keys and values are handled by standard Jackson
 * {@code Map} deserializers.
 */
public final class FastutilMapDeserializers
{
    private static final List<Entry<?, ?, ?>> ENTRIES = new ArrayList<>();

    // Maps with identity-based reference keys and primitive values, read as the
    // matching (insertion-ordered) `Object2XMap`, so that values are handled the same way
    private static final List<ReferenceEntry> REFERENCE_ENTRIES = new ArrayList<>();

    static {
        TypeHandlerPairs.register();

        // Not included in `fastutil-core`
        _addReference(() -> new ReferenceEntry(new ContainerFamily<Map<?, ?>>(Reference2BooleanMap.class)
                .add(Reference2BooleanSortedMap.class, Reference2BooleanLinkedOpenHashMap::new, Reference2BooleanLinkedOpenHashMap::new)
                .add(Reference2BooleanMap.class, Reference2BooleanOpenHashMap::new, Reference2BooleanLinkedOpenHashMap::new),
                Object2BooleanLinkedOpenHashMap.class));
        _addReference(() -> new ReferenceEntry(new ContainerFamily<Map<?, ?>>(Reference2ByteMap.class)
                .add(Reference2ByteSortedMap.class, Reference2ByteLinkedOpenHashMap::new, Reference2ByteLinkedOpenHashMap::new)
                .add(Reference2ByteMap.class, Reference2ByteOpenHashMap::new, Reference2ByteLinkedOpenHashMap::new),
                Object2ByteLinkedOpenHashMap.class));
        _addReference(() -> new ReferenceEntry(new ContainerFamily<Map<?, ?>>(Reference2ShortMap.class)
                .add(Reference2ShortSortedMap.class, Reference2ShortLinkedOpenHashMap::new, Reference2ShortLinkedOpenHashMap::new)
                .add(Reference2ShortMap.class, Reference2ShortOpenHashMap::new, Reference2ShortLinkedOpenHashMap::new),
                Object2ShortLinkedOpenHashMap.class));
        _addReference(() -> new ReferenceEntry(new ContainerFamily<Map<?, ?>>(Reference2CharMap.class)
                .add(Reference2CharSortedMap.class, Reference2CharLinkedOpenHashMap::new, Reference2CharLinkedOpenHashMap::new)
                .add(Reference2CharMap.class, Reference2CharOpenHashMap::new, Reference2CharLinkedOpenHashMap::new),
                Object2CharLinkedOpenHashMap.class));
        _addReference(() -> new ReferenceEntry(new ContainerFamily<Map<?, ?>>(Reference2IntMap.class)
                .add(Reference2IntSortedMap.class, Reference2IntLinkedOpenHashMap::new, Reference2IntLinkedOpenHashMap::new)
                .add(Reference2IntMap.class, Reference2IntOpenHashMap::new, Reference2IntLinkedOpenHashMap::new),
                Object2IntLinkedOpenHashMap.class));
        _addReference(() -> new ReferenceEntry(new ContainerFamily<Map<?, ?>>(Reference2LongMap.class)
                .add(Reference2LongSortedMap.class, Reference2LongLinkedOpenHashMap::new, Reference2LongLinkedOpenHashMap::new)
                .add(Reference2LongMap.class, Reference2LongOpenHashMap::new, Reference2LongLinkedOpenHashMap::new),
                Object2LongLinkedOpenHashMap.class));
        _addReference(() -> new ReferenceEntry(new ContainerFamily<Map<?, ?>>(Reference2FloatMap.class)
                .add(Reference2FloatSortedMap.class, Reference2FloatLinkedOpenHashMap::new, Reference2FloatLinkedOpenHashMap::new)
                .add(Reference2FloatMap.class, Reference2FloatOpenHashMap::new, Reference2FloatLinkedOpenHashMap::new),
                Object2FloatLinkedOpenHashMap.class));
        _addReference(() -> new ReferenceEntry(new ContainerFamily<Map<?, ?>>(Reference2DoubleMap.class)
                .add(Reference2DoubleSortedMap.class, Reference2DoubleLinkedOpenHashMap::new, Reference2DoubleLinkedOpenHashMap::new)
                .add(Reference2DoubleMap.class, Reference2DoubleOpenHashMap::new, Reference2DoubleLinkedOpenHashMap::new),
                Object2DoubleLinkedOpenHashMap.class));
    }

    private static void _addReference(Supplier<ReferenceEntry> entry) {
        OptionalTypes.registerIfPresent(() -> REFERENCE_ENTRIES.add(entry.get()));
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
        ContainerFamily<?> family = _findFamily(rawType);
        return (family != null) && (family.findCreator(rawType) != null);
    }

    /**
     * @return Deserializer for given type if it is a fastutil map with primitive keys
     *    and/or values that can be constructed; {@code null} otherwise
     */
    public static ValueDeserializer<?> findDeserializer(DeserializationConfig config, MapType type,
            KeyDeserializer keyDeserializer,
            TypeDeserializer valueTypeDeserializer, ValueDeserializer<?> valueDeserializer)
    {
        final Class<?> rawType = type.getRawClass();
        Entry<?, ?, ?> entry = _findEntry(rawType);
        if (entry != null) {
            return entry.createDeserializer(config, type, keyDeserializer,
                    valueTypeDeserializer, valueDeserializer);
        }
        for (ReferenceEntry refEntry : REFERENCE_ENTRIES) {
            if (refEntry.family().handles(rawType)) {
                ContainerFamily.Creator<Map<?, ?>> creator = refEntry.family().findCreator(rawType);
                if (creator == null) {
                    return null;
                }
                JavaType delegateType;
                if (valueDeserializer == null) {
                    delegateType = config.getTypeFactory()
                            .constructParametricType(refEntry.delegateType(), type.getKeyType());
                    if (keyDeserializer != null) {
                        delegateType = ((MapLikeType) delegateType).withKeyValueHandler(keyDeserializer);
                    }
                } else { // custom deserializer for primitive values
                    delegateType = _boxedMapType(config, type, keyDeserializer, valueDeserializer);
                }
                return new BoxedDelegatingDeserializer<>(type, delegateType, creator);
            }
        }
        return null;
    }

    /**
     * @return Type of {@code LinkedHashMap} with same key and value types as given
     *    map type, using given custom deserializers (that need to be set explicitly:
     *    resolved types are cached without them)
     */
    static JavaType _boxedMapType(DeserializationConfig config, MapType type,
            KeyDeserializer keyDeserializer, ValueDeserializer<?> valueDeserializer)
    {
        MapLikeType boxed = config.getTypeFactory()
                .constructMapType(LinkedHashMap.class, type.getKeyType(), type.getContentType());
        if (keyDeserializer != null) {
            boxed = boxed.withKeyValueHandler(keyDeserializer);
        }
        if (valueDeserializer != null) {
            boxed = boxed.withContentValueHandler(valueDeserializer);
        }
        return boxed;
    }

    private static ContainerFamily<?> _findFamily(Class<?> rawType) {
        Entry<?, ?, ?> entry = _findEntry(rawType);
        if (entry != null) {
            return entry.family();
        }
        for (ReferenceEntry refEntry : REFERENCE_ENTRIES) {
            if (refEntry.family().handles(rawType)) {
                return refEntry.family();
            }
        }
        return null;
    }

    private static Entry<?, ?, ?> _findEntry(Class<?> rawType) {
        for (Entry<?, ?, ?> entry : ENTRIES) {
            if (entry.family().handles(rawType)) {
                return entry;
            }
        }
        return null;
    }

    private record ReferenceEntry(ContainerFamily<Map<?, ?>> family, Class<?> delegateType) { }

    private record Entry<M, K extends KeyHandler<K>, V extends ValueHandler<V>>(
            boolean refKey, boolean refValue,
            ContainerFamily<M> family, TypeHandlerPair<M, K, V> typeHandlerPair)
    {
        @SuppressWarnings("unchecked")
        ValueDeserializer<?> createDeserializer(DeserializationConfig config, MapType type,
                KeyDeserializer keyDeserializer,
                TypeDeserializer valueTypeDeserializer, ValueDeserializer<?> valueDeserializer)
        {
            final ContainerFamily.Creator<M> creator = family.findCreator(type.getRawClass());
            if (creator == null) {
                return null;
            }
            // Custom deserializers for primitive keys or values produce boxed values:
            // let standard Jackson handling use them
            if ((!refKey && keyDeserializer != null) || (!refValue && valueDeserializer != null)) {
                return new BoxedDelegatingDeserializer<>(type,
                        _boxedMapType(config, type, keyDeserializer, valueDeserializer), creator);
            }
            K keyHandler = refKey
                    ? (K) new RefKeyHandler(type.getKeyType(), keyDeserializer)
                    : typeHandlerPair.keyHandler(type.getKeyType());
            V valueHandler = refValue
                    ? (V) new RefValueHandler(type.getContentType(), valueDeserializer, valueTypeDeserializer)
                    : typeHandlerPair.valueHandler(type.getContentType());
            return new FastutilMapDeserializer<>(type, keyHandler, valueHandler,
                    typeHandlerPair, creator, null);
        }
    }

    /**
     * Creates maps using {@link ContainerFamily.Creator} (instead of
     * {@link TypeHandlerPair#createEmpty}) since the map type to create depends on
     * the requested type, not just on key and value types.
     *<p>
     * Also handles {@code @JsonSetter(contentNulls = SKIP / FAIL)} for values.
     */
    static final class FastutilMapDeserializer<M, K extends KeyHandler<K>, V extends ValueHandler<V>>
        extends MapDeserializer<M, M, K, V>
    {
        private final JavaType _mapType;
        private final K _keyHandler;
        private final V _valueHandler;
        private final TypeHandlerPair<M, K, V> _typeHandlerPair;
        private final ContainerFamily.Creator<M> _creator;

        // Handler for JSON nulls as values, if not the default one
        private final NullValueProvider _nuller;

        FastutilMapDeserializer(JavaType mapType, K keyHandler, V valueHandler,
                TypeHandlerPair<M, K, V> typeHandlerPair, ContainerFamily.Creator<M> creator,
                NullValueProvider nuller) {
            super(mapType.getRawClass(), keyHandler, valueHandler, typeHandlerPair,
                    (ctxt, intermediate) -> creator.finish(ctxt, intermediate));
            _mapType = mapType;
            _keyHandler = keyHandler;
            _valueHandler = valueHandler;
            _typeHandlerPair = typeHandlerPair;
            _creator = creator;
            _nuller = nuller;
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
        {
            FastutilMapDeserializer<M, K, V> deser = (FastutilMapDeserializer<M, K, V>)
                    super.createContextual(ctxt, property);
            NullValueProvider nuller = ContentNulls.findNuller(ctxt, property, _mapType);
            if (nuller == deser._nuller) {
                return deser;
            }
            return new FastutilMapDeserializer<>(_mapType, deser._keyHandler, deser._valueHandler,
                    _typeHandlerPair, _creator, nuller);
        }

        @Override
        protected M createIntermediate() {
            return _creator.create(null);
        }

        @Override
        protected M createIntermediate(DeserializationContext ctxt) throws JacksonException {
            return _creator.create(ctxt);
        }

        @Override
        protected void deserializeEntry(M target, DeserializationContext ctxt, String key,
                JsonParser valueParser)
            throws JacksonException
        {
            if ((_nuller != null) && valueParser.hasToken(JsonToken.VALUE_NULL)) {
                _nuller.getNullValue(ctxt); // fails, or skips
                return;
            }
            super.deserializeEntry(target, ctxt, key, valueParser);
        }

        @SuppressWarnings("unchecked")
        @Override
        public M deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            // Empty String may be coerced (like for `java.util` maps)
            CoercionAction act = EmptyStrings.findCoercion(p, ctxt, this);
            if (act != null) {
                return (M) EmptyStrings.coercedValue(ctxt, this, act);
            }
            return super.deserialize(p, ctxt);
        }

        @Override
        public LogicalType logicalType() {
            return LogicalType.Map;
        }

        // Merging (`@JsonMerge`, `ObjectReader.readerForUpdating()`) adds entries,
        // replacing (not merging) existing values
        @Override
        public M deserialize(JsonParser p, DeserializationContext ctxt, M intoValue)
            throws JacksonException
        {
            return Merging.putAll(ctxt, intoValue, (Map<?, ?>) deserialize(p, ctxt));
        }

        @Override
        public Boolean supportsUpdate(DeserializationConfig config) {
            return Boolean.TRUE;
        }

        @Override
        public Object getEmptyValue(DeserializationContext ctxt) {
            return _creator.createEmpty(ctxt);
        }

        @Override
        public AccessPattern getEmptyAccessPattern() {
            return AccessPattern.DYNAMIC;
        }

        @Override
        protected MapDeserializer<M, ?, ?, ?> withResolved(K keyHandler, V valueHandler) {
            return new FastutilMapDeserializer<>(_mapType, keyHandler, valueHandler,
                    _typeHandlerPair, _creator, _nuller);
        }
    }
}
