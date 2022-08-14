package com.fasterxml.jackson.datatype.eclipsecollections.deser;

import java.util.Arrays;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseCharCollectionDeserializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseCollectionDeserializer;

import org.eclipse.collections.api.*;
import org.eclipse.collections.api.collection.primitive.*;

public final class BaseCollectionDeserializers {

    private BaseCollectionDeserializers() {}

    public abstract static class Boolean<T extends BooleanIterable, Intermediate extends MutableBooleanCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Boolean(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws JacksonException
        {
            intermediate.add(parser.getBooleanValue());
        }
    }

    public abstract static class Byte<T extends ByteIterable, Intermediate extends MutableByteCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Byte(Class<? super T> cls) {
            super(cls);
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
        {
            if (p.currentToken() == JsonToken.VALUE_STRING ||
                p.currentToken() == JsonToken.VALUE_EMBEDDED_OBJECT) {

                byte[] binaryValue = p.getBinaryValue();
                Intermediate intermediate = createIntermediate(binaryValue.length);
                intermediate.addAll(binaryValue);
                return finish(intermediate);
            }
            return super.deserialize(p, ctxt);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws JacksonException
        {
            intermediate.add(parser.getByteValue());
        }
    }

    public abstract static class Short<T extends ShortIterable, Intermediate extends MutableShortCollection>
            extends BaseCollectionDeserializer<T, Intermediate>
    {
        protected Short(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws JacksonException
        {
            intermediate.add(parser.getShortValue());
        }
    }

    public abstract static class Char<T extends CharIterable, Intermediate extends MutableCharCollection>
            extends BaseCharCollectionDeserializer<T, Intermediate>
    {
        private static final int BATCH_COPY_SIZE = 4096;

        protected Char(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected abstract Intermediate createIntermediate();

        @Override
        protected abstract T finish(Intermediate intermediate);

        @Override
        protected void add(Intermediate intermediate, char c) {
            intermediate.add(c);
        }

        @Override
        protected void addAll(Intermediate intermediate, char[] chars, int off, int len) {
            if (off == 0 && len == chars.length) {
                intermediate.addAll(chars);
            } else {
                int i = 0;
                // first, copy in batches of BATCH_COPY_SIZE
                if ((len - i) >= BATCH_COPY_SIZE) {
                    char[] buf = new char[BATCH_COPY_SIZE];
                    do {
                        System.arraycopy(chars, off + i, buf, 0, BATCH_COPY_SIZE);
                        intermediate.addAll(buf);
                        i += BATCH_COPY_SIZE;
                    } while ((len - i) >= BATCH_COPY_SIZE);
                }
                // and finally, copy the remainder.
                if (len > i) {
                    char[] tail = Arrays.copyOfRange(chars, off + i, off + len);
                    intermediate.addAll(tail);
                }
            }
        }
    }

    public abstract static class Int<T extends IntIterable, Intermediate extends MutableIntCollection>
            extends BaseCollectionDeserializer<T, Intermediate>
    {
        protected Int(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws JacksonException
        {
            intermediate.add(parser.getIntValue());
        }
    }

    public abstract static class Float<T extends FloatIterable, Intermediate extends MutableFloatCollection>
            extends BaseCollectionDeserializer<T, Intermediate>
    {
        protected Float(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws JacksonException
        {
            intermediate.add(parser.getFloatValue());
        }
    }

    public abstract static class Long<T extends LongIterable, Intermediate extends MutableLongCollection>
            extends BaseCollectionDeserializer<T, Intermediate>
    {
        protected Long(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws JacksonException
        {
            intermediate.add(parser.getLongValue());
        }
    }

    public abstract static class Double<T extends DoubleIterable, Intermediate extends MutableDoubleCollection>
            extends BaseCollectionDeserializer<T, Intermediate>
    {
        protected Double(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws JacksonException
        {
            intermediate.add(parser.getDoubleValue());
        }
    }
}
