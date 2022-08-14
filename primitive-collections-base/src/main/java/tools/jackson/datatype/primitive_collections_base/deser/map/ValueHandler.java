package tools.jackson.datatype.primitive_collections_base.deser.map;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;

/**
 * @author yawkat
 */
public interface ValueHandler<V extends ValueHandler<V>>
{
    V createContextualValue(DeserializationContext ctxt, BeanProperty property);

    // TYPE value(DeserializationContext ctx, JsonParser parser)
}
