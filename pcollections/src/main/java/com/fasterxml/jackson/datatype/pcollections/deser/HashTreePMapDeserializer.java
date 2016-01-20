package com.fasterxml.jackson.datatype.pcollections.deser;


import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

public class HashTreePMapDeserializer
 extends PCollectionsMapDeserializer<HashPMap<Object, Object>>
{
    public HashTreePMapDeserializer(MapType type, KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser)
    {
        super(type, keyDeser, typeDeser, deser);
    }

    @Override
    public HashTreePMapDeserializer withResolved(KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new HashTreePMapDeserializer(_mapType, keyDeser,
                typeDeser, valueDeser);
    }

    @Override
    protected HashPMap<Object, Object> createEmptyMap() {
        return HashTreePMap.empty();
    }
}
