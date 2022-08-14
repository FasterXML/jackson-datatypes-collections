package com.fasterxml.jackson.datatype.guava.deser;

import java.util.Locale;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.FromStringDeserializer;

import com.google.common.hash.HashCode;

public class HashCodeDeserializer extends FromStringDeserializer<HashCode>
{
    public final static HashCodeDeserializer std = new HashCodeDeserializer();

    public HashCodeDeserializer() { super(HashCode.class); }

    @Override
    protected HashCode _deserialize(String value, DeserializationContext ctxt)
        throws JacksonException
    {
        return HashCode.fromString(value.toLowerCase(Locale.ENGLISH));
    }
}
