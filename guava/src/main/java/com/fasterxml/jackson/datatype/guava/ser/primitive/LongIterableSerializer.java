//package com.fasterxml.jackson.datatype.guava.ser.primitive;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.BeanProperty;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.type.TypeFactory;
//import org.eclipse.collections.api.LongIterable;
//import org.eclipse.collections.api.iterator.LongIterator;
//
//import java.io.IOException;
//
//public final class LongIterableSerializer extends GuavaPrimitiveIterableSerializer<LongIterable> {
//    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(long.class);
//
//    public LongIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
//        super(LongIterable.class, ELEMENT_TYPE, property, unwrapSingle);
//    }
//
//    @Override
//    protected LongIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
//        return new LongIterableSerializer(property, unwrapSingle);
//    }
//
//    @Override
//    protected void serializeContents(LongIterable value, JsonGenerator gen) throws IOException {
//        LongIterator iterator = value.longIterator();
//        while (iterator.hasNext()) {
//            gen.writeNumber(iterator.next());
//        }
//    }
//}
