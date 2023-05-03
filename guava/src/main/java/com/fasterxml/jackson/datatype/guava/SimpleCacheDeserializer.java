package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.datatype.guava.deser.cache.GuavaCacheDeserializer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class SimpleCacheDeserializer 
    extends GuavaCacheDeserializer<Cache<Object,Object>> 
{
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public SimpleCacheDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
        TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) 
    {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    public SimpleCacheDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
        TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer,
        NullValueProvider nvp) 
    {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, nvp);
    }
    
    /*
    /**********************************************************************
    /* Abstract method overrides
    /**********************************************************************
     */

    @Override
    protected Cache<Object, Object> createCache() {
        return CacheBuilder.newBuilder().build();
    }

    @Override
    protected JsonDeserializer<?> _createContextual(MapLikeType t, 
        KeyDeserializer kd, TypeDeserializer vtd, JsonDeserializer<?> vd, NullValueProvider np) 
    {
        return new SimpleCacheDeserializer(t, kd, vtd, vd, np);
    }
}
