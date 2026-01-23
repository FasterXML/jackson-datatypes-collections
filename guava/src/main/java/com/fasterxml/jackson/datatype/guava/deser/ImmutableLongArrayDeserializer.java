package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;

import com.google.common.primitives.ImmutableLongArray;

public final class ImmutableLongArrayDeserializer extends StdDeserializer<ImmutableLongArray>
{
    private static final long serialVersionUID = 1L;

    private final JsonDeserializer<long[]> _longArrayDeserializer;

    public ImmutableLongArrayDeserializer() {
        super(ImmutableLongArray.class);
        @SuppressWarnings("unchecked")
        JsonDeserializer<long[]> deser = (JsonDeserializer<long[]>) PrimitiveArrayDeserializers.forType(long.class);
        _longArrayDeserializer = deser;
    }

    @Override
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override
    public ImmutableLongArray getEmptyValue(DeserializationContext ctxt) {
        return ImmutableLongArray.of();
    }

    @Override
    public ImmutableLongArray deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ImmutableLongArray.copyOf(_longArrayDeserializer.deserialize(p, ctxt));
    }
}
