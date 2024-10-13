package tools.jackson.datatype.primitive_collections_base.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdContainerSerializer;

public abstract class PrimitiveIterableSerializer<C> extends StdContainerSerializer<C>
{
    protected final JavaType _elementType;
    protected final BeanProperty _property;
    protected final Boolean _unwrapSingle;

    protected PrimitiveIterableSerializer(
            Class<C> type, JavaType elementType,
            BeanProperty property, Boolean unwrapSingle
    ) {
        super(type);
        _elementType = elementType;
        _property = property;
        _unwrapSingle = unwrapSingle;
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
    protected StdContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        // no type info for primitives
        return this;
    }

    @Override
    public final void serialize(C value, JsonGenerator gen, SerializerProvider ctxt)
        throws JacksonException
    {
        if (((_unwrapSingle == null) &&
                ctxt.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED))
                || (Boolean.TRUE.equals(_unwrapSingle))) {
            if (hasSingleElement(value)) {
                serializeContents(value, gen);
                return;
            }
        }
        gen.writeStartArray();
        serializeContents(value, gen);
        gen.writeEndArray();
    }

    @Override
    public void serializeWithType(C value, JsonGenerator g, SerializerProvider ctxt, TypeSerializer typeSer)
        throws JacksonException
    {
        g.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt, typeSer.typeId(value, JsonToken.START_ARRAY));
        serializeContents(value, g);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    protected abstract void serializeContents(C value, JsonGenerator gen)
        throws JacksonException;
}

