package tools.jackson.datatype.hppc.ser;

import tools.jackson.core.*;

import tools.jackson.databind.*;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.jdk.ObjectArraySerializer;
import tools.jackson.databind.ser.std.StdContainerSerializer;
import tools.jackson.databind.type.*;

import com.carrotsearch.hppc.*;

/**
 * Note: this implementation does not yet properly handle all
 * polymorphic cases
 */
public class ObjectContainerSerializer
    extends ContainerSerializerBase<ObjectContainer<?>>
{
    protected final JavaType _contentType;

    /**
     * We will basically just delegate serialization to the standard
     * Object[] serializer; as we can not sub-class it.
     */
    protected final ObjectArraySerializer _delegate;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */
    
    public ObjectContainerSerializer(CollectionLikeType containerType,
            ObjectArraySerializer delegate)
    {
        // not sure if we can claim it is "object"... could be String, wrapper types etc:
        super(containerType, "any");
        _contentType = containerType.getContentType();
        _delegate = delegate;
    }

    protected ObjectContainerSerializer(ObjectContainerSerializer base,
            ObjectArraySerializer delegate)
    {
        super(base);
        _contentType = base._contentType;
        _delegate = delegate;
    }
    
    protected ObjectContainerSerializer withDelegate(ObjectArraySerializer newDelegate) {
        return (newDelegate == _delegate) ? this : new ObjectContainerSerializer(this, newDelegate);
    }

    @Override
    protected StdContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        ObjectArraySerializer ser = (ObjectArraySerializer) _delegate._withValueTypeSerializer(vts);
        if (ser == _delegate) {
            return this;
        }
        return withDelegate(ser);
    }

    /*
    /**********************************************************************
    /* Schema support
    /**********************************************************************
     */

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    {
        _delegate.acceptJsonFormatVisitor(visitor, typeHint);
    }

    /*
    /**********************************************************************
    /* Overridden accessors
    /**********************************************************************
     */

    @Override
    public ValueSerializer<?> getContentSerializer() {
        return _delegate.getContentSerializer();
    }

    @Override
    public boolean hasSingleElement(ObjectContainer<?> value) {
        return value.size() == 1;
    }
    
    @Override
    public boolean isEmpty(SerializerProvider provider, ObjectContainer<?> value) {
        // 27-Nov-2015, tatu: Not up to 2.7 full specs (wrt value vs content emptiness)
        //   but better than we had before
        return value.isEmpty();
    }

    @Override
    public JavaType getContentType() {
        return _contentType;
    }
    
    /*
    /**********************************************************************
    /* Contextualization
    /**********************************************************************
     */

    @Override
    public ValueSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property)
    {
        return withDelegate((ObjectArraySerializer) _delegate.createContextual(prov, property));
    }

    /*
    /**********************************************************************
    /* Serialization
    /**********************************************************************
     */
    
    @Override
    public void serialize(ObjectContainer<?> value, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        _delegate.serialize(value.toArray(), gen, provider);
    }
    
    @Override
    public void serializeWithType(ObjectContainer<?> value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws JacksonException
    {
        _delegate.serializeWithType(value.toArray(), gen, provider, typeSer);
    }

    @Override
    protected void serializeContents(ObjectContainer<?> value, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        throw new IllegalStateException();
    }
}
