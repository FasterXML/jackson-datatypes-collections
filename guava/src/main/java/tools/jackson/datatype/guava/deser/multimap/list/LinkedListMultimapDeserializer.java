package tools.jackson.datatype.guava.deser.multimap.list;

import java.lang.reflect.Method;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.guava.deser.multimap.GuavaMultimapDeserializer;

import com.google.common.collect.LinkedListMultimap;

/**
 * Provides deserialization for the Guava LinkedListMultimap class.
 *
 * @author mvolkhart
 */
public class LinkedListMultimapDeserializer
    extends GuavaMultimapDeserializer<LinkedListMultimap<Object,Object>>
{
    public LinkedListMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    protected LinkedListMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer,
            Method creatorMethod, NullValueProvider nvp, Boolean unwrapSingle) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, creatorMethod, nvp, unwrapSingle);
    }

    @Override
    protected LinkedListMultimap<Object, Object> createMultimap() {
        return LinkedListMultimap.create();
    }

    @Override
    protected ValueDeserializer<?> _createContextual(JavaType type,
            KeyDeserializer keyDeserializer, TypeDeserializer typeDeserializer,
            ValueDeserializer<?> elementDeserializer, Method method, NullValueProvider nvp,
            Boolean unwrapSingle) {
        return new LinkedListMultimapDeserializer(type, keyDeserializer, typeDeserializer,
                elementDeserializer, method, nvp, unwrapSingle);
    }
}
