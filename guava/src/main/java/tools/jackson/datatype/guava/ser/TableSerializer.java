package tools.jackson.datatype.guava.ser;

import java.util.Map;
import java.util.Set;

import tools.jackson.core.*;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.jdk.MapSerializer;
import tools.jackson.databind.ser.std.StdContainerSerializer;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.type.TypeFactory;

import com.google.common.collect.Table;

/**
 * @author stevenmhood (via hyandell) - Initial implementation
 * @author tatu - Some refactoring to streamline code
 */
public class TableSerializer
    extends StdContainerSerializer<Table<?, ?, ?>>
{
    /**
     * Type declaration that defines parameters; may be a supertype of actual
     * type of property being serialized.
     */
    private final JavaType _type;

    private final ValueSerializer<Object> _rowSerializer;
    private final ValueSerializer<Object> _columnSerializer;
    private final TypeSerializer _valueTypeSerializer;
    private final ValueSerializer<Object> _valueSerializer;

    private final MapSerializer _rowMapSerializer;

    /*
    /**********************************************************************
    /* Serializer lifecycle
    /**********************************************************************
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
            final ValueSerializer<?> rowKeySerializer,
            final ValueSerializer<?> columnKeySerializer,
            final TypeSerializer valueTypeSerializer,
            final ValueSerializer<?> valueSerializer)
    {
        super(src, property);
        _type = src._type;
        _rowSerializer = (ValueSerializer<Object>) rowKeySerializer;
        _columnSerializer = (ValueSerializer<Object>) columnKeySerializer;
        _valueTypeSerializer = valueTypeSerializer;
        _valueSerializer = (ValueSerializer<Object>) valueSerializer;
        
        final MapType columnAndValueType = typeFactory.constructMapType(Map.class,
                _type.containedTypeOrUnknown(1), _type.containedTypeOrUnknown(2));

        ValueSerializer<?> columnAndValueSerializer = 
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
                        (ValueSerializer<Object>) columnAndValueSerializer,
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
            final ValueSerializer<?> rowKeySer,
            final ValueSerializer<?> columnKeySer,
            final TypeSerializer vts,
            final ValueSerializer<?> valueSer )
    {
        return new TableSerializer(this, property, typeFactory,
                rowKeySer, columnKeySer, vts, valueSer);
    }

    @Override
    protected StdContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer typeSer)
    {
        return new TableSerializer(this, typeSer);
    }

    @Override
    public ValueSerializer<?> createContextual(final SerializationContext ctxt,
            final BeanProperty property)
    {
        ValueSerializer<?> valueSer = _valueSerializer;
        if (valueSer == null) { // if type is final, can actually resolve:
            final JavaType valueType = _type.containedTypeOrUnknown(2);
            if (valueType.isFinal()) {
                valueSer = ctxt.findContentValueSerializer(valueType, property);
            }
        } else {
            valueSer = ctxt.handleSecondaryContextualization(valueSer, property);
        }
        ValueSerializer<?> rowKeySer = _rowSerializer;
        if (rowKeySer == null) {
            rowKeySer = ctxt.findKeySerializer(_type.containedTypeOrUnknown(0), property);
        } else {
            rowKeySer = ctxt.handleSecondaryContextualization(rowKeySer, property);
        }
        ValueSerializer<?> columnKeySer = _columnSerializer;
        if (columnKeySer == null) {
            columnKeySer = ctxt.findKeySerializer(_type.containedTypeOrUnknown(1), property);
        } else {
            columnKeySer = ctxt.handleSecondaryContextualization(columnKeySer, property);
        }
        // finally, TypeSerializers may need contextualization as well
        TypeSerializer typeSer = _valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(ctxt, property);
        }
        return withResolved(property, ctxt.getTypeFactory(), rowKeySer, columnKeySer, typeSer, valueSer);
    }

    /*
    /**********************************************************************
    /* Simple accessor API
    /**********************************************************************
     */
    
    @Override
    public JavaType getContentType() {
        return _type.getContentType();
    }

    @Override
    public ValueSerializer<?> getContentSerializer() {
        return _valueSerializer;
    }

    @Override
    public boolean isEmpty(SerializationContext ctxt, Table<?, ?, ?> table) {
        return table.isEmpty();
    }

    @Override
    public boolean hasSingleElement(final Table<?, ?, ?> table) {
        return table.size() == 1;
    }

    /*
    /**********************************************************************
    /* Main serialization methods
    /**********************************************************************
     */
    
    @Override
    public void serialize(final Table<?, ?, ?> value,
            final JsonGenerator gen, final SerializationContext ctxt)
        throws JacksonException
    {
        gen.writeStartObject(value);
        if (!value.isEmpty()) {
            serializeEntries(value, gen, ctxt);
        }
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(final Table<?, ?, ?> value,
            final JsonGenerator g, final SerializationContext ctxt,
            final TypeSerializer typeSer)
        throws JacksonException
    {
        g.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeEntries(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    private final void serializeEntries( final Table<?, ?, ?> table, final JsonGenerator g,
            final SerializationContext ctxt)
        throws JacksonException
    {
        _rowMapSerializer.serializeEntries(table.rowMap(), g, ctxt);
    }
}
