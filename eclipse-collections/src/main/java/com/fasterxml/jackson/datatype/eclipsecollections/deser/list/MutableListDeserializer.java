package com.fasterxml.jackson.datatype.eclipsecollections.deser.list;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ShortLists;

public final class MutableListDeserializer {
    private MutableListDeserializer() {
    }

    public static final class Ref extends
            BaseCollectionDeserializer.Ref<MutableList<?>, MutableList<Object>> {
        public Ref(CollectionLikeType type, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(type, typeDeserializer, deserializer);
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
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new MutableListDeserializer.Ref(_containerType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializer.Boolean<MutableBooleanList, MutableBooleanList> {
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
            BaseCollectionDeserializer.Byte<MutableByteList, MutableByteList> {
        public static final MutableListDeserializer.Byte INSTANCE = new MutableListDeserializer.Byte();

        public Byte() {
            super(ByteIterable.class);
        }

        @Override
        protected MutableByteList createIntermediate() {
            return ByteLists.mutable.empty();
        }

        @Override
        protected MutableByteList finish(MutableByteList objects) {
            return objects;
        }
    }

    public static final class Short extends
            BaseCollectionDeserializer.Short<MutableShortList, MutableShortList> {
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
            BaseCollectionDeserializer.Char<MutableCharList, MutableCharList> {
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
            BaseCollectionDeserializer.Int<MutableIntList, MutableIntList> {
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
            BaseCollectionDeserializer.Float<MutableFloatList, MutableFloatList> {
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
            BaseCollectionDeserializer.Long<MutableLongList, MutableLongList> {
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
            BaseCollectionDeserializer.Double<MutableDoubleList, MutableDoubleList> {
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
