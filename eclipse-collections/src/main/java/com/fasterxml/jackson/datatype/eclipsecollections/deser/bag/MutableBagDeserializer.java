package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializers;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.*;
import org.eclipse.collections.impl.bag.mutable.primitive.ByteHashBag;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.primitive.*;

public final class MutableBagDeserializer {
    private MutableBagDeserializer() {
    }

    public static final class Ref extends
            BaseRefCollectionDeserializer<MutableBag<?>, MutableBag<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(MutableBag.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableBag<Object> createIntermediate() {
            return Bags.mutable.empty();
        }

        @Override
        protected MutableBag<?> finish(MutableBag<Object> objects) {
            return objects;
        }

        @Override
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new MutableBagDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializers.Boolean<MutableBooleanBag, MutableBooleanBag> {
        public static final MutableBagDeserializer.Boolean INSTANCE = new MutableBagDeserializer.Boolean();

        public Boolean() {
            super(BooleanBag.class);
        }

        @Override
        protected MutableBooleanBag createIntermediate() {
            return BooleanBags.mutable.empty();
        }

        @Override
        protected MutableBooleanBag finish(MutableBooleanBag objects) {
            return objects;
        }
    }

    public static final class Byte extends
            BaseCollectionDeserializers.Byte<MutableByteBag, MutableByteBag> {
        public static final MutableBagDeserializer.Byte INSTANCE = new MutableBagDeserializer.Byte();

        public Byte() {
            super(ByteBag.class);
        }

        @Override
        protected MutableByteBag createIntermediate() {
            return ByteBags.mutable.empty();
        }

        @Override
        protected MutableByteBag createIntermediate(int expectedSize) {
            // See TODO in ImmutableBagDeserializer.Byte; same here
            return new ByteHashBag(expectedSize);
        }

        @Override
        protected MutableByteBag finish(MutableByteBag objects) {
            return objects;
        }
    }

    public static final class Short extends
            BaseCollectionDeserializers.Short<MutableShortBag, MutableShortBag> {
        public static final MutableBagDeserializer.Short INSTANCE = new MutableBagDeserializer.Short();

        public Short() {
            super(ShortBag.class);
        }

        @Override
        protected MutableShortBag createIntermediate() {
            return ShortBags.mutable.empty();
        }

        @Override
        protected MutableShortBag finish(MutableShortBag objects) {
            return objects;
        }
    }

    public static final class Char extends
            BaseCollectionDeserializers.Char<MutableCharBag, MutableCharBag> {
        public static final MutableBagDeserializer.Char INSTANCE = new MutableBagDeserializer.Char();

        public Char() {
            super(CharBag.class);
        }

        @Override
        protected MutableCharBag createIntermediate() {
            return CharBags.mutable.empty();
        }

        @Override
        protected MutableCharBag finish(MutableCharBag objects) {
            return objects;
        }
    }

    public static final class Int extends
            BaseCollectionDeserializers.Int<MutableIntBag, MutableIntBag> {
        public static final MutableBagDeserializer.Int INSTANCE = new MutableBagDeserializer.Int();

        public Int() {
            super(IntBag.class);
        }

        @Override
        protected MutableIntBag createIntermediate() {
            return IntBags.mutable.empty();
        }

        @Override
        protected MutableIntBag finish(MutableIntBag objects) {
            return objects;
        }
    }

    public static final class Float extends
            BaseCollectionDeserializers.Float<MutableFloatBag, MutableFloatBag> {
        public static final MutableBagDeserializer.Float INSTANCE = new MutableBagDeserializer.Float();

        public Float() {
            super(FloatBag.class);
        }

        @Override
        protected MutableFloatBag createIntermediate() {
            return FloatBags.mutable.empty();
        }

        @Override
        protected MutableFloatBag finish(MutableFloatBag objects) {
            return objects;
        }
    }

    public static final class Long extends
            BaseCollectionDeserializers.Long<MutableLongBag, MutableLongBag> {
        public static final MutableBagDeserializer.Long INSTANCE = new MutableBagDeserializer.Long();

        public Long() {
            super(LongBag.class);
        }

        @Override
        protected MutableLongBag createIntermediate() {
            return LongBags.mutable.empty();
        }

        @Override
        protected MutableLongBag finish(MutableLongBag objects) {
            return objects;
        }
    }

    public static final class Double extends
            BaseCollectionDeserializers.Double<MutableDoubleBag, MutableDoubleBag> {
        public static final MutableBagDeserializer.Double INSTANCE = new MutableBagDeserializer.Double();

        public Double() {
            super(DoubleBag.class);
        }

        @Override
        protected MutableDoubleBag createIntermediate() {
            return DoubleBags.mutable.empty();
        }

        @Override
        protected MutableDoubleBag finish(MutableDoubleBag objects) {
            return objects;
        }
    }
}
