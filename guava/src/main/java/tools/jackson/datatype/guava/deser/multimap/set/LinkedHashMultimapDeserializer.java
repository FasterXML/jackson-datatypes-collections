package tools.jackson.datatype.guava.deser.multimap.set;

import java.lang.reflect.Method;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.guava.deser.multimap.GuavaMultimapDeserializer;

import com.google.common.collect.LinkedHashMultimap;

/**
 * Provides deserialization for the Guava LinkedHashMultimap class.
 *
 * @author mvolkhart
 */
public class LinkedHashMultimapDeserializer
    extends GuavaMultimapDeserializer<LinkedHashMultimap<Object, Object>>
{
    public LinkedHashMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    public LinkedHashMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer,
            Method creatorMethod, NullValueProvider nvp, Boolean unwrapSingle) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, creatorMethod, nvp, unwrapSingle);
    }

    @Override
    protected LinkedHashMultimap<Object, Object> createMultimap() {
        return LinkedHashMultimap.create();
    }

    @Override
    protected ValueDeserializer<?> _createContextual(JavaType type,
            KeyDeserializer keyDeserializer, TypeDeserializer typeDeserializer,
            ValueDeserializer<?> elementDeserializer, Method method, NullValueProvider nvp,
            Boolean unwrapSingle)
    {
        return new LinkedHashMultimapDeserializer(type, keyDeserializer, typeDeserializer,
                elementDeserializer, method, nvp, unwrapSingle);
    }
}
