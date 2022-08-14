package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.datatype.primitive_collections_base.ser.PrimitiveIterableSerializer;

import org.eclipse.collections.api.PrimitiveIterable;

public abstract class EclipsePrimitiveIterableSerializer<C extends PrimitiveIterable>
        extends PrimitiveIterableSerializer<C> {

    protected EclipsePrimitiveIterableSerializer(Class<C> type, JavaType elementType,
            BeanProperty property, Boolean unwrapSingle) {
        super(type, elementType, property, unwrapSingle);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, C value) {
        return value.isEmpty();
    }

    @Override
    public boolean hasSingleElement(C value) {
        if (value != null) {
            return value.size() == 1;
        }
        return false;
    }
}
