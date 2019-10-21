package com.fasterxml.jackson.datatype.eclipsecollections.deser.list;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializers;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;
import org.eclipse.collections.api.*;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.*;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.*;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;

public final class MutableListDeserializer {
    private MutableListDeserializer() {
    }

    public static final class Ref extends
            BaseRefCollectionDeserializer<MutableList<?>, MutableList<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(MutableList.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableList<Object> createIntermediate() {
            return Lists.mutable.empty();
        }

        @Override
        protected MutableList<?> finish(MutableList<Object> objects) {
            return objects;
        }

        @Override
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new MutableListDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializers.Boolean<MutableBooleanList, MutableBooleanList> {
        public static final MutableListDeserializer.Boolean INSTANCE = new MutableListDeserializer.Boolean();

        public Boolean() {
            super(BooleanIterable.class);
        }

        @Override
        protected MutableBooleanList createIntermediate() {
            return BooleanLists.mutable.empty();
        }

        @Override
        protected MutableBooleanList finish(MutableBooleanList objects) {
            return objects;
        }
    }

    public static final class Byte extends
            BaseCollectionDeserializers.Byte<MutableByteList, MutableByteList> {
        public static final MutableListDeserializer.Byte INSTANCE = new MutableListDeserializer.Byte();

        public Byte() {
            super(ByteIterable.class);
        }

        @Override
        protected MutableByteList createIntermediate() {
            return ByteLists.mutable.empty();
        }

        @Override
        protected MutableByteList createIntermediate(int expectedSize) {
            return new ByteArrayList(expectedSize);
        }

        @Override
        protected MutableByteList finish(MutableByteList objects) {
            return objects;
        }
    }

    public static final class Short extends
            BaseCollectionDeserializers.Short<MutableShortList, MutableShortList> {
        public static final MutableListDeserializer.Short INSTANCE = new MutableListDeserializer.Short();

        public Short() {
            super(ShortIterable.class);
        }

        @Override
        protected MutableShortList createIntermediate() {
            return ShortLists.mutable.empty();
        }

        @Override
        protected MutableShortList finish(MutableShortList objects) {
            return objects;
        }
    }

    public static final class Char extends
            BaseCollectionDeserializers.Char<MutableCharList, MutableCharList> {
        public static final MutableListDeserializer.Char INSTANCE = new MutableListDeserializer.Char();

        public Char() {
            super(CharIterable.class);
        }

        @Override
        protected MutableCharList createIntermediate() {
            return CharLists.mutable.empty();
        }

        @Override
        protected MutableCharList finish(MutableCharList objects) {
            return objects;
        }
    }

    public static final class Int extends
            BaseCollectionDeserializers.Int<MutableIntList, MutableIntList> {
        public static final MutableListDeserializer.Int INSTANCE = new MutableListDeserializer.Int();

        public Int() {
            super(IntIterable.class);
        }

        @Override
        protected MutableIntList createIntermediate() {
            return IntLists.mutable.empty();
        }

        @Override
        protected MutableIntList finish(MutableIntList objects) {
            return objects;
        }
    }

    public static final class Float extends
            BaseCollectionDeserializers.Float<MutableFloatList, MutableFloatList> {
        public static final MutableListDeserializer.Float INSTANCE = new MutableListDeserializer.Float();

        public Float() {
            super(FloatIterable.class);
        }

        @Override
        protected MutableFloatList createIntermediate() {
            return FloatLists.mutable.empty();
        }

        @Override
        protected MutableFloatList finish(MutableFloatList objects) {
            return objects;
        }
    }

    public static final class Long extends
            BaseCollectionDeserializers.Long<MutableLongList, MutableLongList> {
        public static final MutableListDeserializer.Long INSTANCE = new MutableListDeserializer.Long();

        public Long() {
            super(LongIterable.class);
        }

        @Override
        protected MutableLongList createIntermediate() {
            return LongLists.mutable.empty();
        }

        @Override
        protected MutableLongList finish(MutableLongList objects) {
            return objects;
        }
    }

    public static final class Double extends
            BaseCollectionDeserializers.Double<MutableDoubleList, MutableDoubleList> {
        public static final MutableListDeserializer.Double INSTANCE = new MutableListDeserializer.Double();

        public Double() {
            super(DoubleIterable.class);
        }

        @Override
        protected MutableDoubleList createIntermediate() {
            return DoubleLists.mutable.empty();
        }

        @Override
        protected MutableDoubleList finish(MutableDoubleList objects) {
            return objects;
        }
    }
}
