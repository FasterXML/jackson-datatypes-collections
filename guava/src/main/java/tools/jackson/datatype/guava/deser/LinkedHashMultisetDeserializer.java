package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.LinkedHashMultiset;

public class LinkedHashMultisetDeserializer
    extends GuavaMultisetDeserializer<LinkedHashMultiset<Object>>
{
    /**
     * Constructor that uses {@link MultisetConfig#DEFAULT} settings: note that this means
     * entries format ({@code {"element":...,"count":...}}) is expected since 3.3.
     *
     * @deprecated Since 3.3 use constructor that takes {@link MultisetConfig} to
     *    explicitly specify expected format
     */
    @Deprecated // since 3.3
    public LinkedHashMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    /**
     * @since 3.3
     */
    public LinkedHashMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, MultisetConfig config) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle, config);
    }

    @Override
    protected LinkedHashMultiset<Object> createMultiset() {
        return LinkedHashMultiset.create();
    }

    @Override
    public GuavaCollectionDeserializer<LinkedHashMultiset<Object>> withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new LinkedHashMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle, _config);
    }
}
