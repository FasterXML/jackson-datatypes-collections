package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import com.google.common.collect.TreeMultiset;

public class TreeMultisetDeserializer extends GuavaMultisetDeserializer<TreeMultiset<Object>>
{
    /**
     * Constructor that uses {@link MultisetConfig#DEFAULT} settings: note that this means
     * entries format ({@code {"element":...,"count":...}}) is expected since 3.3.
     *
     * @deprecated Since 3.3 use constructor that takes {@link MultisetConfig} to
     *    explicitly specify expected format
     */
    @Deprecated // since 3.3
    public TreeMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    /**
     * @since 3.3
     */
    public TreeMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, MultisetConfig config) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle, config);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TreeMultiset<Object> createMultiset() {
        if (_hasUntypedElements()) {
            return TreeMultiset.create(UntypedElementComparator.INSTANCE);
        }
        @SuppressWarnings("rawtypes")
        TreeMultiset<?> naturalOrder = TreeMultiset.<Comparable> create();
        return (TreeMultiset<Object>) naturalOrder;
    }

    @Override
    public GuavaCollectionDeserializer<TreeMultiset<Object>> withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new TreeMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle, _config);
    }
}
