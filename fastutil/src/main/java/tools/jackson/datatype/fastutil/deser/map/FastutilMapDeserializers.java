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
                if (valueDeserializer != null) { // custom deserializer for primitive values
                    return new BoxedDelegatingDeserializer<>(type,
                            _boxedMapType(config, type, keyDeserializer, valueDeserializer), creator,
                            false, true);
                }
                Entry<?, ?, ?> intermediateEntry = _findEntry(refEntry.intermediateType());
                if (intermediateEntry == null) { // never happens: registered together
                    return null;
                }
                return _createReferenceDeserializer(intermediateEntry, creator,
                        refEntry.intermediateType(), type, keyDeserializer);
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

    /**
     * Maps with identity-based reference keys and primitive values: populated as
     * an (insertion-ordered) {@code Object2XMap} of given type, then copied.
     */
    private record ReferenceEntry(ContainerFamily<Map<?, ?>> family, Class<?> intermediateType) { }

    private static <M> ValueDeserializer<?> _createReferenceDeserializer(Entry<M, ?, ?> entry,
            ContainerFamily.Creator<Map<?, ?>> creator, Class<?> intermediateType,
            MapType type, KeyDeserializer keyDeserializer)
    {
        ContainerFamily.Creator<M> intermediate = entry.family().findCreator(intermediateType);
        return entry.createDeserializer(type, keyDeserializer, null, null,
                new CopyingConstruction<>(intermediate, creator));
    }

    private record Entry<M, K extends KeyHandler<K>, V extends ValueHandler<V>>(
            boolean refKey, boolean refValue,
            ContainerFamily<M> family, TypeHandlerPair<M, K, V> typeHandlerPair)
    {
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
                        _boxedMapType(config, type, keyDeserializer, valueDeserializer), creator,
                        !refKey, !refValue);
            }
            return createDeserializer(type, keyDeserializer, valueTypeDeserializer, valueDeserializer,
                    new DirectConstruction<>(creator));
        }

        @SuppressWarnings("unchecked")
        <T> ValueDeserializer<?> createDeserializer(MapType type, KeyDeserializer keyDeserializer,
                TypeDeserializer valueTypeDeserializer, ValueDeserializer<?> valueDeserializer,
                Construction<T, M> construction)
        {
            K keyHandler = refKey
                    ? (K) new RefKeyHandler(type.getKeyType(), keyDeserializer)
                    : typeHandlerPair.keyHandler(type.getKeyType());
            V valueHandler = refValue
                    ? (V) new RefValueHandler(type.getContentType(), valueDeserializer, valueTypeDeserializer)
                    : typeHandlerPair.valueHandler(type.getContentType());
            return new FastutilMapDeserializer<>(type, keyHandler, valueHandler,
                    typeHandlerPair, construction, null);
        }
    }

    /**
     * How maps are constructed: an intermediate map is populated, then converted into
     * the result. Maps to create depend on the requested type (not just on key and value
     * types), so {@link TypeHandlerPair#createEmpty} is not used.
     *
     * @param <T> Type of maps returned
     * @param <I> Type of intermediate maps populated
     */
    interface Construction<T, I>
    {
        /**
         * @param ctxt Context for reporting failures; may be {@code null}
         */
        I createIntermediate(DeserializationContext ctxt) throws JacksonException;

        T finish(DeserializationContext ctxt, I intermediate) throws JacksonException;

        /**
         * @return Whether existing maps (when merging) can be populated directly,
         *    that is, whether intermediate and result types are the same
         */
        boolean canPopulateExisting();
    }

    // Intermediate map is the result (possibly wrapped, or copied by `Creator`)
    private record DirectConstruction<M>(ContainerFamily.Creator<M> creator)
        implements Construction<M, M>
    {
        @Override
        public M createIntermediate(DeserializationContext ctxt) throws JacksonException {
            return creator.create(ctxt);
        }

        @Override
        public M finish(DeserializationContext ctxt, M intermediate) throws JacksonException {
            return creator.finish(ctxt, intermediate);
        }

        @Override
        public boolean canPopulateExisting() {
            return true;
        }
    }

    // Intermediate map (of a different type) is copied into the result
    private record CopyingConstruction<I>(ContainerFamily.Creator<I> intermediate,
            ContainerFamily.Creator<Map<?, ?>> result)
        implements Construction<Map<?, ?>, I>
    {
        @Override
        public I createIntermediate(DeserializationContext ctxt) throws JacksonException {
            return intermediate.create(ctxt);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map<?, ?> finish(DeserializationContext ctxt, I populated) throws JacksonException {
            Map<?, ?> map = result.create(ctxt);
            ((Map<Object, Object>) map).putAll((Map<?, ?>) populated);
            return result.finish(ctxt, map);
        }

        @Override
        public boolean canPopulateExisting() {
            return false;
        }
    }

    /**
     * Deserializer that creates maps using given {@link Construction}.
     *<p>
     * Also handles {@code @JsonSetter(contentNulls = SKIP / FAIL)} for values,
     * coercion of empty Strings and merging.
     */
    static final class FastutilMapDeserializer<T, I, K extends KeyHandler<K>, V extends ValueHandler<V>>
        extends MapDeserializer<T, I, K, V>
    {
        private final JavaType _mapType;
        private final Construction<T, I> _construction;

        // Handler for JSON nulls as values, if not the default one
        private final NullValueProvider _nuller;

        FastutilMapDeserializer(JavaType mapType, K keyHandler, V valueHandler,
                TypeHandlerPair<I, K, V> typeHandlerPair, Construction<T, I> construction,
                NullValueProvider nuller) {
            super(mapType.getRawClass(), keyHandler, valueHandler, typeHandlerPair,
                    construction::finish);
            _mapType = mapType;
            _construction = construction;
            _nuller = nuller;
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
        {
            FastutilMapDeserializer<T, I, K, V> deser = (FastutilMapDeserializer<T, I, K, V>)
                    super.createContextual(ctxt, property);
            NullValueProvider nuller = ContentNulls.findNuller(ctxt, property, _mapType);
            if (nuller == deser._nuller) {
                return deser;
            }
            return new FastutilMapDeserializer<>(_mapType, deser._keyHandler, deser._valueHandler,
                    _typeHandlerPair, _construction, nuller);
        }

        @Override
        protected MapDeserializer<T, ?, ?, ?> withResolved(K keyHandler, V valueHandler) {
            return new FastutilMapDeserializer<>(_mapType, keyHandler, valueHandler,
                    _typeHandlerPair, _construction, _nuller);
        }

        @Override
        protected I createIntermediate() {
            return _construction.createIntermediate(null);
        }

        @Override
        protected I createIntermediate(DeserializationContext ctxt) throws JacksonException {
            return _construction.createIntermediate(ctxt);
        }

        @Override
        protected void deserializeEntry(I target, DeserializationContext ctxt, String key,
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
        public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            // Empty String may be coerced (like for `java.util` maps)
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                return (T) EmptyStrings.deserializeFromString(p, ctxt, this);
            }
            return super.deserialize(p, ctxt);
        }

        // Merging (`@JsonMerge`, `ObjectReader.readerForUpdating()`) adds entries,
        // replacing (not merging) existing values
        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt, T intoValue)
            throws JacksonException
        {
            if (_construction.canPopulateExisting()) {
                JsonToken t = p.currentToken();
                if (t == JsonToken.START_OBJECT) {
                    t = p.nextToken();
                }
                if (t == JsonToken.PROPERTY_NAME || t == JsonToken.END_OBJECT) {
                    try {
                        deserializeEntries(p, ctxt, (I) intoValue);
                    } catch (UnsupportedOperationException e) {
                        throw Merging.failure(ctxt, intoValue, e);
                    }
                    return intoValue;
                }
            }
            return Merging.putAll(ctxt, intoValue, (Map<?, ?>) deserialize(p, ctxt));
        }

        @Override
        public Boolean supportsUpdate(DeserializationConfig config) {
            return Boolean.TRUE;
        }

        @Override
        public Object getEmptyValue(DeserializationContext ctxt) {
            return _construction.finish(ctxt, _construction.createIntermediate(ctxt));
        }

        @Override
        public AccessPattern getEmptyAccessPattern() {
            return AccessPattern.DYNAMIC;
        }

        @Override
        public LogicalType logicalType() {
            return LogicalType.Map;
        }
    }
}
