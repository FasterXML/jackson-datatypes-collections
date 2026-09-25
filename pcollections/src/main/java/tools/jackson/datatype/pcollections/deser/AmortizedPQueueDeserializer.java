package tools.jackson.datatype.pcollections.deser;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import org.pcollections.AmortizedPQueue;

public class AmortizedPQueueDeserializer extends
        PCollectionsCollectionDeserializer<AmortizedPQueue<Object>>
{
    public AmortizedPQueueDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public AmortizedPQueueDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new AmortizedPQueueDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    @Override
    protected AmortizedPQueue<Object> createEmptyCollection() {
        return AmortizedPQueue.empty();
    }
}
