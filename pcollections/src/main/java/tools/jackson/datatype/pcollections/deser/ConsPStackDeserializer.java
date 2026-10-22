package tools.jackson.datatype.pcollections.deser;

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

    @Override
    protected ConsPStack<Object> _deserializeContents(JsonParser p,
            DeserializationContext ctxt) {
        // Adding to a stack prepends each element; reverse the accumulated
        // contents once more to retain their order in the JSON array.
        return ConsPStack.empty().plusAll(super._deserializeContents(p, ctxt));
    }

    @Override
    protected ConsPStack<Object> createEmptyCollection() {
        return ConsPStack.empty();
    }
}
