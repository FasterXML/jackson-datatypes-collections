package com.fasterxml.jackson.datatype.guava.ser;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.google.common.collect.Table;

/**
 * @author stevenmhood (via hyandell) - Initial implementation
 * @author tatu - Some refactoring to streamline code
 */
public class TableSerializer
    extends ContainerSerializer<Table<?, ?, ?>>
{
    /**
     * Type declaration that defines parameters; may be a supertype of actual
     * type of property being serialized.
     */
    private final JavaType _type;

    private final JsonSerializer<Object> _rowSerializer;
    private final JsonSerializer<Object> _columnSerializer;
    private final TypeSerializer _valueTypeSerializer;
    private final JsonSerializer<Object> _valueSerializer;

    private final MapSerializer _rowMapSerializer;

    /*
    /**********************************************************
    /* Serializer lifecycle
    /**********************************************************
     */
    
    public TableSerializer(JavaType type)
    {
        super(type, null);
        _type = type;
        _rowSerializer = null;
        _columnSerializer = null;
        _valueTypeSerializer = null;
        _valueSerializer = null;

        _rowMapSerializer = null;
    }

    @SuppressWarnings( "unchecked" )
    protected TableSerializer(final TableSerializer src,
            final BeanProperty property,
            final TypeFactory typeFactory,
            final JsonSerializer<?> rowKeySerializer,
            final JsonSerializer<?> columnKeySerializer,
            final TypeSerializer valueTypeSerializer,
            final JsonSerializer<?> valueSerializer)
    {
        super(src, property);
        _type = src._type;
        _rowSerializer = (JsonSerializer<Object>) rowKeySerializer;
        _columnSerializer = (JsonSerializer<Object>) columnKeySerializer;
        _valueTypeSerializer = valueTypeSerializer;
        _valueSerializer = (JsonSerializer<Object>) valueSerializer;
        
        final MapType columnAndValueType = typeFactory.constructMapType(Map.class,
                _type.containedTypeOrUnknown(1), _type.containedTypeOrUnknown(2));

        JsonSerializer<?> columnAndValueSerializer = 
                MapSerializer.construct(columnAndValueType, false,
                        _valueTypeSerializer,
                        _columnSerializer,
                        _valueSerializer,
                        null,
                        (Set<String>) null, (Set<String>) null);

        final MapType rowMapType = typeFactory.constructMapType(Map.class,
                _type.containedTypeOrUnknown(0), columnAndValueType);
        _rowMapSerializer =
                MapSerializer.construct(rowMapType, false,
                        null,
                        _rowSerializer,
                        (JsonSerializer<Object>) columnAndValueSerializer,
                        null,
                        (Set<String>) null, (Set<String>) null);
    }

    protected TableSerializer(final TableSerializer src, TypeSerializer typeSer)
    {
        super(src);
        _type = src._type;
        _rowSerializer = src._rowSerializer;
        _columnSerializer = src._columnSerializer;
        _valueTypeSerializer = typeSer;
        _valueSerializer = src._valueSerializer;

        _rowMapSerializer = src._rowMapSerializer;
    }

    protected TableSerializer withResolved(final BeanProperty property,
            final TypeFactory typeFactory,
            final JsonSerializer<?> rowKeySer,
            final JsonSerializer<?> columnKeySer,
            final TypeSerializer vts,
            final JsonSerializer<?> valueSer )
    {
        return new TableSerializer(this, property, typeFactory,
                rowKeySer, columnKeySer, vts, valueSer);
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer typeSer)
    {
        return new TableSerializer(this, typeSer);
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider,
            final BeanProperty property)
    {
        JsonSerializer<?> valueSer = _valueSerializer;
        if (valueSer == null) { // if type is final, can actually resolve:
            final JavaType valueType = _type.containedTypeOrUnknown(2);
            if (valueType.isFinal()) {
                valueSer = provider.findContentValueSerializer(valueType, property);
            }
        } else {
            valueSer = provider.handleSecondaryContextualization(valueSer, property);
        }
        JsonSerializer<?> rowKeySer = _rowSerializer;
        if (rowKeySer == null) {
            rowKeySer = provider.findKeySerializer(_type.containedTypeOrUnknown(0), property);
        } else {
            rowKeySer = provider.handleSecondaryContextualization(rowKeySer, property);
        }
        JsonSerializer<?> columnKeySer = _columnSerializer;
        if (columnKeySer == null) {
            columnKeySer = provider.findKeySerializer(_type.containedTypeOrUnknown(1), property);
        } else {
            columnKeySer = provider.handleSecondaryContextualization(columnKeySer, property);
        }
        // finally, TypeSerializers may need contextualization as well
        TypeSerializer typeSer = _valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(provider, property);
        }
        return withResolved(property, provider.getTypeFactory(), rowKeySer, columnKeySer, typeSer, valueSer);
    }

    /*
    /**********************************************************
    /* Simple accessor API
    /**********************************************************
     */
    
    @Override
    public JavaType getContentType() {
        return _type.getContentType();
    }

    @Override
    public JsonSerializer<?> getContentSerializer() {
        return _valueSerializer;
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Table<?, ?, ?> table) {
        return table.isEmpty();
    }

    @Override
    public boolean hasSingleElement(final Table<?, ?, ?> table) {
        return table.size() == 1;
    }

    /*
    /**********************************************************
    /* Main serialization methods
    /**********************************************************
     */
    
    @Override
    public void serialize(final Table<?, ?, ?> value,
            final JsonGenerator gen, final SerializerProvider provider)
        throws JacksonException
    {
        gen.writeStartObject(value);
        if (!value.isEmpty()) {
            serializeFields(value, gen, provider);
        }
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(final Table<?, ?, ?> value,
            final JsonGenerator gen, final SerializerProvider ctxt,
            final TypeSerializer typeSer)
        throws JacksonException
    {
        gen.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, ctxt,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeFields(value, gen, ctxt);
        typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
    }

    private final void serializeFields( final Table<?, ?, ?> table, final JsonGenerator jgen, final SerializerProvider provider )
        throws JacksonException
    {
        _rowMapSerializer.serializeFields(table.rowMap(), jgen, provider);
    }
}
