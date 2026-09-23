package tools.jackson.datatype.pcollections.deser;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapType;
import org.pcollections.TreePMap;

public class TreePMapDeserializer
 extends PCollectionsMapDeserializer<TreePMap<Object, Object>>
{
    public TreePMapDeserializer(MapType type, KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser)
    {
        super(type, keyDeser, typeDeser, deser);
    }

    @Override
    public TreePMapDeserializer withResolved(KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, ValueDeserializer<?> valueDeser) {
        return new TreePMapDeserializer(_mapType, keyDeser,
                typeDeser, valueDeser);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected TreePMap<Object, Object> createEmptyMap() {
        // Natural ordering: keys must be `Comparable`
        return (TreePMap) TreePMap.empty();
    }
}
