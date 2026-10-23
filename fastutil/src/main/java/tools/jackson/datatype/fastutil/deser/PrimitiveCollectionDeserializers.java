package tools.jackson.datatype.fastutil.deser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.datatype.fastutil.util.ContentNulls;
import tools.jackson.datatype.fastutil.util.Merging;
import tools.jackson.datatype.fastutil.util.OptionalTypes;
import tools.jackson.datatype.primitive_collections_base.deser.BaseCharCollectionDeserializer;
import tools.jackson.datatype.primitive_collections_base.deser.BaseCollectionDeserializer;

import it.unimi.dsi.fastutil.booleans.*;
import it.unimi.dsi.fastutil.bytes.*;
import it.unimi.dsi.fastutil.chars.*;
import it.unimi.dsi.fastutil.doubles.*;
import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.shorts.*;

/**
 * Deserializers for fastutil collections of primitive values
 * ({@code IntCollection}, {@code LongList}, {@code DoubleSortedSet} and so on).
 */
public final class PrimitiveCollectionDeserializers
{
    private PrimitiveCollectionDeserializers() { }

    // Collection types of `fastutil-core` (a subset of `fastutil`) that are missing are
    // skipped: it has no `boolean` or `byte` collections, and only lists of `short`,
    // `char` and `float`
    private static final List<Support<?>> SUPPORTS = new ArrayList<>();

