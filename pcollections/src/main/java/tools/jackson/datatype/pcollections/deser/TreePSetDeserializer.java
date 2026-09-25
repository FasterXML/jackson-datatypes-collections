package tools.jackson.datatype.pcollections.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import org.pcollections.TreePSet;

public class TreePSetDeserializer extends
        PCollectionsCollectionDeserializer<TreePSet<Object>>
{
    public TreePSetDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public TreePSetDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new TreePSetDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected TreePSet<Object> createEmptyCollection() {
        // Natural ordering: elements must be `Comparable`
        return (TreePSet) TreePSet.empty();
    }

    // `TreePSet` does not accept `null` elements
    @Override
    protected Object _nullElement(DeserializationContext ctxt)
        throws JacksonException
    {
        return ctxt.reportInputMismatch(this,
                "Cannot deserialize `null` element into %s: `null` elements not allowed",
                _containerType);
    }
}
