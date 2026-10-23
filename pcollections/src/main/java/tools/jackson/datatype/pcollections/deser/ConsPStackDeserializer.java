package tools.jackson.datatype.pcollections.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import org.pcollections.ConsPStack;

public class ConsPStackDeserializer extends
        PCollectionsCollectionDeserializer<ConsPStack<Object>>
{
    public ConsPStackDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public ConsPStackDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new ConsPStackDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    /**
     * Since {@link ConsPStack#plus} prepends elements, contents are
     * reversed after reading to retain the order of the JSON array.
     */
    @Override
    protected ConsPStack<Object> _deserializeContents(JsonParser p,
            DeserializationContext ctxt) throws JacksonException
    {
        ConsPStack<Object> result = ConsPStack.empty();
        for (Object value : super._deserializeContents(p, ctxt)) {
            result = result.plus(value);
        }
        return result;
    }

    @Override
    protected ConsPStack<Object> createEmptyCollection() {
        return ConsPStack.empty();
    }
}
