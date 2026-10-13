package tools.jackson.datatype.fastutil.deser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
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
     */
    public static boolean handles(Class<?> rawType) {
        for (Support<?> support : SUPPORTS) {
            if (support.family().handles(rawType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Deserializer for given type if it is a fastutil collection of primitive
     *    values that can be constructed; {@code null} otherwise
     */
    public static ValueDeserializer<?> findDeserializer(JavaType type)
    {
        for (Support<?> support : SUPPORTS) {
            if (support.family().handles(type.getRawClass())) {
                return support.findDeserializer(type);
            }
        }
        return null;
    }

    private record Support<C>(ContainerFamily<C> family,
            BiFunction<JavaType, ContainerFamily.Creator<C>, ValueDeserializer<?>> deserializerFactory)
    {
        ValueDeserializer<?> findDeserializer(JavaType type) {
            ContainerFamily.Creator<C> creator = family.findCreator(type.getRawClass());
            return (creator == null) ? null : deserializerFactory.apply(type, creator);
        }
    }

    /**
     * Base class for deserializers of primitive collections: elements are added
     * to an intermediate collection which is then converted to the result type.
     */
    public abstract static class Base<C extends Collection<?>>
        extends BaseCollectionDeserializer<C, C>
    {
        protected final ContainerFamily.Creator<C> _creator;

        protected Base(JavaType type, ContainerFamily.Creator<C> creator) {
            super(type);
            _creator = creator;
        }

        @Override
        protected C createIntermediate() {
            return _creator.create();
        }

        @Override
        protected C finish(C intermediate) {
            return _creator.finish(intermediate);
        }
    }

    public static final class BooleanDeserializer extends Base<BooleanCollection>
    {
        public BooleanDeserializer(JavaType type, ContainerFamily.Creator<BooleanCollection> creator) {
            super(type, creator);
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

        public CharDeserializer(Class<?> rawType, ContainerFamily.Creator<CharCollection> creator) {
            super(_cast(rawType));
            _creator = creator;
        }

        @SuppressWarnings("unchecked")
        private static Class<? super CharCollection> _cast(Class<?> rawType) {
            return (Class<? super CharCollection>) rawType;
        }

        @Override
        public CharCollection deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            if (!p.isExpectedStartArrayToken() && !p.hasToken(JsonToken.VALUE_STRING)) {
                return (CharCollection) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            }
            return super.deserialize(p, ctxt);
        }

        @Override
        protected CharCollection createIntermediate() {
            return _creator.create();
        }

        @Override
        protected CharCollection finish(CharCollection intermediate) {
            return _creator.finish(intermediate);
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
