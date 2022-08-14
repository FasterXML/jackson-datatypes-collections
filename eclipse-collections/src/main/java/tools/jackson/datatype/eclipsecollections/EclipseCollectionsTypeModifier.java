package tools.jackson.datatype.eclipsecollections;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.CollectionLikeType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.type.TypeModifier;
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
