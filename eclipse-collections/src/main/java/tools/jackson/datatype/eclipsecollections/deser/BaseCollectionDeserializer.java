package tools.jackson.datatype.eclipsecollections.deser;

import java.util.Arrays;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;

public abstract class BaseCollectionDeserializer<T, Intermediate> extends StdDeserializer<T> {
    protected BaseCollectionDeserializer(Class<? super T> cls) {
        super(cls);
    }

    protected BaseCollectionDeserializer(JavaType type) {
        super(type);
    }

    @Override
    public LogicalType logicalType() { return LogicalType.Collection; }

    protected abstract Intermediate createIntermediate();

    protected abstract void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws JacksonException;

    protected abstract T finish(Intermediate intermediate);

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
            throws JacksonException
    {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
{
        // Should usually point to START_ARRAY
        if (p.isExpectedStartArrayToken()) {
            return _deserializeContents(p, ctxt);
        }
        // But may support implicit arrays from single values?
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            return _deserializeFromSingleValue(p, ctxt);
        }
        return (T) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }

    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
            throws JacksonException {
        Intermediate collection = createIntermediate();

        while (p.nextToken() != JsonToken.END_ARRAY) {
            add(collection, p, ctxt);
        }
        return finish(collection);
    }

    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
            throws JacksonException {
        Intermediate intermediate = createIntermediate();
        add(intermediate, p, ctxt);
        return finish(intermediate);
    }

    public abstract static class Boolean<T extends BooleanIterable, Intermediate extends MutableBooleanCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Boolean(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
                throws JacksonException {
            intermediate.add(parser.getBooleanValue());
        }
    }

    public abstract static class Byte<T extends ByteIterable, Intermediate extends MutableByteCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Byte(Class<? super T> cls) {
            super(cls);
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            if (p.currentToken() == JsonToken.VALUE_STRING ||
                p.currentToken() == JsonToken.VALUE_EMBEDDED_OBJECT) {

                byte[] binaryValue = p.getBinaryValue();
                Intermediate intermediate = createIntermediate();
                intermediate.addAll(binaryValue);
                return finish(intermediate);
            }
            return super.deserialize(p, ctxt);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
                throws JacksonException {
            intermediate.add(parser.getByteValue());
        }
    }

    public abstract static class Short<T extends ShortIterable, Intermediate extends MutableShortCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Short(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
                throws JacksonException {
            intermediate.add(parser.getShortValue());
        }
    }

    public abstract static class Char<T extends CharIterable, Intermediate extends MutableCharCollection>
            extends StdDeserializer<T> {
        private static final int BATCH_COPY_SIZE = 4096;

        // this isn't actually a BaseCollectionDeserializer because we serialize from/to strings instead

        protected Char(Class<? super T> cls) {
            super(cls);
        }

        protected abstract Intermediate createIntermediate();

        protected abstract T finish(Intermediate intermediate);

        @Override
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
                throws JacksonException {
            return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt)
                throws JacksonException {
            Intermediate intermediate = createIntermediate();

            if (p.isExpectedStartArrayToken()) {
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    String str;
                    if (t == JsonToken.VALUE_STRING) {
                        str = p.getString();
                    } else {
                        CharSequence cs = (CharSequence) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                        str = cs.toString();
                    }
                    if (str.length() != 1) {
                        ctxt.reportInputMismatch(this,
                                                 "Cannot convert a JSON String of length %d into a char element of " +
                                                 "char array",
                                                 str.length());
                    }
                    intermediate.add(str.charAt(0));
                }
                return finish(intermediate);
            }

            char[] chars = p.getStringCharacters();
            if (p.getStringOffset() == 0 && p.getStringLength() == chars.length) {
                intermediate.addAll(chars);
            } else {
                int i = 0;
                // first, copy in batches of BATCH_COPY_SIZE
                if ((p.getStringLength() - i) >= BATCH_COPY_SIZE) {
                    char[] buf = new char[BATCH_COPY_SIZE];
                    do {
                        System.arraycopy(chars, p.getStringOffset() + i, buf, 0, BATCH_COPY_SIZE);
                        intermediate.addAll(buf);
                        i += BATCH_COPY_SIZE;
                    } while ((p.getStringLength() - i) >= BATCH_COPY_SIZE);
                }
                // and finally, copy the remainder.
                if (p.getStringLength() > i) {
                    char[] tail = Arrays.copyOfRange(
                            chars, p.getStringOffset() + i, p.getStringOffset() + p.getStringLength());
                    intermediate.addAll(tail);
                }
            }
            return finish(intermediate);
        }
    }

    public abstract static class Int<T extends IntIterable, Intermediate extends MutableIntCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Int(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
                throws JacksonException {
            intermediate.add(parser.getIntValue());
        }
    }

    public abstract static class Float<T extends FloatIterable, Intermediate extends MutableFloatCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Float(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
                throws JacksonException {
            intermediate.add(parser.getFloatValue());
        }
    }

    public abstract static class Long<T extends LongIterable, Intermediate extends MutableLongCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Long(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
                throws JacksonException {
            intermediate.add(parser.getLongValue());
        }
    }

    public abstract static class Double<T extends DoubleIterable, Intermediate extends MutableDoubleCollection>
            extends BaseCollectionDeserializer<T, Intermediate> {
        protected Double(Class<? super T> cls) {
            super(cls);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
                throws JacksonException {
            intermediate.add(parser.getDoubleValue());
        }
    }

    public abstract static class Ref<T, Intermediate extends MutableCollection<Object>>
            extends BaseCollectionDeserializer<T, Intermediate>
    {
        protected final JavaType _elementType;
        protected final ValueDeserializer<?> _valueDeserializer;
        protected final TypeDeserializer _typeDeserializerForValue;

        protected Ref(
                // ? super T so we can support generics in T
                Class<? super T> containerType,
                JavaType elementType,
                TypeDeserializer typeDeserializer,
                ValueDeserializer<?> deserializer
        ) {
            super(containerType);
            this._elementType = elementType;
            this._typeDeserializerForValue = typeDeserializer;
            this._valueDeserializer = deserializer;
        }

        protected abstract Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        );

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
        {
            ValueDeserializer<?> deser = _valueDeserializer;
            TypeDeserializer typeDeser = _typeDeserializerForValue;
            if (deser == null) {
                deser = ctxt.findContextualValueDeserializer(_elementType, property);
            }
            if (typeDeser != null) {
                typeDeser = typeDeser.forProperty(property);
            }
            if (deser == _valueDeserializer && typeDeser == _typeDeserializerForValue) {
                return this;
            }
            return withResolved(typeDeser, deser);
        }

        @Override
        protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
                throws JacksonException {
            Object value;
            if (parser.currentToken() == JsonToken.VALUE_NULL) {
                value = null;
            } else if (_typeDeserializerForValue == null) {
                value = _valueDeserializer.deserialize(parser, ctx);
            } else {
                value = _valueDeserializer.deserializeWithType(parser, ctx, _typeDeserializerForValue);
            }
            intermediate.add(value);
        }
    }
}
