package tools.jackson.datatype.hppc.ser;

import tools.jackson.core.*;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.*;
import tools.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsontype.TypeSerializer;

import com.carrotsearch.hppc.*;
import com.carrotsearch.hppc.procedures.DoubleProcedure;
import com.carrotsearch.hppc.procedures.FloatProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.procedures.ShortProcedure;

public class HppcContainerSerializers
{
    public final static ContainerSerializerBase<?>[] _primitiveSerializers =
        new ContainerSerializerBase<?>[] {
                new ByteContainerSerializer(),
                new ShortContainerSerializer(),
                new IntContainerSerializer(),
                new LongContainerSerializer(),
                new CharContainerSerializer(),
                new FloatContainerSerializer(),
                new DoubleContainerSerializer(),
                
                new BitSetSerializer()
        };

    /**
     * Method called to see if this serializer (or a serializer this serializer
     * knows) should be used for given type; if not, null is returned.
     */
    public static ValueSerializer<?> getMatchingSerializer(SerializationConfig config,
            JavaType type)
    {
        for (ContainerSerializerBase<?> ser : _primitiveSerializers) {
            ValueSerializer<?> actual = ser.getSerializer(type);
            if (actual != null) {
                return actual;
            }
        }
        return null;
    }        

    /*
    /**********************************************************************
    /* Concrete container implementations; basic integral types
    /**********************************************************************
     */

