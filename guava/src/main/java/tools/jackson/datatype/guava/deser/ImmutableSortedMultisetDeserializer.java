package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableSortedMultiset;

public class ImmutableSortedMultisetDeserializer extends GuavaImmutableMultisetDeserializer<ImmutableSortedMultiset<Object>>
{
    /**
     * Constructor that uses {@link MultisetConfig#DEFAULT} settings: note that this means
     * entries format ({@code {"element":...,"count":...}}) is expected since 3.3.
     *
     * @deprecated Since 3.3 use constructor that takes {@link MultisetConfig} to
     *    explicitly specify expected format
     */
    @Deprecated // since 3.3
    public ImmutableSortedMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        this(selfType, deser, typeDeser, nuller, unwrapSingle, MultisetConfig.DEFAULT);
    }

    /**
     * @since 3.3
     */
    public ImmutableSortedMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, MultisetConfig config) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle, config);
    }

    @Override
    public GuavaCollectionDeserializer<ImmutableSortedMultiset<Object>> withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new ImmutableSortedMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle, _config);
    }

    @Override
    protected Builder<Object> createBuilder() {
        /* This is suboptimal. See the considerations in ImmutableSortedSetDeserializer. */
        if (_hasUntypedElements()) {
            return ImmutableSortedMultiset.orderedBy(UntypedElementComparator.INSTANCE);
        }
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
            Object value)
        throws JacksonException
    {
        return _buildWithSingleElement(ctxt, value);
    }

    @Override
    protected boolean _requiresComparableElements() {
        return true;
    }
}
