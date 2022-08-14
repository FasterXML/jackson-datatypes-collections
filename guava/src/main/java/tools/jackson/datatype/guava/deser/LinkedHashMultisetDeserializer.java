package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.LinkedHashMultiset;

public class LinkedHashMultisetDeserializer
    extends GuavaMultisetDeserializer<LinkedHashMultiset<Object>>
{
    public LinkedHashMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    protected LinkedHashMultiset<Object> createMultiset() {
        return LinkedHashMultiset.create();
    }

    @Override
    public GuavaCollectionDeserializer<LinkedHashMultiset<Object>> withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new LinkedHashMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }
}
