package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.google.common.primitives.ImmutableDoubleArray;

import java.io.IOException;

public final class ImmutableDoubleArrayDeserializer extends StdDeserializer<ImmutableDoubleArray> {

    private final JsonDeserializer<double[]> doubleArrayDeserializer;

    public ImmutableDoubleArrayDeserializer() {
        super(ImmutableDoubleArray.class);
        @SuppressWarnings("unchecked")
        JsonDeserializer<double[]> doubleArrayDeserializer = (JsonDeserializer<double[]>) PrimitiveArrayDeserializers.forType(double.class);
        this.doubleArrayDeserializer = doubleArrayDeserializer;
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
    public ImmutableDoubleArray getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return ImmutableDoubleArray.of();
    }

    @Override
    public ImmutableDoubleArray deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return ImmutableDoubleArray.copyOf(doubleArrayDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
