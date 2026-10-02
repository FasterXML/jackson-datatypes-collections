package tools.jackson.datatype.guava.deser;


import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.HashMultiset;

public class HashMultisetDeserializer
    extends GuavaMultisetDeserializer<HashMultiset<Object>>
{
    public HashMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    /**
     * @since 3.3
     */
    public HashMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, boolean asEntries) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle, asEntries);
    }

    @Override
    public HashMultisetDeserializer withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new HashMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle, _asEntries);
    }

    @Override
    protected HashMultiset<Object> createMultiset() {
        return HashMultiset.<Object> create();
    }
}
