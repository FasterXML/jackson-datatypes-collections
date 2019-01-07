package com.fasterxml.jackson.datatype.primitive_collections_base.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import java.io.IOException;

public abstract class PrimitiveIterableSerializer<C> extends ContainerSerializer<C> {
    private static final long serialVersionUID = 0L;

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

    protected abstract PrimitiveIterableSerializer<C> withResolved(BeanProperty property, Boolean unwrapSingle);

    @Override
    public JavaType getContentType() {
        return _elementType;
    }

    @Override
    public JsonSerializer<?> getContentSerializer() {
        return null;
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        // no type info for primitives
        return this;
    }

    @Override
    public final void serialize(C value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (((_unwrapSingle == null) &&
                provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED))
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
    public void serializeWithType(C value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
            throws IOException {
        g.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
        serializeContents(value, g);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    protected abstract void serializeContents(C value, JsonGenerator gen) throws IOException;
}

