package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableMultiset;

public class ImmutableMultisetDeserializer extends GuavaImmutableMultisetDeserializer<ImmutableMultiset<Object>>
{
    /**
     * Constructor that uses {@link MultisetConfig#DEFAULT} settings: note that this means
     * entries format ({@code {"element":...,"count":...}}) is expected since 3.3.
     *
     * @deprecated Since 3.3 use constructor that takes {@link MultisetConfig} to
     *    explicitly specify expected format
     */
    @Deprecated // since 3.3
    public ImmutableMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        this(selfType, deser, typeDeser, nuller, unwrapSingle, MultisetConfig.DEFAULT);
    }

    /**
     * @since 3.3
     */
    public ImmutableMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, MultisetConfig config) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle, config);
    }

    @Override
    protected Builder<Object> createBuilder() {
        return ImmutableMultiset.builder();
    }

    @Override
    public GuavaCollectionDeserializer<ImmutableMultiset<Object>> withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new ImmutableMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle, _config);
    }

    @Override
    protected ImmutableMultiset<Object> _createEmpty(DeserializationContext ctxt) {
        return ImmutableMultiset.of();
    }

    @Override
    protected ImmutableMultiset<Object> _createWithSingleElement(DeserializationContext ctxt, Object value) {
        return ImmutableMultiset.of(value);
    }
}
