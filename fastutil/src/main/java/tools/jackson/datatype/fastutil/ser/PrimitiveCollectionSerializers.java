package tools.jackson.datatype.fastutil.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.datatype.fastutil.util.OptionalTypes;
import tools.jackson.datatype.primitive_collections_base.ser.PrimitiveIterableSerializer;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

/**
 * Serializers for fastutil collections of primitive values, which avoid boxing
 * of elements. Collections are written as JSON Arrays, except for {@code char}
 * collections that are written as JSON Strings (unless
 * {@link SerializationFeature#WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS} is enabled).
 */
public final class PrimitiveCollectionSerializers
{
    private final static TypeFactory TYPE_F = TypeFactory.createDefaultInstance();

    private PrimitiveCollectionSerializers() { }

    // Collection types missing from `fastutil-core` (a subset of `fastutil`, without
    // `boolean` or `byte` collections) are skipped
    private static final List<Entry> ENTRIES = new ArrayList<>();

    static {
        _register(() -> new Entry(BooleanCollection.class, () -> new BooleanSerializer(null, null)));
        _register(() -> new Entry(ByteCollection.class, () -> new ByteSerializer(null, null)));
        _register(() -> new Entry(ShortCollection.class, () -> new ShortSerializer(null, null)));
        _register(() -> new Entry(CharCollection.class, () -> CharSerializer.INSTANCE));
        _register(() -> new Entry(IntCollection.class, () -> new IntSerializer(null, null)));
        _register(() -> new Entry(LongCollection.class, () -> new LongSerializer(null, null)));
        _register(() -> new Entry(FloatCollection.class, () -> new FloatSerializer(null, null)));
        _register(() -> new Entry(DoubleCollection.class, () -> new DoubleSerializer(null, null)));
    }

    private static void _register(Supplier<Entry> entry) {
        OptionalTypes.registerIfPresent(() -> ENTRIES.add(entry.get()));
    }

    private record Entry(Class<?> collectionType, Supplier<ValueSerializer<?>> serializer) { }

    /**
     * @return Serializer for given type if it is a fastutil collection of primitive
     *    values; {@code null} otherwise
     */
    public static ValueSerializer<?> findSerializer(Class<?> rawType)
    {
        for (Entry entry : ENTRIES) {
            if (entry.collectionType().isAssignableFrom(rawType)) {
                return entry.serializer().get();
            }
        }
        return null;
    }

