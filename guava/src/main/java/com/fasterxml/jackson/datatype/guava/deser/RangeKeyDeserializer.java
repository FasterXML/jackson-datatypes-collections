package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeHelper;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;

import java.io.IOException;

/**
 * Jackson key deserializer for a Guava {@link Range}.
 *
 * @author mcvayc
 * @since 2.21
 */
public class RangeKeyDeserializer
    extends KeyDeserializer
    implements ContextualKeyDeserializer
{
    protected final JavaType _rangeType;

    protected final KeyDeserializer _fromStringDeserializer;

    /**
     * @since 2.21
     */
    public RangeKeyDeserializer(JavaType type) {
        this(type, null);
    }

    protected RangeKeyDeserializer(JavaType rangeType, KeyDeserializer rangeDeserializer) {
        _rangeType = rangeType;
        _fromStringDeserializer = rangeDeserializer;
    }

    @Override
    public KeyDeserializer createContextual(DeserializationContext ctxt,
            BeanProperty property)
        throws JsonMappingException
    {
        JavaType endpointType = _rangeType.containedType(0);
        if (endpointType == null) { // should this ever occur?
            endpointType = TypeFactory.unknownType();
        }
        KeyDeserializer kd = _fromStringDeserializer;
        kd = ctxt.findKeyDeserializer(endpointType, property);
        if ((kd != _fromStringDeserializer)) {
            return new RangeKeyDeserializer(_rangeType, kd);
        }
        return this;
    }

    @Override
    public Object deserializeKey(String rangeInterval, DeserializationContext context)
        throws IOException
    {
        if (rangeInterval.isEmpty()) {
            throw context.instantiationException(RangeMap.class, "RangeMap keys can't be null or empty.");
        }

        return RangeHelper.getRangeFromString(rangeInterval, context, _fromStringDeserializer, _rangeType, Range.class);
    }
}
