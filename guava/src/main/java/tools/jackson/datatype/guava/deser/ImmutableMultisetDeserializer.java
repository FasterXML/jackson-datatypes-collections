package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.guava.GuavaModule;

import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableMultiset;

public class ImmutableMultisetDeserializer extends GuavaImmutableMultisetDeserializer<ImmutableMultiset<Object>>
{
    public ImmutableMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        this(selfType, deser, typeDeser, nuller, unwrapSingle, true, GuavaModule.DEFAULT_MAX_MULTISET_SIZE);
    }

    /**
     * @since 3.3
     */
    public ImmutableMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, boolean asEntries, int maxSize) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle, asEntries, maxSize);
    }

    @Override
    protected Builder<Object> createBuilder() {
        return ImmutableMultiset.builder();
    }

    @Override
    public GuavaCollectionDeserializer<ImmutableMultiset<Object>> withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new ImmutableMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle, _asEntries, _maxSize);
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