    public abstract static class Base<C extends Collection<?>>
        extends PrimitiveIterableSerializer<C>
    {
        protected Base(Class<C> type, Class<?> rawElementType,
                BeanProperty property, Boolean unwrapSingle) {
            super(type, TYPE_F.constructType(rawElementType), property, unwrapSingle);
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, C value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(C value) {
            return (value != null) && (value.size() == 1);
        }
    }

    public static final class BooleanSerializer extends Base<BooleanCollection>
    {
        public BooleanSerializer(BeanProperty property, Boolean unwrapSingle) {
            super(BooleanCollection.class, boolean.class, property, unwrapSingle);
        }

        @Override
        protected void serializeContents(BooleanCollection value, JsonGenerator g)
            throws JacksonException
        {
            for (BooleanIterator it = value.iterator(); it.hasNext(); ) {
                g.writeBoolean(it.nextBoolean());
            }
        }
    }

    public static final class ByteSerializer extends Base<ByteCollection>
    {
        public ByteSerializer(BeanProperty property, Boolean unwrapSingle) {
            super(ByteCollection.class, byte.class, property, unwrapSingle);
        }

        @Override
        protected void serializeContents(ByteCollection value, JsonGenerator g)
            throws JacksonException
        {
            for (ByteIterator it = value.iterator(); it.hasNext(); ) {
                g.writeNumber(it.nextByte());
            }
        }
    }

    public static final class ShortSerializer extends Base<ShortCollection>
    {
        public ShortSerializer(BeanProperty property, Boolean unwrapSingle) {
            super(ShortCollection.class, short.class, property, unwrapSingle);
        }

        @Override
        protected void serializeContents(ShortCollection value, JsonGenerator g)
            throws JacksonException
        {
            for (ShortIterator it = value.iterator(); it.hasNext(); ) {
                g.writeNumber(it.nextShort());
            }
        }
    }

    public static final class IntSerializer extends Base<IntCollection>
    {
        public IntSerializer(BeanProperty property, Boolean unwrapSingle) {
            super(IntCollection.class, int.class, property, unwrapSingle);
        }

        @Override
        protected void serializeContents(IntCollection value, JsonGenerator g)
            throws JacksonException
        {
            for (IntIterator it = value.iterator(); it.hasNext(); ) {
                g.writeNumber(it.nextInt());
            }
        }
    }

    public static final class LongSerializer extends Base<LongCollection>
    {
        public LongSerializer(BeanProperty property, Boolean unwrapSingle) {
            super(LongCollection.class, long.class, property, unwrapSingle);
        }

        @Override
        protected void serializeContents(LongCollection value, JsonGenerator g)
            throws JacksonException
        {
            for (LongIterator it = value.iterator(); it.hasNext(); ) {
                g.writeNumber(it.nextLong());
            }
        }
    }

    public static final class FloatSerializer extends Base<FloatCollection>
    {
        public FloatSerializer(BeanProperty property, Boolean unwrapSingle) {
            super(FloatCollection.class, float.class, property, unwrapSingle);
        }

        @Override
        protected void serializeContents(FloatCollection value, JsonGenerator g)
            throws JacksonException
        {
            for (FloatIterator it = value.iterator(); it.hasNext(); ) {
                g.writeNumber(it.nextFloat());
            }
        }
    }

    public static final class DoubleSerializer extends Base<DoubleCollection>
    {
        public DoubleSerializer(BeanProperty property, Boolean unwrapSingle) {
            super(DoubleCollection.class, double.class, property, unwrapSingle);
        }

        @Override
        protected void serializeContents(DoubleCollection value, JsonGenerator g)
            throws JacksonException
        {
            for (DoubleIterator it = value.iterator(); it.hasNext(); ) {
                g.writeNumber(it.nextDouble());
            }
        }
    }

    /**
     * Writes {@code char} collections as JSON Strings, or as JSON Arrays of
     * single-character Strings if
     * {@link SerializationFeature#WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS} is enabled.
     */
    public static final class CharSerializer extends StdSerializer<CharCollection>
    {
        public static final CharSerializer INSTANCE = new CharSerializer();

        private CharSerializer() {
            super(CharCollection.class);
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, CharCollection value) {
            return value.isEmpty();
        }

        @Override
        public void serialize(CharCollection value, JsonGenerator g, SerializationContext ctxt)
            throws JacksonException
        {
            if (ctxt.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                g.writeStartArray(value);
                _writeContentsAsArray(value, g);
                g.writeEndArray();
            } else {
                char[] chars = value.toCharArray();
                g.writeString(chars, 0, chars.length);
            }
        }

        @Override
        public void serializeWithType(CharCollection value, JsonGenerator g,
                SerializationContext ctxt, TypeSerializer typeSer)
            throws JacksonException
        {
            g.assignCurrentValue(value);
            WritableTypeId typeIdDef;
            if (ctxt.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                typeIdDef = typeSer.writeTypePrefix(g, ctxt, typeSer.typeId(value, JsonToken.START_ARRAY));
                _writeContentsAsArray(value, g);
            } else {
                typeIdDef = typeSer.writeTypePrefix(g, ctxt, typeSer.typeId(value, JsonToken.VALUE_STRING));
                char[] chars = value.toCharArray();
                g.writeString(chars, 0, chars.length);
            }
            typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
        }

        private void _writeContentsAsArray(CharCollection value, JsonGenerator g)
            throws JacksonException
        {
            char[] buf = new char[1];
            for (CharIterator it = value.iterator(); it.hasNext(); ) {
                buf[0] = it.nextChar();
                g.writeString(buf, 0, 1);
            }
        }
    }
}