    static {
        _register(() -> new Support<>(new ContainerFamily<BooleanCollection>(BooleanCollection.class)
                .addIfPresent(f -> f.add(BooleanSet.class, BooleanOpenHashSet::new, BooleanArraySet::new))
                .addIfPresent(f -> f.add(BooleanBigList.class, BooleanBigArrayBigList::new, BooleanBigArrayBigList::new))
                .addIfPresent(f -> f.add(BooleanList.class, BooleanArrayList::new, BooleanArrayList::new))
                .addIfPresent(f -> f.add(BooleanCollection.class, BooleanArrayList::new, BooleanArrayList::new)),
                BooleanDeserializer::new));
        _register(() -> new Support<>(new ContainerFamily<ByteCollection>(ByteCollection.class)
                .addIfPresent(f -> f.add(ByteSortedSet.class, ByteRBTreeSet::new, ByteRBTreeSet::new))
                .addIfPresent(f -> f.add(ByteSet.class, ByteOpenHashSet::new, ByteLinkedOpenHashSet::new))
                .addIfPresent(f -> f.add(ByteBigList.class, ByteBigArrayBigList::new, ByteBigArrayBigList::new))
                .addIfPresent(f -> f.add(ByteList.class, ByteArrayList::new, ByteArrayList::new))
                .addIfPresent(f -> f.add(ByteCollection.class, ByteArrayList::new, ByteArrayList::new)),
                ByteDeserializer::new));
        _register(() -> new Support<>(new ContainerFamily<ShortCollection>(ShortCollection.class)
                .addIfPresent(f -> f.add(ShortSortedSet.class, ShortRBTreeSet::new, ShortRBTreeSet::new))
                .addIfPresent(f -> f.add(ShortSet.class, ShortOpenHashSet::new, ShortLinkedOpenHashSet::new))
                .addIfPresent(f -> f.add(ShortBigList.class, ShortBigArrayBigList::new, ShortBigArrayBigList::new))
                .addIfPresent(f -> f.add(ShortList.class, ShortArrayList::new, ShortArrayList::new))
                .addIfPresent(f -> f.add(ShortCollection.class, ShortArrayList::new, ShortArrayList::new)),
                ShortDeserializer::new));
        _register(() -> new Support<>(new ContainerFamily<CharCollection>(CharCollection.class)
                .addIfPresent(f -> f.add(CharSortedSet.class, CharRBTreeSet::new, CharRBTreeSet::new))
                .addIfPresent(f -> f.add(CharSet.class, CharOpenHashSet::new, CharLinkedOpenHashSet::new))
                .addIfPresent(f -> f.add(CharBigList.class, CharBigArrayBigList::new, CharBigArrayBigList::new))
                .addIfPresent(f -> f.add(CharList.class, CharArrayList::new, CharArrayList::new))
                .addIfPresent(f -> f.add(CharCollection.class, CharArrayList::new, CharArrayList::new)),
                (type, creator) -> new CharDeserializer(type.getRawClass(), creator)));
        _register(() -> new Support<>(new ContainerFamily<IntCollection>(IntCollection.class)
                .addIfPresent(f -> f.add(IntSortedSet.class, IntRBTreeSet::new, IntRBTreeSet::new))
                .addIfPresent(f -> f.add(IntSet.class, IntOpenHashSet::new, IntLinkedOpenHashSet::new))
                .addIfPresent(f -> f.add(IntBigList.class, IntBigArrayBigList::new, IntBigArrayBigList::new))
                .addIfPresent(f -> f.add(IntList.class, IntArrayList::new, IntArrayList::new))
                .addIfPresent(f -> f.add(IntCollection.class, IntArrayList::new, IntArrayList::new)),
                IntDeserializer::new));
        _register(() -> new Support<>(new ContainerFamily<LongCollection>(LongCollection.class)
                .addIfPresent(f -> f.add(LongSortedSet.class, LongRBTreeSet::new, LongRBTreeSet::new))
                .addIfPresent(f -> f.add(LongSet.class, LongOpenHashSet::new, LongLinkedOpenHashSet::new))
                .addIfPresent(f -> f.add(LongBigList.class, LongBigArrayBigList::new, LongBigArrayBigList::new))
                .addIfPresent(f -> f.add(LongList.class, LongArrayList::new, LongArrayList::new))
                .addIfPresent(f -> f.add(LongCollection.class, LongArrayList::new, LongArrayList::new)),
                LongDeserializer::new));
        _register(() -> new Support<>(new ContainerFamily<FloatCollection>(FloatCollection.class)
                .addIfPresent(f -> f.add(FloatSortedSet.class, FloatRBTreeSet::new, FloatRBTreeSet::new))
                .addIfPresent(f -> f.add(FloatSet.class, FloatOpenHashSet::new, FloatLinkedOpenHashSet::new))
                .addIfPresent(f -> f.add(FloatBigList.class, FloatBigArrayBigList::new, FloatBigArrayBigList::new))
                .addIfPresent(f -> f.add(FloatList.class, FloatArrayList::new, FloatArrayList::new))
                .addIfPresent(f -> f.add(FloatCollection.class, FloatArrayList::new, FloatArrayList::new)),
                FloatDeserializer::new));
        _register(() -> new Support<>(new ContainerFamily<DoubleCollection>(DoubleCollection.class)
                .addIfPresent(f -> f.add(DoubleSortedSet.class, DoubleRBTreeSet::new, DoubleRBTreeSet::new))
                .addIfPresent(f -> f.add(DoubleSet.class, DoubleOpenHashSet::new, DoubleLinkedOpenHashSet::new))
                .addIfPresent(f -> f.add(DoubleBigList.class, DoubleBigArrayBigList::new, DoubleBigArrayBigList::new))
                .addIfPresent(f -> f.add(DoubleList.class, DoubleArrayList::new, DoubleArrayList::new))
                .addIfPresent(f -> f.add(DoubleCollection.class, DoubleArrayList::new, DoubleArrayList::new)),
                DoubleDeserializer::new));
    }

    private static void _register(Supplier<Support<?>> support) {
        OptionalTypes.registerIfPresent(() -> SUPPORTS.add(support.get()));
    }

    /**
     * @return Whether given type is a fastutil collection of primitive values
     *    that can be constructed
     */
    public static boolean handles(Class<?> rawType) {
        for (Support<?> support : SUPPORTS) {
            if (support.family().handles(rawType)) {
                return support.family().findCreator(rawType) != null;
            }
        }
        return false;
    }

    /**
     * @param elementDeserializer Custom deserializer for elements, if any (like one
     *    specified with {@code @JsonDeserialize(contentUsing = ...)})
     *
     * @return Deserializer for given type if it is a fastutil collection of primitive
     *    values that can be constructed; {@code null} otherwise
     */
    public static ValueDeserializer<?> findDeserializer(DeserializationConfig config,
            JavaType type, ValueDeserializer<?> elementDeserializer)
    {
        for (Support<?> support : SUPPORTS) {
            if (support.family().handles(type.getRawClass())) {
                return support.findDeserializer(config, type, elementDeserializer);
            }
        }
        return null;
    }

