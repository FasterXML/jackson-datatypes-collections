package tools.jackson.datatype.eclipsecollections.deser.list;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializers;
import tools.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;

import org.eclipse.collections.api.collection.primitive.*;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.*;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.*;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;

public final class ImmutableListDeserializer {
    private ImmutableListDeserializer() {
    }

    public static final class Ref extends
            BaseRefCollectionDeserializer<ImmutableList<?>, MutableList<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(ImmutableList.class, elementType, typeDeserializer, deserializer);
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
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new ImmutableListDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializers.Boolean<ImmutableBooleanList, MutableBooleanList> {
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
            BaseCollectionDeserializers.Byte<ImmutableByteList, MutableByteList> {
        public static final ImmutableListDeserializer.Byte INSTANCE = new ImmutableListDeserializer.Byte();

        public Byte() {
            super(ImmutableByteCollection.class);
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
        protected ImmutableByteList finish(MutableByteList objects) {
            return objects.toImmutable();
        }
    }

    public static final class Short extends
            BaseCollectionDeserializers.Short<ImmutableShortList, MutableShortList> {
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
            BaseCollectionDeserializers.Char<ImmutableCharList, MutableCharList> {
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
            BaseCollectionDeserializers.Int<ImmutableIntList, MutableIntList> {
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
            BaseCollectionDeserializers.Float<ImmutableFloatList, MutableFloatList> {
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
            BaseCollectionDeserializers.Long<ImmutableLongList, MutableLongList> {
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
            BaseCollectionDeserializers.Double<ImmutableDoubleList, MutableDoubleList> {
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
