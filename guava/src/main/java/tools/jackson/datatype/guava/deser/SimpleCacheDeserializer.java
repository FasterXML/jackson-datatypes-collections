package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapLikeType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * {@link GuavaCacheDeserializer} class implementation for deserializing Guava {@link Cache} instances.
 *
 * @since 2.16
 */
public class SimpleCacheDeserializer 
    extends GuavaCacheDeserializer<Cache<Object,Object>> 
{
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public SimpleCacheDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
        TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) 
    {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    public SimpleCacheDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
        TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer,
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
    protected ValueDeserializer<?> _createContextual(MapLikeType t, 
        KeyDeserializer kd, TypeDeserializer vtd, ValueDeserializer<?> vd, NullValueProvider np) 
    {
        return new SimpleCacheDeserializer(t, kd, vtd, vd, np);
    }
}
