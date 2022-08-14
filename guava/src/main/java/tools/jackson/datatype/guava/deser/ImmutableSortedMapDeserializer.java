package tools.jackson.datatype.guava.deser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;

import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSortedMap;

public class ImmutableSortedMapDeserializer
    extends GuavaImmutableMapDeserializer<ImmutableSortedMap<Object, Object>>
{
    public ImmutableSortedMapDeserializer(JavaType type, KeyDeserializer keyDeser,
            ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller) {
        super(type, keyDeser, valueDeser, typeDeser, nuller);
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) {
        return ImmutableSortedMap.of();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Builder<Object, Object> createBuilder() {
        // Not quite sure what to do with sorting/ordering; may require better
        // support either via annotations, or via custom serialization (bean
        // style that includes ordering aspects)
        @SuppressWarnings("rawtypes")
        ImmutableSortedMap.Builder<?, Object> naturalOrder = ImmutableSortedMap.<Comparable, Object>naturalOrder();
        ImmutableSortedMap.Builder<Object, Object> builder = (ImmutableSortedMap.Builder<Object, Object>) naturalOrder;
        return builder;
    }

    @Override
    public GuavaMapDeserializer<ImmutableSortedMap<Object, Object>> withResolved(KeyDeserializer keyDeser,
            ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller)
    {
        return new ImmutableSortedMapDeserializer(_containerType, keyDeser, valueDeser, typeDeser, nuller);
    }
}
