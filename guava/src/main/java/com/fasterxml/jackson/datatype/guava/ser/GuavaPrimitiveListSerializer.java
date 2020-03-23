//package com.fasterxml.jackson.datatype.guava.ser;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.BeanProperty;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
//import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
//import com.fasterxml.jackson.databind.ser.ContainerSerializer;
//import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
//import com.fasterxml.jackson.databind.type.CollectionLikeType;
//
//import java.io.IOException;
//
///**
// * Note: this implementation does not yet properly handle all
// * polymorphic cases
// */
//public class GuavaPrimitiveListSerializer
//        extends ContainerSerializer<ObjectContainer<?>>
//{
//    protected final JavaType _contentType;
//
//    /**
//     * We will basically just delegate serialization to the standard
//     * Object[] serializer; as we can not sub-class it.
//     */
//    protected final ObjectArraySerializer _delegate;
//
//    /*
//    /**********************************************************************
//    /* Life-cycle
//    /**********************************************************************
//     */
//
//    public ObjectContainerSerializer(CollectionLikeType containerType,
//                                     ObjectArraySerializer delegate)
//    {
//        // not sure if we can claim it is "object"... could be String, wrapper types etc:
//        super(containerType, "any");
//        _contentType = containerType.getContentType();
//        _delegate = delegate;
//    }
//
//    protected ObjectContainerSerializer(ObjectContainerSerializer base,
//                                        ObjectArraySerializer delegate)
//    {
//        super(base);
//        _contentType = base._contentType;
//        _delegate = delegate;
//    }
//
//    protected ObjectContainerSerializer withDelegate(ObjectArraySerializer newDelegate) {
//        return (newDelegate == _delegate) ? this : new ObjectContainerSerializer(this, newDelegate);
//    }
//
//    @Override
//    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
//        ObjectArraySerializer ser = (ObjectArraySerializer) _delegate._withValueTypeSerializer(vts);
//        if (ser == _delegate) {
//            return this;
//        }
//        return withDelegate(ser);
//    }
//
//    /*
//    /**********************************************************************
//    /* Schema support
//    /**********************************************************************
//     */
//
//    @Override
//    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
//            throws JsonMappingException
//    {
//        _delegate.acceptJsonFormatVisitor(visitor, typeHint);
//    }
//
//    /*
//    /**********************************************************************
//    /* Overridden accessors
//    /**********************************************************************
//     */
//
//    @Override
//    public JsonSerializer<?> getContentSerializer() {
//        return _delegate.getContentSerializer();
//    }
//
//    @Override
//    public boolean hasSingleElement(ObjectContainer<?> value) {
//        return value.size() == 1;
//    }
//
//    @Override
//    public boolean isEmpty(SerializerProvider provider, ObjectContainer<?> value) {
//        // 27-Nov-2015, tatu: Not up to 2.7 full specs (wrt value vs content emptiness)
//        //   but better than we had before
//        return value.isEmpty();
//    }
//
//    @Override
//    public JavaType getContentType() {
//        return _contentType;
//    }
//
//    /*
//    /**********************************************************************
//    /* Contextualization
//    /**********************************************************************
//     */
//
//    @Override
//    public JsonSerializer<?> createContextual(SerializerProvider prov,
//                                              BeanProperty property) throws JsonMappingException {
//        return withDelegate((ObjectArraySerializer) _delegate.createContextual(prov, property));
//    }
//
//    /*
//    /**********************************************************************
//    /* Serialization
//    /**********************************************************************
//     */
//
//    @Override
//    public void serialize(ObjectContainer<?> value, JsonGenerator gen, SerializerProvider provider)
//            throws IOException
//    {
//        _delegate.serialize(value.toArray(), gen, provider);
//    }
//
//    @Override
//    public void serializeWithType(ObjectContainer<?> value, JsonGenerator gen, SerializerProvider provider,
//                                  TypeSerializer typeSer)
//            throws IOException
//    {
//        _delegate.serializeWithType(value.toArray(), gen, provider, typeSer);
//    }
//
//    @Override
//    protected void serializeContents(ObjectContainer<?> value, JsonGenerator gen, SerializerProvider provider)
//            throws IOException
//    {
//        throw new IllegalStateException();
//    }
//}
