package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableSortedMultiset;

public class ImmutableSortedMultisetDeserializer extends GuavaImmutableCollectionDeserializer<ImmutableSortedMultiset<Object>>
{
    public ImmutableSortedMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    public GuavaCollectionDeserializer<ImmutableSortedMultiset<Object>> withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new ImmutableSortedMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    protected Builder<Object> createBuilder() {
        /* This is suboptimal. See the considerations in ImmutableSortedSetDeserializer. */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Builder<Object> builder =  (Builder) ImmutableSortedMultiset.naturalOrder();
        return builder;
    }

    @Override
    protected ImmutableSortedMultiset<Object> _createEmpty(DeserializationContext ctxt) {
        return ImmutableSortedMultiset.of();
    }

    @Override
    protected ImmutableSortedMultiset<Object> _createWithSingleElement(DeserializationContext ctxt,
            Object value) {
        return (ImmutableSortedMultiset<Object>) createBuilder()
                .add(value)
                .build();
    }
}
