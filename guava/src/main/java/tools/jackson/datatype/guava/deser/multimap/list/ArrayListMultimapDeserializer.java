package tools.jackson.datatype.guava.deser.multimap.list;

import java.lang.reflect.Method;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.guava.deser.multimap.GuavaMultimapDeserializer;

import com.google.common.collect.ArrayListMultimap;

/**
 * Provides deserialization for the Guava ArrayListMultimap class.
 *
 * @author mvolkhart
 */
public class ArrayListMultimapDeserializer
    extends GuavaMultimapDeserializer<ArrayListMultimap<Object,Object>>
{
    public ArrayListMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    protected ArrayListMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer,
            Method creatorMethod, NullValueProvider nvp, Boolean unwrapSingle) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, creatorMethod, nvp, unwrapSingle);
    }

    @Override
    protected ArrayListMultimap<Object, Object> createMultimap() {
        return ArrayListMultimap.create();
    }

    @Override
    protected ValueDeserializer<?> _createContextual(JavaType type,
            KeyDeserializer keyDeserializer, TypeDeserializer typeDeserializer,
            ValueDeserializer<?> elementDeserializer, Method method,
            NullValueProvider nvp, Boolean unwrapSingle) {
        return new ArrayListMultimapDeserializer(type, keyDeserializer, typeDeserializer,
                elementDeserializer, method, nvp, unwrapSingle);
    }
}
