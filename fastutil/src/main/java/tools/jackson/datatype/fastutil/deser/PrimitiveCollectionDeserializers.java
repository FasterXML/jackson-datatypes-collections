package tools.jackson.datatype.fastutil.deser;

import java.util.Collection;
import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
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

    private static final ContainerFamily<BooleanCollection> BOOLEANS =
            new ContainerFamily<BooleanCollection>(BooleanCollection.class)
                .add(BooleanSet.class, BooleanOpenHashSet::new, BooleanArraySet::new)
                .add(BooleanBigList.class, BooleanBigArrayBigList::new, BooleanBigArrayBigList::new)
                .add(BooleanList.class, BooleanArrayList::new, BooleanArrayList::new)
                .add(BooleanCollection.class, BooleanArrayList::new, BooleanArrayList::new);

    private static final ContainerFamily<ByteCollection> BYTES =
            new ContainerFamily<ByteCollection>(ByteCollection.class)
                .add(ByteSortedSet.class, ByteRBTreeSet::new, ByteRBTreeSet::new)
                .add(ByteSet.class, ByteOpenHashSet::new, ByteLinkedOpenHashSet::new)
                .add(ByteBigList.class, ByteBigArrayBigList::new, ByteBigArrayBigList::new)
                .add(ByteList.class, ByteArrayList::new, ByteArrayList::new)
                .add(ByteCollection.class, ByteArrayList::new, ByteArrayList::new);

    private static final ContainerFamily<ShortCollection> SHORTS =
            new ContainerFamily<ShortCollection>(ShortCollection.class)
                .add(ShortSortedSet.class, ShortRBTreeSet::new, ShortRBTreeSet::new)
                .add(ShortSet.class, ShortOpenHashSet::new, ShortLinkedOpenHashSet::new)
                .add(ShortBigList.class, ShortBigArrayBigList::new, ShortBigArrayBigList::new)
                .add(ShortList.class, ShortArrayList::new, ShortArrayList::new)
                .add(ShortCollection.class, ShortArrayList::new, ShortArrayList::new);

    private static final ContainerFamily<CharCollection> CHARS =
            new ContainerFamily<CharCollection>(CharCollection.class)
                .add(CharSortedSet.class, CharRBTreeSet::new, CharRBTreeSet::new)
                .add(CharSet.class, CharOpenHashSet::new, CharLinkedOpenHashSet::new)
                .add(CharBigList.class, CharBigArrayBigList::new, CharBigArrayBigList::new)
                .add(CharList.class, CharArrayList::new, CharArrayList::new)
                .add(CharCollection.class, CharArrayList::new, CharArrayList::new);

    private static final ContainerFamily<IntCollection> INTS =
            new ContainerFamily<IntCollection>(IntCollection.class)
                .add(IntSortedSet.class, IntRBTreeSet::new, IntRBTreeSet::new)
                .add(IntSet.class, IntOpenHashSet::new, IntLinkedOpenHashSet::new)
                .add(IntBigList.class, IntBigArrayBigList::new, IntBigArrayBigList::new)
                .add(IntList.class, IntArrayList::new, IntArrayList::new)
                .add(IntCollection.class, IntArrayList::new, IntArrayList::new);

    private static final ContainerFamily<LongCollection> LONGS =
            new ContainerFamily<LongCollection>(LongCollection.class)
                .add(LongSortedSet.class, LongRBTreeSet::new, LongRBTreeSet::new)
                .add(LongSet.class, LongOpenHashSet::new, LongLinkedOpenHashSet::new)
                .add(LongBigList.class, LongBigArrayBigList::new, LongBigArrayBigList::new)
                .add(LongList.class, LongArrayList::new, LongArrayList::new)
                .add(LongCollection.class, LongArrayList::new, LongArrayList::new);

    private static final ContainerFamily<FloatCollection> FLOATS =
            new ContainerFamily<FloatCollection>(FloatCollection.class)
                .add(FloatSortedSet.class, FloatRBTreeSet::new, FloatRBTreeSet::new)
                .add(FloatSet.class, FloatOpenHashSet::new, FloatLinkedOpenHashSet::new)
                .add(FloatBigList.class, FloatBigArrayBigList::new, FloatBigArrayBigList::new)
                .add(FloatList.class, FloatArrayList::new, FloatArrayList::new)
                .add(FloatCollection.class, FloatArrayList::new, FloatArrayList::new);

    private static final ContainerFamily<DoubleCollection> DOUBLES =
            new ContainerFamily<DoubleCollection>(DoubleCollection.class)
                .add(DoubleSortedSet.class, DoubleRBTreeSet::new, DoubleRBTreeSet::new)
                .add(DoubleSet.class, DoubleOpenHashSet::new, DoubleLinkedOpenHashSet::new)
                .add(DoubleBigList.class, DoubleBigArrayBigList::new, DoubleBigArrayBigList::new)
                .add(DoubleList.class, DoubleArrayList::new, DoubleArrayList::new)
                .add(DoubleCollection.class, DoubleArrayList::new, DoubleArrayList::new);

    private static final List<ContainerFamily<?>> FAMILIES = List.of(
            BOOLEANS, BYTES, SHORTS, CHARS, INTS, LONGS, FLOATS, DOUBLES);

    /**
     * @return Whether given type is a fastutil collection of primitive values
     */
    public static boolean handles(Class<?> rawType) {
        for (ContainerFamily<?> family : FAMILIES) {
            if (family.handles(rawType)) {
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
        final Class<?> raw = type.getRawClass();
        if (BOOLEANS.handles(raw)) {
            ContainerFamily.Creator<BooleanCollection> creator = BOOLEANS.findCreator(raw);
            return (creator == null) ? null : new BooleanDeserializer(type, creator);
        }
        if (BYTES.handles(raw)) {
            ContainerFamily.Creator<ByteCollection> creator = BYTES.findCreator(raw);
            return (creator == null) ? null : new ByteDeserializer(type, creator);
        }
        if (SHORTS.handles(raw)) {
            ContainerFamily.Creator<ShortCollection> creator = SHORTS.findCreator(raw);
            return (creator == null) ? null : new ShortDeserializer(type, creator);
        }
        if (CHARS.handles(raw)) {
            ContainerFamily.Creator<CharCollection> creator = CHARS.findCreator(raw);
            return (creator == null) ? null : new CharDeserializer(raw, creator);
        }
        if (INTS.handles(raw)) {
            ContainerFamily.Creator<IntCollection> creator = INTS.findCreator(raw);
            return (creator == null) ? null : new IntDeserializer(type, creator);
        }
        if (LONGS.handles(raw)) {
            ContainerFamily.Creator<LongCollection> creator = LONGS.findCreator(raw);
            return (creator == null) ? null : new LongDeserializer(type, creator);
        }
        if (FLOATS.handles(raw)) {
            ContainerFamily.Creator<FloatCollection> creator = FLOATS.findCreator(raw);
            return (creator == null) ? null : new FloatDeserializer(type, creator);
        }
        if (DOUBLES.handles(raw)) {
            ContainerFamily.Creator<DoubleCollection> creator = DOUBLES.findCreator(raw);
            return (creator == null) ? null : new DoubleDeserializer(type, creator);
        }
        return null;
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
