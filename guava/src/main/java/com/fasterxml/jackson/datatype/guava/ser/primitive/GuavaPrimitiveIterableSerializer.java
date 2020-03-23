//package com.fasterxml.jackson.datatype.guava.ser.primitive;
//
//import com.fasterxml.jackson.databind.BeanProperty;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.SerializerProvider;
//
//import java.util.List;
//
//public abstract class GuavaPrimitiveIterableSerializer<C extends List>
//        extends PrimitiveIterableSerializer<C> {
//
//    protected GuavaPrimitiveIterableSerializer(Class<C> type, JavaType elementType,
//                                               BeanProperty property, Boolean unwrapSingle) {
//        super(type, elementType, property, unwrapSingle);
//    }
//
//    @Override
//    public boolean isEmpty(SerializerProvider prov, C value) {
//        return value.isEmpty();
//    }
//
//    @Override
//    public boolean hasSingleElement(C value) {
//        if (value != null) {
//            return value.size() == 1;
//        }
//        return false;
//    }
//}
