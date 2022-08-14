package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import com.google.common.collect.TreeMultiset;

public class TreeMultisetDeserializer extends GuavaMultisetDeserializer<TreeMultiset<Object>>
{
    public TreeMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TreeMultiset<Object> createMultiset() {
        @SuppressWarnings("rawtypes")
        TreeMultiset<?> naturalOrder = TreeMultiset.<Comparable> create();
        return (TreeMultiset<Object>) naturalOrder;
    }

    @Override
    public GuavaCollectionDeserializer<TreeMultiset<Object>> withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new TreeMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }
}