    private record Support<C>(ContainerFamily<C> family,
            BiFunction<JavaType, ContainerFamily.Creator<C>, ValueDeserializer<?>> deserializerFactory)
    {
        ValueDeserializer<?> findDeserializer(DeserializationConfig config, JavaType type,
                ValueDeserializer<?> elementDeserializer)
        {
            ContainerFamily.Creator<C> creator = family.findCreator(type.getRawClass());
            if (creator == null) {
                return null;
            }
            if (elementDeserializer != null) {
                // Let standard Jackson handling use the custom deserializer (which
                // produces boxed values)
                // (handler needs to be set explicitly: resolved types are cached without one)
                return new BoxedDelegatingDeserializer<>(type, config.getTypeFactory()
                        .constructCollectionType(ArrayList.class, type.getContentType())
                        .withContentValueHandler(elementDeserializer), creator);
            }
            return deserializerFactory.apply(type, creator);
        }
    }

    /**
     * Base class for deserializers of primitive collections: elements are added
     * to an intermediate collection which is then converted to the result type.
     *<p>
     * Handles per-property settings the same way as {@code jackson-databind} does
     * for primitive arrays: {@code @JsonSetter(contentNulls = ...)} ({@code SKIP} and
     * {@code FAIL}; others handle {@code null}s as specified by
     * {@link tools.jackson.databind.DeserializationFeature#FAIL_ON_NULL_FOR_PRIMITIVES})
     * and {@code @JsonFormat(with = ACCEPT_SINGLE_VALUE_AS_ARRAY)}.
     */
    public abstract static class Base<C extends Collection<?>>
        extends BaseCollectionDeserializer<C, C>
    {
        protected final ContainerFamily.Creator<C> _creator;

        // Handler for JSON nulls as elements, if not the default one
        protected final NullValueProvider _nuller;

        // Per-property override for `ACCEPT_SINGLE_VALUE_AS_ARRAY`, if any
        protected final Boolean _unwrapSingle;

        protected Base(JavaType type, ContainerFamily.Creator<C> creator) {
            super(type);
            _creator = creator;
            _nuller = null;
            _unwrapSingle = null;
        }

        protected Base(Base<C> src, NullValueProvider nuller, Boolean unwrapSingle) {
            super(src.getValueType());
            _creator = src._creator;
            _nuller = nuller;
            _unwrapSingle = unwrapSingle;
        }

        protected abstract Base<C> withResolved(NullValueProvider nuller, Boolean unwrapSingle);

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
        {
            Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class,
                    JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            NullValueProvider nuller = ContentNulls.findNuller(ctxt, property, getValueType());
            if (Objects.equals(unwrapSingle, _unwrapSingle) && (nuller == _nuller)) {
                return this;
            }
            return withResolved(nuller, unwrapSingle);
        }

        @SuppressWarnings("unchecked")
        @Override
        public C deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            if (p.isExpectedStartArrayToken()) {
                return _deserializeContents(p, ctxt);
            }
            if ((_unwrapSingle == Boolean.TRUE) || ((_unwrapSingle == null)
                    && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY))) {
                return _deserializeFromSingleValue(p, ctxt);
            }
            return (C) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
        }

        @Override
        protected C _deserializeContents(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            C collection = createIntermediate(ctxt);
            JsonToken t;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                if ((t == JsonToken.VALUE_NULL) && (_nuller != null)) {
                    _nuller.getNullValue(ctxt); // fails, or skips
                    continue;
                }
                add(collection, p, ctxt);
            }
            return finish(ctxt, collection);
        }

        @Override
        protected C _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            C collection = createIntermediate(ctxt);
            if (p.hasToken(JsonToken.VALUE_NULL) && (_nuller != null)) {
                _nuller.getNullValue(ctxt);
            } else {
                add(collection, p, ctxt);
            }
            return finish(ctxt, collection);
        }

        @Override
        protected C createIntermediate() {
            return _creator.create(null);
        }

        @Override
        protected C createIntermediate(DeserializationContext ctxt) throws JacksonException {
            return _creator.create(ctxt);
        }

        @Override
        protected C finish(C intermediate) {
            return _creator.finish(null, intermediate);
        }

        @Override
        protected C finish(DeserializationContext ctxt, C intermediate) throws JacksonException {
            return _creator.finish(ctxt, intermediate);
        }

        // Merging (`@JsonMerge`, `ObjectReader.readerForUpdating()`) adds elements
        @Override
        public C deserialize(JsonParser p, DeserializationContext ctxt, C intoValue)
            throws JacksonException
        {
            return Merging.addAll(ctxt, intoValue, deserialize(p, ctxt));
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
    }

    public static final class BooleanDeserializer extends Base<BooleanCollection>
    {
        public BooleanDeserializer(JavaType type, ContainerFamily.Creator<BooleanCollection> creator) {
            super(type, creator);
        }

        private BooleanDeserializer(BooleanDeserializer src, NullValueProvider nuller, Boolean unwrapSingle) {
            super(src, nuller, unwrapSingle);
        }

        @Override
        protected BooleanDeserializer withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new BooleanDeserializer(this, nuller, unwrapSingle);
        }

        @Override
        protected void add(BooleanCollection intermediate, JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            intermediate.add(_parseBooleanPrimitive(p, ctxt));
        }
    }

    public static final class ByteDeserializer extends Base<ByteCollection>
    {
        public ByteDeserializer(JavaType type, ContainerFamily.Creator<ByteCollection> creator) {
            super(type, creator);
        }

        private ByteDeserializer(ByteDeserializer src, NullValueProvider nuller, Boolean unwrapSingle) {
            super(src, nuller, unwrapSingle);
        }

        @Override
        protected ByteDeserializer withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new ByteDeserializer(this, nuller, unwrapSingle);
        }

        @Override
        protected void add(ByteCollection intermediate, JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            intermediate.add(_parseBytePrimitive(p, ctxt));
        }
    }

    public static final class ShortDeserializer extends Base<ShortCollection>
    {
        public ShortDeserializer(JavaType type, ContainerFamily.Creator<ShortCollection> creator) {
            super(type, creator);
        }

        private ShortDeserializer(ShortDeserializer src, NullValueProvider nuller, Boolean unwrapSingle) {
            super(src, nuller, unwrapSingle);
        }

        @Override
        protected ShortDeserializer withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new ShortDeserializer(this, nuller, unwrapSingle);
        }

        @Override
        protected void add(ShortCollection intermediate, JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            intermediate.add(_parseShortPrimitive(p, ctxt));
        }
    }

    public static final class IntDeserializer extends Base<IntCollection>
    {
        public IntDeserializer(JavaType type, ContainerFamily.Creator<IntCollection> creator) {
            super(type, creator);
        }

        private IntDeserializer(IntDeserializer src, NullValueProvider nuller, Boolean unwrapSingle) {
            super(src, nuller, unwrapSingle);
        }

        @Override
        protected IntDeserializer withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new IntDeserializer(this, nuller, unwrapSingle);
        }

        @Override
        protected void add(IntCollection intermediate, JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            intermediate.add(_parseIntPrimitive(p, ctxt));
        }
    }

    public static final class LongDeserializer extends Base<LongCollection>
    {
        public LongDeserializer(JavaType type, ContainerFamily.Creator<LongCollection> creator) {
            super(type, creator);
        }

        private LongDeserializer(LongDeserializer src, NullValueProvider nuller, Boolean unwrapSingle) {
            super(src, nuller, unwrapSingle);
        }

        @Override
        protected LongDeserializer withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new LongDeserializer(this, nuller, unwrapSingle);
        }

        @Override
        protected void add(LongCollection intermediate, JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            intermediate.add(_parseLongPrimitive(p, ctxt));
        }
    }

    public static final class FloatDeserializer extends Base<FloatCollection>
    {
        public FloatDeserializer(JavaType type, ContainerFamily.Creator<FloatCollection> creator) {
            super(type, creator);
        }

        private FloatDeserializer(FloatDeserializer src, NullValueProvider nuller, Boolean unwrapSingle) {
            super(src, nuller, unwrapSingle);
        }

        @Override
        protected FloatDeserializer withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new FloatDeserializer(this, nuller, unwrapSingle);
        }

        @Override
        protected void add(FloatCollection intermediate, JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            intermediate.add(_parseFloatPrimitive(p, ctxt));
        }
    }

    public static final class DoubleDeserializer extends Base<DoubleCollection>
    {
        public DoubleDeserializer(JavaType type, ContainerFamily.Creator<DoubleCollection> creator) {
            super(type, creator);
        }

        private DoubleDeserializer(DoubleDeserializer src, NullValueProvider nuller, Boolean unwrapSingle) {
            super(src, nuller, unwrapSingle);
        }

        @Override
        protected DoubleDeserializer withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new DoubleDeserializer(this, nuller, unwrapSingle);
        }

        @Override
        protected void add(DoubleCollection intermediate, JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            intermediate.add(_parseDoublePrimitive(p, ctxt));
        }
    }

    /**
     * Char collections are read from a JSON String (as written by default), or from
     * a JSON Array of single-character Strings.
     */
    public static final class CharDeserializer
        extends BaseCharCollectionDeserializer<CharCollection, CharCollection>
    {
        private final ContainerFamily.Creator<CharCollection> _creator;

        // Handler for JSON nulls as elements of JSON Arrays, if not the default one
        private final NullValueProvider _nuller;

        public CharDeserializer(Class<?> rawType, ContainerFamily.Creator<CharCollection> creator) {
            this(rawType, creator, null);
        }

        private CharDeserializer(Class<?> rawType, ContainerFamily.Creator<CharCollection> creator,
                NullValueProvider nuller) {
            super(_cast(rawType));
            _creator = creator;
            _nuller = nuller;
        }

        @SuppressWarnings("unchecked")
        private static Class<? super CharCollection> _cast(Class<?> rawType) {
            return (Class<? super CharCollection>) rawType;
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
        {
            NullValueProvider nuller = ContentNulls.findNuller(ctxt, property,
                    ctxt.constructType(handledType()));
            if (nuller == _nuller) {
                return this;
            }
            return new CharDeserializer(handledType(), _creator, nuller);
        }

        @Override
        public CharCollection deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            if (p.isExpectedStartArrayToken()) {
                if (_nuller != null) {
                    return _deserializeArrayWithNuller(p, ctxt);
                }
            } else if (!p.hasToken(JsonToken.VALUE_STRING)) {
                return (CharCollection) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            }
            return super.deserialize(p, ctxt);
        }

        private CharCollection _deserializeArrayWithNuller(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            CharCollection collection = createIntermediate(ctxt);
            JsonToken t;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                if (t == JsonToken.VALUE_NULL) {
                    _nuller.getNullValue(ctxt); // fails, or skips
                    continue;
                }
                if (t != JsonToken.VALUE_STRING) {
                    return (CharCollection) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                }
                String str = p.getString();
                if (str.length() != 1) {
                    ctxt.reportInputMismatch(this,
                            "Cannot convert a JSON String of length %d into a char element of char array",
                            str.length());
                }
                collection.add(str.charAt(0));
            }
            return finish(ctxt, collection);
        }

        @Override
        protected CharCollection createIntermediate() {
            return _creator.create(null);
        }

        @Override
        protected CharCollection createIntermediate(DeserializationContext ctxt) throws JacksonException {
            return _creator.create(ctxt);
        }

        @Override
        protected CharCollection finish(CharCollection intermediate) {
            return _creator.finish(null, intermediate);
        }

        @Override
        protected CharCollection finish(DeserializationContext ctxt, CharCollection intermediate)
            throws JacksonException
        {
            return _creator.finish(ctxt, intermediate);
        }

        @Override
        public CharCollection deserialize(JsonParser p, DeserializationContext ctxt,
                CharCollection intoValue)
            throws JacksonException
        {
            return Merging.addAll(ctxt, intoValue, deserialize(p, ctxt));
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
        protected void add(CharCollection intermediate, char c) {
            intermediate.add(c);
        }

        @Override
        protected void addAll(CharCollection intermediate, char[] chars, int off, int len) {
            for (int i = off, end = off + len; i < end; ++i) {
                intermediate.add(chars[i]);
            }
        }
    }
}
