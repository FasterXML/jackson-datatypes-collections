package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.BooleanBag;
import org.eclipse.collections.api.bag.primitive.ByteBag;
import org.eclipse.collections.api.bag.primitive.CharBag;
import org.eclipse.collections.api.bag.primitive.DoubleBag;
import org.eclipse.collections.api.bag.primitive.FloatBag;
import org.eclipse.collections.api.bag.primitive.IntBag;
import org.eclipse.collections.api.bag.primitive.LongBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
import org.eclipse.collections.api.bag.primitive.ShortBag;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.primitive.BooleanBags;
import org.eclipse.collections.impl.factory.primitive.ByteBags;
import org.eclipse.collections.impl.factory.primitive.CharBags;
import org.eclipse.collections.impl.factory.primitive.DoubleBags;
import org.eclipse.collections.impl.factory.primitive.FloatBags;
import org.eclipse.collections.impl.factory.primitive.IntBags;
import org.eclipse.collections.impl.factory.primitive.LongBags;
import org.eclipse.collections.impl.factory.primitive.ShortBags;

public final class MutableBagDeserializer {
    private MutableBagDeserializer() {
    }

    public static final class Ref extends
            BaseCollectionDeserializer.Ref<MutableBag<?>, MutableBag<Object>> {
        public Ref(CollectionLikeType type, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(type, typeDeserializer, deserializer);
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
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new MutableBagDeserializer.Ref(_containerType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializer.Boolean<MutableBooleanBag, MutableBooleanBag> {
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
            BaseCollectionDeserializer.Byte<MutableByteBag, MutableByteBag> {
        public static final MutableBagDeserializer.Byte INSTANCE = new MutableBagDeserializer.Byte();

        public Byte() {
            super(ByteBag.class);
        }

        @Override
        protected MutableByteBag createIntermediate() {
            return ByteBags.mutable.empty();
        }

        @Override
        protected MutableByteBag finish(MutableByteBag objects) {
            return objects;
        }
    }

    public static final class Short extends
            BaseCollectionDeserializer.Short<MutableShortBag, MutableShortBag> {
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
            BaseCollectionDeserializer.Char<MutableCharBag, MutableCharBag> {
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
            BaseCollectionDeserializer.Int<MutableIntBag, MutableIntBag> {
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
            BaseCollectionDeserializer.Float<MutableFloatBag, MutableFloatBag> {
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
            BaseCollectionDeserializer.Long<MutableLongBag, MutableLongBag> {
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
            BaseCollectionDeserializer.Double<MutableDoubleBag, MutableDoubleBag> {
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
