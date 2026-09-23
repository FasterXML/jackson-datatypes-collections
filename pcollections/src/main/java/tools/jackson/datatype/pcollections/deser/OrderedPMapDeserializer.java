package tools.jackson.datatype.pcollections.deser;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapType;
import org.pcollections.OrderedPMap;

public class OrderedPMapDeserializer
 extends PCollectionsMapDeserializer<OrderedPMap<Object, Object>>
{
    public OrderedPMapDeserializer(MapType type, KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser)
    {
        super(type, keyDeser, typeDeser, deser);
    }

    @Override
    public OrderedPMapDeserializer withResolved(KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, ValueDeserializer<?> valueDeser) {
        return new OrderedPMapDeserializer(_mapType, keyDeser,
                typeDeser, valueDeser);
    }

    @Override
    protected OrderedPMap<Object, Object> createEmptyMap() {
        return OrderedPMap.empty();
    }
}
