package tools.jackson.datatype.pcollections.deser;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import org.pcollections.HashTreePBag;
import org.pcollections.MapPBag;

public class HashTreePBagDeserializer extends
        PCollectionsCollectionDeserializer<MapPBag<Object>>
{
    public HashTreePBagDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public HashTreePBagDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new HashTreePBagDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    protected MapPBag<Object> createEmptyCollection() {
        return HashTreePBag.empty();
    }
}