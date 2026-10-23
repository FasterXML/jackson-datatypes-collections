package tools.jackson.datatype.guava.ser.primitives;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.ArraySerializerBase;
import tools.jackson.databind.ser.std.StdContainerSerializer;
import tools.jackson.databind.type.TypeFactory;

/**
 * Shared base for serializers of Guava immutable primitive arrays
 * ({@code ImmutableIntArray}, {@code ImmutableLongArray}, {@code ImmutableDoubleArray}).
 * Extends {@link ArraySerializerBase} to get per-property handling of
 * {@code @JsonFormat} single-element unwrapping.
 *<p>
 * Note: content inclusion ({@code @JsonInclude(content = ...)}) is NOT supported,
 * to avoid boxing of element values.
 *
 * @since 3.3
 */
public abstract class ImmutablePrimitiveArraySerializerBase<T>
    extends ArraySerializerBase<T>
{
    protected final JavaType _elementType;

    protected ImmutablePrimitiveArraySerializerBase(Class<T> cls, Class<?> primitiveType) {
        super(cls);
        _elementType = TypeFactory.unsafeSimpleType(primitiveType);
    }

    protected ImmutablePrimitiveArraySerializerBase(ImmutablePrimitiveArraySerializerBase<T> src,
            BeanProperty prop, Boolean unwrapSingle) {
        super(src, prop, unwrapSingle, null, false);
        _elementType = src._elementType;
    }

    /*
    /**********************************************************************
    /* Abstract methods for sub-classes
    /**********************************************************************
     */

    protected abstract int _length(T value);

    protected abstract void _writeElement(JsonGenerator g, T value, int index);

    /*
    /**********************************************************************
    /* Contextualization
    /**********************************************************************
     */

    // Overridden to only resolve single-element unwrapping, not content inclusion
    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt,
            BeanProperty property)
    {
        Boolean unwrapSingle = null;
        // Note: property may be null (root values), but per-type config overrides apply
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        if (format != null) {
            unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        }
        if (!Objects.equals(unwrapSingle, _unwrapSingle)) {
            return _withResolved(property, unwrapSingle);
        }
        return this;
    }

    protected abstract ImmutablePrimitiveArraySerializerBase<T> _withResolved(BeanProperty prop,
            Boolean unwrapSingle);

    @Override
    protected final ArraySerializerBase<T> _withResolved(BeanProperty prop,
            Boolean unwrapSingle, Object suppressableValue, boolean suppressNulls) {
        return _withResolved(prop, unwrapSingle);
    }

    /*
    /**********************************************************************
    /* StdContainerSerializer implementation
    /**********************************************************************
     */

    // Primitive elements never have type ids
    @Override
    protected StdContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return this;
    }

    @Override
    public JavaType getContentType() {
        return _elementType;
    }

    @Override
    public ValueSerializer<?> getContentSerializer() {
        return null;
    }

    @Override
    public boolean isEmpty(SerializationContext ctxt, T value) {
        return _length(value) == 0;
    }

    @Override
    public boolean hasSingleElement(T value) {
        return _length(value) == 1;
    }

    /*
    /**********************************************************************
    /* Serialization
    /**********************************************************************
     */

    @Override
    public final void serialize(T value, JsonGenerator g, SerializationContext ctxt)
    {
        if (_shouldUnwrapSingle(ctxt) && hasSingleElement(value)) {
            serializeContents(value, g, ctxt);
            return;
        }
        g.writeStartArray(value, _length(value));
        serializeContents(value, g, ctxt);
        g.writeEndArray();
    }

    // NOTE: `serializeWithType()` inherited from `ArraySerializerBase`

    @Override
    protected void serializeContents(T value, JsonGenerator g, SerializationContext ctxt)
    {
        for (int i = 0, len = _length(value); i < len; ++i) {
            _writeElement(g, value, i);
        }
    }
}
