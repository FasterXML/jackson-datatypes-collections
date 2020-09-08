package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import java.lang.reflect.Type;
import org.eclipse.collections.api.collection.ImmutableCollection;

class EclipseCollectionsTypeModifier extends TypeModifier {
    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context, TypeFactory typeFactory) {
        if (!type.isCollectionLikeType()) {
            JavaType collectionType = type.findSuperType(ImmutableCollection.class);
            if (collectionType != null) {
                return CollectionLikeType.upgradeFrom(type, collectionType.containedTypeOrUnknown(0));
            }
        }
        return type;
    }
}
