package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializers;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.*;
import org.eclipse.collections.impl.bag.mutable.primitive.ByteHashBag;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.primitive.*;

public final class ImmutableBagDeserializer {
    private ImmutableBagDeserializer() {
    }

    public static final class Ref extends
            BaseRefCollectionDeserializer<ImmutableBag<?>, MutableBag<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(ImmutableBag.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableBag<Object> createIntermediate() {
            return Bags.mutable.empty();
        }

        @Override
        protected ImmutableBag<?> finish(MutableBag<Object> objects) {
            return objects.toImmutable();
        }

        @Override
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new ImmutableBagDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializers.Boolean<ImmutableBooleanBag, MutableBooleanBag> {
        public static final ImmutableBagDeserializer.Boolean INSTANCE = new ImmutableBagDeserializer.Boolean();

        public Boolean() {
            super(ImmutableBooleanBag.class);
        }

        @Override
        protected MutableBooleanBag createIntermediate() {
            return BooleanBags.mutable.empty();
        }

        @Override
        protected ImmutableBooleanBag finish(MutableBooleanBag objects) {
            return objects.toImmutable();
        }
    }

    public static final class Byte extends
            BaseCollectionDeserializers.Byte<ImmutableByteBag, MutableByteBag> {
        public static final ImmutableBagDeserializer.Byte INSTANCE = new ImmutableBagDeserializer.Byte();

        public Byte() {
            super(ImmutableByteBag.class);
        }

        @Override
        protected MutableByteBag createIntermediate() {
            return ByteBags.mutable.empty();
        }

        @Override
        protected MutableByteBag createIntermediate(int expectedSize) {
            // TODO check whether ByteHashBag really accepts expected size (the parameter is called
            //  "size") or it is still "capacity" a-la in java.util.HashMap, and therefore the
            //  expectedSize should be translated to "capacity" w. r. t. the default load factor.
            return new ByteHashBag(expectedSize);
        }

        @Override
        protected ImmutableByteBag finish(MutableByteBag objects) {
            return objects.toImmutable();
        }
    }

    public static final class Short extends
            BaseCollectionDeserializers.Short<ImmutableShortBag, MutableShortBag> {
        public static final ImmutableBagDeserializer.Short INSTANCE = new ImmutableBagDeserializer.Short();

        public Short() {
            super(ImmutableShortBag.class);
        }

        @Override
        protected MutableShortBag createIntermediate() {
            return ShortBags.mutable.empty();
        }

        @Override
        protected ImmutableShortBag finish(MutableShortBag objects) {
            return objects.toImmutable();
        }
    }

    public static final class Char extends
            BaseCollectionDeserializers.Char<ImmutableCharBag, MutableCharBag> {
        public static final ImmutableBagDeserializer.Char INSTANCE = new ImmutableBagDeserializer.Char();

        public Char() {
            super(ImmutableCharBag.class);
        }

        @Override
        protected MutableCharBag createIntermediate() {
            return CharBags.mutable.empty();
        }

        @Override
        protected ImmutableCharBag finish(MutableCharBag objects) {
            return objects.toImmutable();
        }
    }

    public static final class Int extends
            BaseCollectionDeserializers.Int<ImmutableIntBag, MutableIntBag> {
        public static final ImmutableBagDeserializer.Int INSTANCE = new ImmutableBagDeserializer.Int();

        public Int() {
            super(ImmutableIntBag.class);
        }

        @Override
        protected MutableIntBag createIntermediate() {
            return IntBags.mutable.empty();
        }

        @Override
        protected ImmutableIntBag finish(MutableIntBag objects) {
            return objects.toImmutable();
        }
    }

    public static final class Float extends
            BaseCollectionDeserializers.Float<ImmutableFloatBag, MutableFloatBag> {
        public static final ImmutableBagDeserializer.Float INSTANCE = new ImmutableBagDeserializer.Float();

        public Float() {
            super(ImmutableFloatBag.class);
        }

        @Override
        protected MutableFloatBag createIntermediate() {
            return FloatBags.mutable.empty();
        }

        @Override
        protected ImmutableFloatBag finish(MutableFloatBag objects) {
            return objects.toImmutable();
        }
    }

    public static final class Long extends
            BaseCollectionDeserializers.Long<ImmutableLongBag, MutableLongBag> {
        public static final ImmutableBagDeserializer.Long INSTANCE = new ImmutableBagDeserializer.Long();

        public Long() {
            super(ImmutableLongBag.class);
        }

        @Override
        protected MutableLongBag createIntermediate() {
            return LongBags.mutable.empty();
        }

        @Override
        protected ImmutableLongBag finish(MutableLongBag objects) {
            return objects.toImmutable();
        }
    }

    public static final class Double extends
            BaseCollectionDeserializers.Double<ImmutableDoubleBag, MutableDoubleBag> {
        public static final ImmutableBagDeserializer.Double INSTANCE = new ImmutableBagDeserializer.Double();

        public Double() {
            super(ImmutableDoubleBag.class);
        }

        @Override
        protected MutableDoubleBag createIntermediate() {
            return DoubleBags.mutable.empty();
        }

        @Override
        protected ImmutableDoubleBag finish(MutableDoubleBag objects) {
            return objects.toImmutable();
        }
    }
}
