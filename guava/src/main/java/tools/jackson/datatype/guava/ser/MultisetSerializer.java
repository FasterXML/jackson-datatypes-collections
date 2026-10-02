package tools.jackson.datatype.guava.ser;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Multiset;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.impl.PropertySerializerMap;
import tools.jackson.databind.ser.std.AsArraySerializerBase;
import tools.jackson.databind.ser.std.StdContainerSerializer;

/**
 * Serializer for {@link Multiset}s that writes each distinct element once,
 * along with its count:
 *<pre>
 *  [{"element":"apple","count":5},{"element":"pear","count":2}]
 *</pre>
 *
 * @since 3.3
 */
public class MultisetSerializer
    extends AsArraySerializerBase<Multiset<?>>
{
    public final static String PROP_ELEMENT = "element";

    public final static String PROP_COUNT = "count";

    public MultisetSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts,
            ValueSerializer<Object> valueSerializer) {
        super(Multiset.class, elemType, staticTyping, vts, valueSerializer);
    }

    protected MultisetSerializer(MultisetSerializer src,
            TypeSerializer vts, ValueSerializer<?> valueSerializer,
            Boolean unwrapSingle, BeanProperty property,
            Object suppressableValue, boolean suppressNulls) {
        super(src, vts, valueSerializer, unwrapSingle, property, suppressableValue, suppressNulls);
    }

    @Override
    protected StdContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return new MultisetSerializer(this, vts, _elementSerializer, _unwrapSingle, _property,
                _suppressableValue, _suppressNulls);
    }

    @Override
    protected MultisetSerializer withResolved(BeanProperty property,
            TypeSerializer vts, ValueSerializer<?> elementSerializer,
            Boolean unwrapSingle, Object suppressableValue, boolean suppressNulls) {
        return new MultisetSerializer(this, vts, elementSerializer, unwrapSingle, property,
                suppressableValue, suppressNulls);
    }

    /*
    /**********************************************************************
    /* Accessors
    /**********************************************************************
     */

    @Override
    public boolean isEmpty(SerializationContext ctxt, Multiset<?> value) {
        if (value.isEmpty()) {
            return true;
        }
        // [databind#6065]: same as `CollectionSerializer`, if all elements are suppressed
        if (_needToCheckFiltering(ctxt)) {
            return _allElementsSuppressed(ctxt, value.elementSet().iterator());
        }
        return false;
    }

    @Override
    public boolean hasSingleElement(Multiset<?> value) {
        return value.entrySet().size() == 1;
    }

    /*
    /**********************************************************************
    /* Actual serialization
    /**********************************************************************
     */

    @Override
    public void serialize(Multiset<?> value, JsonGenerator g, SerializationContext ctxt)
        throws JacksonException
    {
        final Set<? extends Multiset.Entry<?>> entries = value.entrySet();
        final int len = entries.size();
        if (len == 1) {
            if (((_unwrapSingle == null) &&
                    ctxt.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED))
                    || (_unwrapSingle == Boolean.TRUE)) {
                serializeContents(value, g, ctxt);
                return;
            }
        }
        g.writeStartArray(value, len);
        serializeContents(value, g, ctxt);
        g.writeEndArray();
    }

    @Override
    protected void serializeContents(Multiset<?> value, JsonGenerator g, SerializationContext ctxt)
        throws JacksonException
    {
        final Iterator<? extends Multiset.Entry<?>> it = value.entrySet().iterator();
        final boolean filtered = _needToCheckFiltering(ctxt);
        int i = 0;
        try {
            while (it.hasNext()) {
                final Multiset.Entry<?> entry = it.next();
                final Object elem = entry.getElement();
                ValueSerializer<Object> ser = null;
                if (elem == null) {
                    if (filtered && _suppressNulls) {
                        ++i;
                        continue;
                    }
                } else {
                    ser = _elementSerializer;
                    if (ser == null) {
                        ser = _findElementSerializer(ctxt, elem.getClass());
                    }
                    if (filtered && !_shouldSerializeElement(ctxt, elem, ser)) {
                        ++i;
                        continue;
                    }
                }
                g.writeStartObject(entry);
                g.writeName(PROP_ELEMENT);
                if (elem == null) {
                    ctxt.defaultSerializeNullValue(g);
                } else if (_valueTypeSerializer == null) {
                    ser.serialize(elem, g, ctxt);
                } else {
                    ser.serializeWithType(elem, g, ctxt, _valueTypeSerializer);
                }
                g.writeName(PROP_COUNT);
                g.writeNumber(entry.getCount());
                g.writeEndObject();
                ++i;
            }
        } catch (Exception e) {
            wrapAndThrow(ctxt, e, value, i);
        }
    }

    private ValueSerializer<Object> _findElementSerializer(SerializationContext ctxt,
            Class<?> cc)
    {
        PropertySerializerMap serializers = _dynamicValueSerializers;
        ValueSerializer<Object> ser = serializers.serializerFor(cc);
        if (ser == null) {
            if (_elementType.hasGenericTypes()) {
                ser = _findAndAddDynamic(ctxt, ctxt.constructSpecializedType(_elementType, cc));
            } else {
                ser = _findAndAddDynamic(ctxt, cc);
            }
        }
        return ser;
    }
}