    /**
     * Byte containers are handled similar to byte[], meaning that they are
     * actually serialized as base64-encoded Strings by default
     *<p>
     * TODO: allow specifying other modes (serialize as array?)
     */
    static class ByteContainerSerializer
        extends ContainerSerializerBase<ByteContainer>
    {
        ByteContainerSerializer() {
            super(ByteContainer.class, "string"); // really, "binary", but...
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        {
            // Logical content byte array/stream, but physically a JSON String so:
            if (visitor != null) visitor.expectStringFormat(typeHint);
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, ByteContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(ByteContainer value) {
            return value.size() == 1;
        }

        @Override
        public void serialize(ByteContainer value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException
        {
            gen.assignCurrentValue(value);
            serializeContents(value, gen, ctxt);
        }
        
        @Override
        public void serializeWithType(ByteContainer value, JsonGenerator gen, SerializationContext ctxt,
                TypeSerializer typeSer)
            throws JacksonException
        {
            gen.assignCurrentValue(value);
            WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, ctxt,
                    typeSer.typeId(value, JsonToken.VALUE_EMBEDDED_OBJECT));
            serializeContents(value, gen, ctxt);
            typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
        }

        @Override
        protected void serializeContents(final ByteContainer value, final JsonGenerator gen, SerializationContext ctxt)
               throws JacksonException
        {
            if (value instanceof ByteArrayList list) {
                // Write straight from the backing buffer, avoiding a copy
                gen.writeBinary(list.buffer, 0, list.size());
                return;
            }
            gen.writeBinary(value.toArray());
        }
    }

    final static class ShortContainerSerializer
        extends ContainerSerializerBase<ShortContainer>
    {
        ShortContainerSerializer() {
            super(ShortContainer.class, "integer");
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, ShortContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(ShortContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }

        @Override
        protected void serializeContents(final ShortContainer value, final JsonGenerator gen, SerializationContext ctxt)
               throws JacksonException
        {
            if (value instanceof ShortIndexedContainer list) {
                for (int i = 0, len = list.size(); i < len; ++i) {
                    gen.writeNumber(list.get(i));
                }
                return;
            }
            value.forEach((ShortProcedure) v -> gen.writeNumber(v));
        }
    }

    final static class IntContainerSerializer
        extends ContainerSerializerBase<IntContainer>
    {
        IntContainerSerializer() {
            super(IntContainer.class, "integer");
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, IntContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(IntContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }

        @Override
        protected void serializeContents(final IntContainer value, final JsonGenerator gen, SerializationContext ctxt)
           throws JacksonException
        {
            if (value instanceof IntIndexedContainer list) {
                for (int i = 0, len = list.size(); i < len; ++i) {
                    gen.writeNumber(list.get(i));
                }
                return;
            }
            value.forEach((IntProcedure) v -> gen.writeNumber(v));
        }
    }

    final static class LongContainerSerializer
        extends ContainerSerializerBase<LongContainer>
    {
        LongContainerSerializer() {
            super(LongContainer.class, "integer");
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, LongContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(LongContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }

        @Override
        protected void serializeContents(final LongContainer value, final JsonGenerator gen, SerializationContext ctxt)
           throws JacksonException
        {
            if (value instanceof LongIndexedContainer list) {
                for (int i = 0, len = list.size(); i < len; ++i) {
                    gen.writeNumber(list.get(i));
                }
                return;
            }
            value.forEach((LongProcedure) v -> gen.writeNumber(v));
        }
    }

    /*
    /**********************************************************************
    /* Concrete container implementations; char
    /**********************************************************************
     */

    /**
     * This one is bit tricky: could serialize in multiple ways;
     * for example:
     *<ul>
     * <li>String that contains all characters (in order)</li>
     * <li>Array that contains single-character Strings</li>
     * <li>Array that contains numbers that represent character codes</li>
     *</ul>
     *
     * Let's start with the first option
     */
    final static class CharContainerSerializer
        extends ContainerSerializerBase<CharContainer>
    {
        CharContainerSerializer() {
            super(CharContainer.class, "string");
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, CharContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(CharContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        {
            // Logical content byte array/stream, but physically a JSON String so:
            if (visitor != null) visitor.expectStringFormat(typeHint);
        }
        
        @Override
        public void serialize(CharContainer value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException
        {
            gen.assignCurrentValue(value);
            serializeContents(value, gen, ctxt);
        }
        
        @Override
        public void serializeWithType(CharContainer value,
                JsonGenerator gen, SerializationContext ctxt,
                TypeSerializer typeSer)
            throws JacksonException
        {
            gen.assignCurrentValue(value);
            WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, ctxt,
                    typeSer.typeId(value, JsonToken.VALUE_STRING));
            serializeContents(value, gen, ctxt);
            typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
        }

        @Override
        protected void serializeContents(final CharContainer value,
                final JsonGenerator gen, SerializationContext ctxt)
               throws JacksonException
        {
            if (value instanceof CharArrayList list) {
                // Write straight from the backing buffer, avoiding a copy
                gen.writeString(list.buffer, 0, list.size());
                return;
            }
            char[] ch = value.toArray();
            gen.writeString(ch, 0, ch.length);
        }
    }
    
    /*
    /**********************************************************************
    /* Concrete container implementations; floating-point types
    /**********************************************************************
     */

    final static class FloatContainerSerializer
        extends ContainerSerializerBase<FloatContainer>
    {
        FloatContainerSerializer() {
            super(FloatContainer.class, "number");
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, FloatContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(FloatContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.NUMBER);
                }
            }
        }

        @Override
        protected void serializeContents(final FloatContainer value,
                final JsonGenerator gen, SerializationContext ctxt)
           throws JacksonException
        {
            if (value instanceof FloatIndexedContainer list) {
                for (int i = 0, len = list.size(); i < len; ++i) {
                    gen.writeNumber(list.get(i));
                }
                return;
            }
            value.forEach((FloatProcedure) v -> gen.writeNumber(v));
        }
    }

    final static class DoubleContainerSerializer
        extends ContainerSerializerBase<DoubleContainer>
    {
        DoubleContainerSerializer() {
            super(DoubleContainer.class, "number");
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, DoubleContainer value) {
            return value.isEmpty();
        }
        @Override
        public boolean hasSingleElement(DoubleContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.NUMBER);
                }
            }
        }

        @Override
        protected void serializeContents(final DoubleContainer value, final JsonGenerator gen, SerializationContext ctxt)
           throws JacksonException
        {
            if (value instanceof DoubleIndexedContainer list) {
                for (int i = 0, len = list.size(); i < len; ++i) {
                    gen.writeNumber(list.get(i));
                }
                return;
            }
            value.forEach((DoubleProcedure) v -> gen.writeNumber(v));
        }
    }

    /*
    /**********************************************************************
    /* Concrete container implementations, other
    /**********************************************************************
     */

    /**
     * The default implementation is not particularly efficient, as it outputs
     * things as an arrays of boolean values. There are many ways to achieve
     * more efficient storage; either by outputting ints/longs, or by using
     * base64 encoding. But for now this will work functionally correctly,
     * and we can optimize better later on, as need be.
     */
    final static class BitSetSerializer
        extends ContainerSerializerBase<BitSet>
    {
        BitSetSerializer() {
            super(BitSet.class, "boolean");
        }

        @Override
        public boolean isEmpty(SerializationContext ctxt, BitSet value) {
            return _lastNonZeroWord(value.bits) < 0;
        }

        @Override
        public boolean hasSingleElement(BitSet value) {
            return _lastNonZeroWord(value.bits) == 0 && value.bits[0] == 1L;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.BOOLEAN);
                }
            }
        }
        
        @Override
        protected void serializeContents(final BitSet value, final JsonGenerator gen, SerializationContext ctxt)
           throws JacksonException
        {
            // Write up to the highest set bit: `size()` is the allocated capacity
            // (a multiple of 64), which may be far beyond it. Scan `bits` directly
            // instead of calling `length()`: it only considers words below `wlen`
            // (which `getAndSet()` etc do not update) and may also modify `wlen`
            final long[] bits = value.bits;
            final int lastWord = _lastNonZeroWord(bits);
            for (int wi = 0; wi <= lastWord; ++wi) {
                final long word = bits[wi];
                final int bitCount = (wi == lastWord)
                        ? (64 - Long.numberOfLeadingZeros(word)) : 64;
                for (int b = 0; b < bitCount; ++b) {
                    gen.writeBoolean(((word >>> b) & 1L) != 0L);
                }
            }
        }

        // Index of the last non-zero word, or -1 if none
        private static int _lastNonZeroWord(long[] bits) {
            int i = bits.length;
            while (--i >= 0 && bits[i] == 0L) { }
            return i;
        }
    }
}
