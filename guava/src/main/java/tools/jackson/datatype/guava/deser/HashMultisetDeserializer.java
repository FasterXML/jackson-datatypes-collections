package tools.jackson.datatype.guava.deser;


import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.HashMultiset;

public class HashMultisetDeserializer
    extends GuavaMultisetDeserializer<HashMultiset<Object>>
{
    /**
     * Constructor that uses {@link MultisetConfig#DEFAULT} settings: note that this means
     * entries format ({@code {"element":...,"count":...}}) is expected since 3.3.
     *
     * @deprecated Since 3.3 use constructor that takes {@link MultisetConfig} to
     *    explicitly specify expected format
     */
    @Deprecated // since 3.3
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
            NullValueProvider nuller, Boolean unwrapSingle, MultisetConfig config) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle, config);
    }

    @Override
    public HashMultisetDeserializer withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new HashMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle, _config);
    }

    @Override
    protected HashMultiset<Object> createMultiset() {
        return HashMultiset.<Object> create();
    }
}
