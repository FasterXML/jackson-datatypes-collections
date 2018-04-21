package com.fasterxml.jackson.datatype.eclipsecollections.deser.list;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.collection.primitive.ImmutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableByteCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableCharCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableIntCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableShortCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
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

public final class ImmutableListDeserializer {
    private ImmutableListDeserializer() {
    }

    public static final class Ref extends
            BaseCollectionDeserializer.Ref<ImmutableList<?>, MutableList<Object>> {
        public Ref(CollectionLikeType type, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(type, typeDeserializer, deserializer);
        }

        @Override
        protected MutableList<Object> createIntermediate() {
            return Lists.mutable.empty();
        }

        @Override
        protected ImmutableList<?> finish(MutableList<Object> objects) {
            return objects.toImmutable();
        }

        @Override
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new ImmutableListDeserializer.Ref(_containerType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializer.Boolean<ImmutableBooleanList, MutableBooleanList> {
        public static final ImmutableListDeserializer.Boolean INSTANCE = new ImmutableListDeserializer.Boolean();

        public Boolean() {
            super(ImmutableBooleanCollection.class);
        }

        @Override
        protected MutableBooleanList createIntermediate() {
            return BooleanLists.mutable.empty();
        }

        @Override
        protected ImmutableBooleanList finish(MutableBooleanList objects) {
            return objects.toImmutable();
        }
    }

    public static final class Byte extends
            BaseCollectionDeserializer.Byte<ImmutableByteList, MutableByteList> {
        public static final ImmutableListDeserializer.Byte INSTANCE = new ImmutableListDeserializer.Byte();

        public Byte() {
            super(ImmutableByteCollection.class);
        }

        @Override
        protected MutableByteList createIntermediate() {
            return ByteLists.mutable.empty();
        }

        @Override
        protected ImmutableByteList finish(MutableByteList objects) {
            return objects.toImmutable();
        }
    }

    public static final class Short extends
            BaseCollectionDeserializer.Short<ImmutableShortList, MutableShortList> {
        public static final ImmutableListDeserializer.Short INSTANCE = new ImmutableListDeserializer.Short();

        public Short() {
            super(ImmutableShortCollection.class);
        }

        @Override
        protected MutableShortList createIntermediate() {
            return ShortLists.mutable.empty();
        }

        @Override
        protected ImmutableShortList finish(MutableShortList objects) {
            return objects.toImmutable();
        }
    }

    public static final class Char extends
            BaseCollectionDeserializer.Char<ImmutableCharList, MutableCharList> {
        public static final ImmutableListDeserializer.Char INSTANCE = new ImmutableListDeserializer.Char();

        public Char() {
            super(ImmutableCharCollection.class);
        }

        @Override
        protected MutableCharList createIntermediate() {
            return CharLists.mutable.empty();
        }

        @Override
        protected ImmutableCharList finish(MutableCharList objects) {
            return objects.toImmutable();
        }
    }

    public static final class Int extends
            BaseCollectionDeserializer.Int<ImmutableIntList, MutableIntList> {
        public static final ImmutableListDeserializer.Int INSTANCE = new ImmutableListDeserializer.Int();

        public Int() {
            super(ImmutableIntCollection.class);
        }

        @Override
        protected MutableIntList createIntermediate() {
            return IntLists.mutable.empty();
        }

        @Override
        protected ImmutableIntList finish(MutableIntList objects) {
            return objects.toImmutable();
        }
    }

    public static final class Float extends
            BaseCollectionDeserializer.Float<ImmutableFloatList, MutableFloatList> {
        public static final ImmutableListDeserializer.Float INSTANCE = new ImmutableListDeserializer.Float();

        public Float() {
            super(ImmutableFloatCollection.class);
        }

        @Override
        protected MutableFloatList createIntermediate() {
            return FloatLists.mutable.empty();
        }

        @Override
        protected ImmutableFloatList finish(MutableFloatList objects) {
            return objects.toImmutable();
        }
    }

    public static final class Long extends
            BaseCollectionDeserializer.Long<ImmutableLongList, MutableLongList> {
        public static final ImmutableListDeserializer.Long INSTANCE = new ImmutableListDeserializer.Long();

        public Long() {
            super(ImmutableLongCollection.class);
        }

        @Override
        protected MutableLongList createIntermediate() {
            return LongLists.mutable.empty();
        }

        @Override
        protected ImmutableLongList finish(MutableLongList objects) {
            return objects.toImmutable();
        }
    }

    public static final class Double extends
            BaseCollectionDeserializer.Double<ImmutableDoubleList, MutableDoubleList> {
        public static final ImmutableListDeserializer.Double INSTANCE = new ImmutableListDeserializer.Double();

        public Double() {
            super(ImmutableDoubleCollection.class);
        }

        @Override
        protected MutableDoubleList createIntermediate() {
            return DoubleLists.mutable.empty();
        }

        @Override
        protected ImmutableDoubleList finish(MutableDoubleList objects) {
            return objects.toImmutable();
        }
    }
}
