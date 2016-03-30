package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.google.common.base.Optional;

public class GuavaOptionalDeserializer
    extends StdDeserializer<Optional<?>>
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

    /**
     * Full type of `Optional` property.
     */
    protected final JavaType _fullType;

    protected final JsonDeserializer<?> _valueDeserializer;

    protected final TypeDeserializer _valueTypeDeserializer;

    public GuavaOptionalDeserializer(JavaType fullType,
            TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
    {
        super(fullType);
        _fullType = fullType;
        _valueTypeDeserializer = typeDeser;
        _valueDeserializer = valueDeser;
    }

    @Override
    public JavaType getValueType() { return _fullType; }

    @Override
    public Optional<?> getNullValue(DeserializationContext ctxt) {
        return Optional.absent();
    }

    @Override
    @Deprecated // since 2.6; remove from 2.8
    public Optional<?> getNullValue() {
        return Optional.absent();
    }

    /**
     * Overridable fluent factory method used for creating contextual
     * instances.
     */
    protected GuavaOptionalDeserializer withResolved(JavaType fullType,
            TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
    {
        if ((_fullType == fullType)
                && (valueDeser == _valueDeserializer) && (typeDeser == _valueTypeDeserializer)) {
            return this;
        }
        return new GuavaOptionalDeserializer(_fullType, typeDeser, valueDeser);
    }

    /*
    /**********************************************************
    /* Validation, post-processing
    /**********************************************************
     */

    /**
     * Method called to finalize setup of this deserializer,
     * after deserializer itself has been registered. This
     * is needed to handle recursive and transitive dependencies.
     */
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

    @Override
    public Optional<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        Object refd = (_valueTypeDeserializer == null)
                ? _valueDeserializer.deserialize(p, ctxt)
                : _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
        return Optional.fromNullable(refd);
    }

    @Override
    public Optional<?> deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
        throws IOException
    {
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return getNullValue();
        }
        // 03-Nov-2013, tatu: This gets rather tricky with "natural" types
        //   (String, Integer, Boolean), which do NOT include type information.
        //   These might actually be handled ok except that nominal type here
        //   is `Optional`, so special handling is not invoked; instead, need
        //   to do a work-around here.
        // 22-Oct-2015, tatu: Most likely this is actually wrong, result of incorrewct
        //   serialization (up to 2.6, was omitting necessary type info after all);
        //   but safest to leave in place for now
        if (t != null && t.isScalarValue()) {
            return deserialize(p, ctxt);
        }
        return (Optional<?>) typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }
}
