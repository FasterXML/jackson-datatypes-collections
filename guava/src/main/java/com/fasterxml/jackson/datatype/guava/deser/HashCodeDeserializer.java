package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.google.common.hash.HashCode;

public class HashCodeDeserializer extends FromStringDeserializer<HashCode>
{
    private static final long serialVersionUID = 1L;

    public final static HashCodeDeserializer std = new HashCodeDeserializer();

    public HashCodeDeserializer() { super(HashCode.class); }

    @Override
    protected HashCode _deserialize(String value, DeserializationContext ctxt)
            throws IOException {
        return HashCode.fromString(value.toLowerCase(Locale.ENGLISH));
    }
}
