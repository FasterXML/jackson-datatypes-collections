package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.base.Optional;

public class GuavaOptionalDeserializer
    extends ReferenceTypeDeserializer<Optional<?>>
{
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    /**
     * @since 2.9
     */
    public GuavaOptionalDeserializer(JavaType fullType, ValueInstantiator inst,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser)
    {
        super(fullType, inst, typeDeser, deser);
    }
    
    /*
    /**********************************************************
    /* Abstract method implementations
    /**********************************************************
     */

    @Override
    public GuavaOptionalDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new GuavaOptionalDeserializer(_fullType, _valueInstantiator,
                typeDeser, valueDeser);
    }

    @Override
    public Optional<?> getNullValue(DeserializationContext ctxt) {
        return Optional.absent();
    }

    @Override
    public Optional<?> referenceValue(Object contents) {
        return Optional.fromNullable(contents);
    }

    @Override
    public Object getReferenced(Optional<?> reference) {
        return reference.get();
    }

    @Override // since 2.9
    public Optional<?> updateReference(Optional<?> reference, Object contents) {
        return Optional.fromNullable(contents);
    }

    // Default ought to be fine:
//    public Boolean supportsUpdate(DeserializationConfig config) {

    /*
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        JsonDeserializer<?> deser = _valueDeserializer;
        TypeDeserializer typeDeser = _valueTypeDeserializer;
        JavaType fullType = _fullType;

        if (deser == null) {
            // 08-Oct-2015, tatu:  need to allow type override, if any
            if (property != null) {
                AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
                AnnotatedMember member = property.getMember();
                if ((intr != null)  && (member != null)) {
                    fullType = intr.refineDeserializationType(ctxt.getConfig(), member, fullType);
                }
            }
            JavaType refdType = fullType.getContentType();
            if (refdType == null) {
                refdType = TypeFactory.unknownType();
            }
            deser = ctxt.findContextualValueDeserializer(refdType, property);
        } else { // otherwise directly assigned, probably not contextual yet:
            JavaType refdType = fullType.getContentType();
            if (refdType == null) {
                refdType = TypeFactory.unknownType();
            }
            deser = ctxt.handleSecondaryContextualization(deser, property, refdType);
        }
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        return withResolved(fullType, typeDeser, deser);
    }
    */
}
