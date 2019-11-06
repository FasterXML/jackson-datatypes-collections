package com.fasterxml.jackson.datatype.eclipsecollections.deser.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.map.*;
import org.eclipse.collections.api.map.*;
import org.eclipse.collections.api.map.primitive.*;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.*;

import java.io.IOException;

public final class TypeHandlerPairs {

    private TypeHandlerPairs() {}

    /* with
        byte|char|short|int|long|float|double|object key
        short|byte|char|int|long|float|double|object|boolean value
    */

    /* define KeyHandlerType //
    // if object key //RefKeyHandler// elif !(object key) //PrimitiveKVHandler.Byte// endif //
    // enddefine */

    /* define ValueHandlerType //
    // if object value //RefValueHandler// elif !(object value) //PrimitiveKVHandler.Short// endif //
    // enddefine */

    /* define MapType //
    // if object key object value //MutableMap<Object, Object>
    // elif object key //MutableObjectShortMap<Object>
    // elif object value //MutableByteObjectMap<Object>
    // elif !(object key) && !(object value) //MutableByteShortMap
    // endif //
    // enddefine */

    private static final TypeHandlerPair</*MapType*/MutableByteShortMap/**/,
            /*KeyHandlerType*/PrimitiveKVHandler.Byte/**/,
            /*ValueHandlerType*/PrimitiveKVHandler.Short/**/> BYTE_SHORT =
            new TypeHandlerPair</*MapType*/MutableByteShortMap/**/,
                    /*KeyHandlerType*/PrimitiveKVHandler.Byte/**/,
                    /*ValueHandlerType*/PrimitiveKVHandler.Short/**/>() {
                @Override
                public /*KeyHandlerType*/PrimitiveKVHandler.Byte/**/ keyHandler(JavaType type) {
                    return /* if !(object key) */PrimitiveKVHandler.Byte.INSTANCE
                            /* elif object key //new RefKeyHandler(type, null)// endif */;
                }

                @Override
                public /*ValueHandlerType*/PrimitiveKVHandler.Short/**/ valueHandler(JavaType type) {
                    return /* if !(object value) */PrimitiveKVHandler.Short.INSTANCE
                            /* elif object value //new RefValueHandler(type, null, null)// endif */;
                }

                @Override
                public /*MapType*/MutableByteShortMap/**/ createEmpty() {
                    return /* if !(object key object value) */ByteShortMaps
                            /* elif object key object value //Maps// endif */.mutable.empty();
                }

                @Override
                public void add(
                        /*MapType*/MutableByteShortMap/**/ target,
                        /*KeyHandlerType*/PrimitiveKVHandler.Byte/**/ kh,
                        /*ValueHandlerType*/PrimitiveKVHandler.Short/**/ vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    /* endwith */


    static void addDeserializers() {
        EclipseMapDeserializers.add(true, true, MutableMap.class, OBJECT_OBJECT, null);
        EclipseMapDeserializers.add(true, true, MutableMapIterable.class, OBJECT_OBJECT, null);
        EclipseMapDeserializers.add(true, true, MapIterable.class, OBJECT_OBJECT, null);
        EclipseMapDeserializers.add(true, true, UnsortedMapIterable.class, OBJECT_OBJECT, null);
        EclipseMapDeserializers.add(
                true, true, ImmutableMap.class, OBJECT_OBJECT, MutableMap::toImmutable);
        EclipseMapDeserializers.add(
                true, true, ImmutableMapIterable.class, OBJECT_OBJECT, MutableMap::toImmutable);

        /* with
            byte|char|short|int|long|float|double|object key
            short|byte|char|int|long|float|double|object|boolean value
        */
        /* if !(object key object value) */

        EclipseMapDeserializers.add(
                /* if !(object key) */false/* elif object key //true// endif */,
                /* if !(object value) */false/* elif object value //true// endif */,
                ByteShortMap.class, BYTE_SHORT, null);
        EclipseMapDeserializers.add(
                /* if !(object key) */false/* elif object key //true// endif */,
                /* if !(object value) */false/* elif object value //true// endif */,
                MutableByteShortMap.class, BYTE_SHORT, null);
        EclipseMapDeserializers.add(
                /* if !(object key) */false/* elif object key //true// endif */,
                /* if !(object value) */false/* elif object value //true// endif */,
                ImmutableByteShortMap.class, BYTE_SHORT, ByteShortMap::toImmutable);
        /* endif */
        /* endwith */
    }
}