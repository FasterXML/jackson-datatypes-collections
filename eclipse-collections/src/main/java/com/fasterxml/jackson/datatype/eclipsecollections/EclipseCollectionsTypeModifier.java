package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import java.lang.reflect.Type;
import java.util.Collection;

public final class EclipseCollectionsTypeModifier extends TypeModifier {
    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context, TypeFactory typeFactory) {
        if (type.isReferenceType() || type.isContainerType()) {
            return type;
        }

        Class<?> rawClass = type.getRawClass();
        //noinspection SuspiciousMethodCalls
        if (EclipseCollectionsDeserializers.REFERENCE_TYPES.contains(rawClass)) {
            if (Collection.class.isAssignableFrom(rawClass)) {
                return CollectionType.upgradeFrom(type, type.containedTypeOrUnknown(0));
            } else {
                return CollectionLikeType.upgradeFrom(type, type.containedTypeOrUnknown(0));
            }
        }

        /*
        //noinspection SuspiciousMethodCalls
        if (EclipseCollectionsDeserializers.PRIMITIVE_DESERIALIZERS.containsKey(rawClass)) {
            if (BooleanIterable.class.isAssignableFrom(rawClass)) {
                return CollectionLikeType.upgradeFrom(type, typeFactory.constructType(int.class));
            } else if (ByteIterable.class.isAssignableFrom(rawClass)) {
                return CollectionLikeType.upgradeFrom(type, typeFactory.constructType(byte.class));
            } else if (ShortIterable.class.isAssignableFrom(rawClass)) {
                return CollectionLikeType.upgradeFrom(type, typeFactory.constructType(short.class));
            } else if (CharIterable.class.isAssignableFrom(rawClass)) {
                return CollectionLikeType.upgradeFrom(type, typeFactory.constructType(char.class));
            } else if (IntIterable.class.isAssignableFrom(rawClass)) {
                return CollectionLikeType.upgradeFrom(type, typeFactory.constructType(int.class));
            } else if (FloatIterable.class.isAssignableFrom(rawClass)) {
                return CollectionLikeType.upgradeFrom(type, typeFactory.constructType(float.class));
            } else if (LongIterable.class.isAssignableFrom(rawClass)) {
                return CollectionLikeType.upgradeFrom(type, typeFactory.constructType(long.class));
            } else if (DoubleIterable.class.isAssignableFrom(rawClass)) {
                return CollectionLikeType.upgradeFrom(type, typeFactory.constructType(double.class));
            } else {
                assert false : rawClass;
            }
        }
        */

        return type;
    }
}
