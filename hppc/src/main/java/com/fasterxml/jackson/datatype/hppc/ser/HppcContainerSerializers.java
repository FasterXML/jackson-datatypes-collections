package com.fasterxml.jackson.datatype.hppc.ser;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.carrotsearch.hppc.*;
import com.carrotsearch.hppc.predicates.*;

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
    public static JsonSerializer<?> getMatchingSerializer(SerializationConfig config,
            JavaType type)
    {
        for (ContainerSerializerBase<?> ser : _primitiveSerializers) {
            JsonSerializer<?> actual = ser.getSerializer(type);
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
        private static final long serialVersionUID = 1L;

        ByteContainerSerializer() {
            super(ByteContainer.class, "string"); // really, "binary", but...
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("string", true);
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            // Logical content byte array/stream, but physically a JSON String so:
            if (visitor != null) visitor.expectStringFormat(typeHint);
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, ByteContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(ByteContainer value) {
            return value.size() == 1;
        }

        @Override
        public void serialize(ByteContainer value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException
        {
            serializeContents(value, jgen, provider);
        }
        
        @Override
        public void serializeWithType(ByteContainer value, JsonGenerator jgen, SerializerProvider provider,
                TypeSerializer typeSer)
            throws IOException
        {
            // will be a JSON String, so can't use array prefix/suffix
            typeSer.writeTypePrefixForScalar(value, jgen);
            serializeContents(value, jgen, provider);
            typeSer.writeTypeSuffixForScalar(value, jgen);
        }
        @Override
        protected void serializeContents(final ByteContainer value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
            byte[] bytes = value.toArray();
            jgen.writeBinary(bytes);
        }
    }

    final static class ShortContainerSerializer
        extends ContainerSerializerBase<ShortContainer>
    {
        private static final long serialVersionUID = 1L;

        ShortContainerSerializer() {
            super(ShortContainer.class, "integer");
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, ShortContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(ShortContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
                throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }

        @Override
        protected void serializeContents(final ShortContainer value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException
        {
            if (value instanceof ShortIndexedContainer) {
                ShortIndexedContainer list = (ShortIndexedContainer) value;
                for (int i = 0, len = list.size(); i < len; ++i) {
                    jgen.writeNumber(list.get(i));
                }
                return;
            }
            final ExceptionHolder holder = new ExceptionHolder();
            value.forEach(new ShortPredicate() {
                @Override
                public boolean apply(short v) {
                    try {
                        jgen.writeNumber(v);
                    } catch (IOException e) {
                        holder.assignException(e);
                        return false;
                    }
                    return true;
                }
            });
            holder.throwHeld();
        }
    }

    /**
     * Handler for HPPC containers that store int values.
     * Specific in that we actually implement separate optimal serializer
     * for indexed type, given how common this type is.
     */
    static class IntContainerSerializer
        extends ContainerSerializerBase<IntContainer>
    {
        private static final long serialVersionUID = 1L;

        IntContainerSerializer() {
            super(IntContainer.class, "integer");
        }

        // Overridden to allow use of more optimized serialized for indexed variant
        @Override
        public JsonSerializer<?> getSerializer(JavaType type)
        {
            JsonSerializer<?> ser = super.getSerializer(type);
            if (ser != null) {
                if (IntIndexedContainer.class.isAssignableFrom(type.getClass())) {
                    return new Indexed();
                }
            }
            return ser;
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, IntContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(IntContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
                throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }

        @Override
        protected void serializeContents(final IntContainer value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
            // doh. Can't throw checked exceptions through; hence need convoluted handling...
            final ExceptionHolder holder = new ExceptionHolder();
            value.forEach(new IntPredicate() {
                @Override
                public boolean apply(int v) {
                    try {
                        jgen.writeNumber(v);
                    } catch (IOException e) {
                        holder.assignException(e);
                        return false;
                    }
                    return true;
                }
            });
            holder.throwHeld();
        }

        // Specialized variant to support indexed int container with more efficient accessor
        static class Indexed extends ContainerSerializerBase<IntIndexedContainer>
        {
            private static final long serialVersionUID = 1L;

            Indexed() {
                super(IntIndexedContainer.class, "integer");
            }

            @Override
            public boolean isEmpty(SerializerProvider provider, IntIndexedContainer value) {
                return value.isEmpty();
            }

            @Override
            public boolean hasSingleElement(IntIndexedContainer value) {
                return value.size() == 1;
            }

            @Override
            public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
                    throws JsonMappingException
            {
                if (visitor != null) {
                    JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                    if (v2 != null) {
                        v2.itemsFormat(JsonFormatTypes.INTEGER);
                    }
                }
            }
            
            @Override
            protected void serializeContents(final IntIndexedContainer value, final JsonGenerator jgen, SerializerProvider provider)
                   throws IOException, JsonGenerationException
            {
                int[] array;
                if (value instanceof IntArrayList) {
                    array = ((IntArrayList) value).buffer;
                } else {
                    array = value.toArray();
                }
                for (int i = 0, len = value.size(); i < len; ++i) {
                    jgen.writeNumber(array[i]);
                }
                return;
            }
        }
        
    }

    final static class LongContainerSerializer
        extends ContainerSerializerBase<LongContainer>
    {
        private static final long serialVersionUID = 1L;

        LongContainerSerializer() {
            super(LongContainer.class, "integer");
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, LongContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(LongContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
                throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
        }

        @Override
        protected void serializeContents(final LongContainer value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
            if (value instanceof LongIndexedContainer) {
                LongIndexedContainer list = (LongIndexedContainer) value;
                long[] array;
                if (value instanceof LongArrayList) {
                    array = ((LongArrayList) value).buffer;
                } else {
                    array = list.toArray();
                }
                for (int i = 0, len = list.size(); i < len; ++i) {
                    jgen.writeNumber(array[i]);
                }
                return;
            }
            // doh. Can't throw checked exceptions through; hence need convoluted handling...
            final ExceptionHolder holder = new ExceptionHolder();
            value.forEach(new LongPredicate() {
                @Override
                public boolean apply(long v) {
                    try {
                        jgen.writeNumber(v);
                    } catch (IOException e) {
                        holder.assignException(e);
                        return false;
                    }
                    return true;
                }
            });
            holder.throwHeld();
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
        private static final long serialVersionUID = 1L;

        CharContainerSerializer() {
            super(CharContainer.class, "string");
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, CharContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(CharContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException {
            // Logical content byte array/stream, but physically a JSON String so:
            if (visitor != null) visitor.expectStringFormat(typeHint);
        }
        
        @Override
        public void serialize(CharContainer value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException
        {
            serializeContents(value, jgen, provider);
        }
        
        @Override
        public void serializeWithType(CharContainer value, JsonGenerator jgen, SerializerProvider provider,
                TypeSerializer typeSer)
            throws IOException
        {
            // will be a JSON String, so can't use array prefix/suffix
            typeSer.writeTypePrefixForScalar(value, jgen);
            serializeContents(value, jgen, provider);
            typeSer.writeTypeSuffixForScalar(value, jgen);
        }

        @Override
        protected void serializeContents(final CharContainer value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
            char[] ch = value.toArray();
            jgen.writeString(ch, 0, ch.length);
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
        private static final long serialVersionUID = 1L;

        FloatContainerSerializer() {
            super(FloatContainer.class, "number");
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, FloatContainer value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(FloatContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.NUMBER);
                }
            }
        }

        @Override
        protected void serializeContents(final FloatContainer value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
            if (value instanceof FloatIndexedContainer) {
                FloatIndexedContainer list = (FloatIndexedContainer) value;
                for (int i = 0, len = list.size(); i < len; ++i) {
                    jgen.writeNumber(list.get(i));
                }
                return;
            }
            // doh. Can't throw checked exceptions through; hence need convoluted handling...
            final ExceptionHolder holder = new ExceptionHolder();
            value.forEach(new FloatPredicate() {
                @Override
                public boolean apply(float v) {
                    try {
                        jgen.writeNumber(v);
                    } catch (IOException e) {
                        holder.assignException(e);
                        return false;
                    }
                    return true;
                }
            });
            holder.throwHeld();
        }
    }

    final static class DoubleContainerSerializer
        extends ContainerSerializerBase<DoubleContainer>
    {
        private static final long serialVersionUID = 1L;

        DoubleContainerSerializer() {
            super(DoubleContainer.class, "number");
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, DoubleContainer value) {
            return value.isEmpty();
        }
        @Override
        public boolean hasSingleElement(DoubleContainer value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.NUMBER);
                }
            }
        }

        @Override
        protected void serializeContents(final DoubleContainer value, final JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonGenerationException
        {
            if (value instanceof DoubleIndexedContainer) {
                DoubleIndexedContainer list = (DoubleIndexedContainer) value;
                for (int i = 0, len = list.size(); i < len; ++i) {
                    jgen.writeNumber(list.get(i));
                }
                return;
            }
            // doh. Can't throw checked exceptions through; hence need convoluted handling...
            final ExceptionHolder holder = new ExceptionHolder();
            value.forEach(new DoublePredicate() {
                @Override
                public boolean apply(double v) {
                    try {
                        jgen.writeNumber(v);
                    } catch (IOException e) {
                        holder.assignException(e);
                        return false;
                    }
                    return true;
                }
            });
            holder.throwHeld();
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
        private static final long serialVersionUID = 1L;

        BitSetSerializer() {
            super(BitSet.class, "boolean");
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, BitSet value) {
            return value.isEmpty();
        }

        @Override
        public boolean hasSingleElement(BitSet value) {
            return value.size() == 1;
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
                throws JsonMappingException
        {
            if (visitor != null) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.BOOLEAN);
                }
            }
        }
        
        @Override
        protected void serializeContents(final BitSet value, final JsonGenerator gen, SerializerProvider provider)
               throws IOException
        {
            // is size() close enough to the last set bit?
            if (!value.isEmpty()) {
                for (int i = 0, len = (int) value.size(); i < len; ++i) {
                    gen.writeBoolean(value.get(i));
                }
            }
        }
    }
}
