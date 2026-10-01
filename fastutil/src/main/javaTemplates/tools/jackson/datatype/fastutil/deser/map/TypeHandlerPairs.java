package tools.jackson.datatype.fastutil.deser.map;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JacksonException;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.datatype.fastutil.deser.ContainerFamily;
import tools.jackson.datatype.primitive_collections_base.deser.map.*;

import it.unimi.dsi.fastutil.bytes.*;
import it.unimi.dsi.fastutil.chars.*;
import it.unimi.dsi.fastutil.doubles.*;
import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.shorts.*;

final class TypeHandlerPairs {

    private TypeHandlerPairs() {}

    /* with
        byte|char|short|int|long|float|double|object key
        short|byte|char|int|long|float|double|object|boolean value
    */
    /* if !(object key object value) */

    /* define KeyHandlerType //
    // if object key //RefKeyHandler// elif !(object key) //PrimitiveKVHandler.Byte// endif //
    // enddefine */

    /* define ValueHandlerType //
    // if object value //RefValueHandler// elif !(object value) //PrimitiveKVHandler.Short// endif //
    // enddefine */

    /* define MapType //
    // if object key //Object2ShortMap<Object>
    // elif object value //Byte2ObjectMap<Object>
    // elif !(object key) && !(object value) //Byte2ShortMap
    // endif //
    // enddefine */

    private static final TypeHandlerPair</*MapType*/Byte2ShortMap/**/,
            /*KeyHandlerType*/PrimitiveKVHandler.Byte/**/,
            /*ValueHandlerType*/PrimitiveKVHandler.Short/**/> BYTE_SHORT =
            new TypeHandlerPair</*MapType*/Byte2ShortMap/**/,
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
                public /*MapType*/Byte2ShortMap/**/ createEmpty() {
                    // Instances are created by `ContainerFamily` instead
                    throw new UnsupportedOperationException();
                }

                @Override
                public void add(
                        /*MapType*/Byte2ShortMap/**/ target,
                        /*KeyHandlerType*/PrimitiveKVHandler.Byte/**/ kh,
                        /*ValueHandlerType*/PrimitiveKVHandler.Short/**/ vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws JacksonException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    /* endif */
    /* endwith */

    static void register() {
        /* with
            byte|char|short|int|long|float|double|object key
            short|byte|char|int|long|float|double|object|boolean value
        */
        /* if !(object key object value) */
        FastutilMapDeserializers.add(
                /* if !(object key) */false/* elif object key //true// endif */,
                /* if !(object value) */false/* elif object value //true// endif */,
                new ContainerFamily</*MapType*/Byte2ShortMap/**/>(Byte2ShortMap.class)
                        .add(Byte2ShortSortedMap.class, Byte2ShortRBTreeMap::new, Byte2ShortRBTreeMap::new)
                        .add(Byte2ShortMap.class, Byte2ShortOpenHashMap::new, Byte2ShortLinkedOpenHashMap::new),
                BYTE_SHORT);
        /* endif */
        /* endwith */
    }
}
