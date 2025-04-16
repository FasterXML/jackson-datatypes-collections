package tools.jackson.datatype.pcollections;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.Deserializers;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import tools.jackson.datatype.pcollections.deser.*;

import org.pcollections.*;

/**
 * Custom deserializers module offers.
 */
public class PCollectionsDeserializers
    extends Deserializers.Base
{
    /**
     * We have plenty of collection types to support...
     */
    @Override
    public ValueDeserializer<?> findCollectionDeserializer(CollectionType type,
            DeserializationConfig config, BeanDescription.Supplier beanDescRef,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer)
    {
        Class<?> raw = type.getRawClass();

        // PCollection types
        if (PCollection.class.isAssignableFrom(raw)) {
            // We check these in order of most desirable to least desirable, using TreePVector as the most
            // desirable type if possible
            if (raw.isAssignableFrom(TreePVector.class)) {
                return new TreePVectorDeserializer(type, elementTypeDeserializer, elementDeserializer);
            }
            if (raw.isAssignableFrom(ConsPStack.class)) {
                return new ConsPStackDeserializer(type, elementTypeDeserializer, elementDeserializer);
            }
            if (raw.isAssignableFrom(MapPSet.class)) {
                return new HashTreePSetDeserializer(type, elementTypeDeserializer, elementDeserializer);
            }
            if (raw.isAssignableFrom(OrderedPSet.class)) {
                return new OrderedPSetDeserializer(type, elementTypeDeserializer, elementDeserializer);
            }
            if (raw.isAssignableFrom(MapPBag.class)) {
                return new HashTreePBagDeserializer(type, elementTypeDeserializer, elementDeserializer);
            }
        }
        return null;
    }

    @Override
    public ValueDeserializer<?> findMapDeserializer(MapType type,
            DeserializationConfig config, BeanDescription.Supplier beanDescRef,
            KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer)
    {
        Class<?> raw = type.getRawClass();

        if (PMap.class.isAssignableFrom(raw)) {
            if (raw.isAssignableFrom(HashPMap.class)) {
                return new HashTreePMapDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            }
        }

        return null;
    }

    @Override
    public boolean hasDeserializerFor(DeserializationConfig config,
            Class<?> valueType)
    {
        // NOTE: ok even if not all actually supported; just claims that we recognize
        // types and might have deserializers wrt polymorphic handling
        return PCollection.class.isAssignableFrom(valueType)
                || PMap.class.isAssignableFrom(valueType);
    }
}
