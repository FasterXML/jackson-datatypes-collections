package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.google.common.primitives.ImmutableIntArray;

import java.io.IOException;

public final class ImmutableIntArrayDeserializer extends StdDeserializer<ImmutableIntArray> {

    private final JsonDeserializer<int[]> intArrayDeserializer;

    public ImmutableIntArrayDeserializer() {
        super(ImmutableIntArray.class);
        @SuppressWarnings("unchecked")
        JsonDeserializer<int[]> intArrayDeserializer = (JsonDeserializer<int[]>) PrimitiveArrayDeserializers.forType(int.class);
        this.intArrayDeserializer = intArrayDeserializer;
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
    public ImmutableIntArray getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return ImmutableIntArray.of();
    }

    @Override
    public ImmutableIntArray deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return ImmutableIntArray.copyOf(intArrayDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
