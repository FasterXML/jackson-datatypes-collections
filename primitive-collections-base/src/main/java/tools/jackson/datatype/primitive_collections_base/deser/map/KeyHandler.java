package tools.jackson.datatype.primitive_collections_base.deser.map;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;

/**
 * @author yawkat
 */
public interface KeyHandler<K extends KeyHandler<K>> {
    K createContextualKey(DeserializationContext ctxt, BeanProperty property);

    // TYPE key(DeserializationContext ctx, String key);
}
