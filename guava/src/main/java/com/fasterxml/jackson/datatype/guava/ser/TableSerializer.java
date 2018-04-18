package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;
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
    private static final long serialVersionUID = 1L;

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
                MapSerializer.construct((Set<String>) null,
                                        columnAndValueType,
                                        false,
                                        _valueTypeSerializer,
                                        _columnSerializer,
                                        _valueSerializer,
                                        null);

        final MapType rowMapType = typeFactory.constructMapType(Map.class,
                _type.containedTypeOrUnknown(0), columnAndValueType);
        _rowMapSerializer =
                MapSerializer.construct((Set<String>) null,
                                        rowMapType,
                                        false,
                                        null,
                                        _rowSerializer,
                                        (JsonSerializer<Object>) columnAndValueSerializer,
                                        null);
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
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property )
        throws JsonMappingException
    {
        JsonSerializer<?> valueSer = _valueSerializer;
        if (valueSer == null) { // if type is final, can actually resolve:
            final JavaType valueType = _type.containedTypeOrUnknown(2);
            if (valueType.isFinal()) {
                valueSer = provider.findSecondaryPropertySerializer(valueType, property);
            }
        } else {
            valueSer = valueSer.createContextual(provider, property);
        }
        JsonSerializer<?> rowKeySer = _rowSerializer;
        if (rowKeySer == null) {
            rowKeySer = provider.findKeySerializer(_type.containedTypeOrUnknown(0), property);
        } else {
            rowKeySer = rowKeySer.createContextual(provider, property);
        }
        JsonSerializer<?> columnKeySer = _columnSerializer;
        if (columnKeySer == null) {
            columnKeySer = provider.findKeySerializer(_type.containedTypeOrUnknown(1), property);
        } else {
            columnKeySer = columnKeySer.createContextual(provider, property);
        }
        // finally, TypeSerializers may need contextualization as well
        TypeSerializer typeSer = _valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
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
        throws IOException
    {
        gen.writeStartObject(value);
        if ( !value.isEmpty()) {
            serializeFields(value, gen, provider);
        }
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(final Table<?, ?, ?> value,
            final JsonGenerator gen, final SerializerProvider provider,
            final TypeSerializer typeSer) throws IOException
    {
        gen.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeFields(value, gen, provider);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    private final void serializeFields( final Table<?, ?, ?> table, final JsonGenerator jgen, final SerializerProvider provider )
        throws IOException
    {
        _rowMapSerializer.serializeFields(table.rowMap(), jgen, provider);
    }
}
