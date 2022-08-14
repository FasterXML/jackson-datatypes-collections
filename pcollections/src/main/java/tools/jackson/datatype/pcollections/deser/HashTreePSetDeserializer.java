package tools.jackson.datatype.pcollections.deser;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import org.pcollections.HashTreePSet;
import org.pcollections.MapPSet;

public class HashTreePSetDeserializer extends
        PCollectionsCollectionDeserializer<MapPSet<Object>>
{
    public HashTreePSetDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public HashTreePSetDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new HashTreePSetDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    protected MapPSet<Object> createEmptyCollection() {
        return HashTreePSet.empty();
    }
}
