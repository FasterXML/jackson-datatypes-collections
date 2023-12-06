package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.ImmutableByteBag;
import org.eclipse.collections.api.bag.primitive.ImmutableCharBag;
import org.eclipse.collections.api.bag.primitive.ImmutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.ImmutableFloatBag;
import org.eclipse.collections.api.bag.primitive.ImmutableIntBag;
import org.eclipse.collections.api.bag.primitive.ImmutableLongBag;
import org.eclipse.collections.api.bag.primitive.ImmutableShortBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.primitive.BooleanBags;
import org.eclipse.collections.impl.factory.primitive.ByteBags;
import org.eclipse.collections.impl.factory.primitive.CharBags;
import org.eclipse.collections.impl.factory.primitive.DoubleBags;
import org.eclipse.collections.impl.factory.primitive.FloatBags;
import org.eclipse.collections.impl.factory.primitive.IntBags;
import org.eclipse.collections.impl.factory.primitive.LongBags;
import org.eclipse.collections.impl.factory.primitive.ShortBags;

public final class ImmutableBagDeserializer {
    private ImmutableBagDeserializer() {
    }

    public static final class Ref extends
            BaseCollectionDeserializer.Ref<ImmutableBag<?>, MutableBag<Object>> {
        private static final long serialVersionUID = 1L;

        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
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
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new ImmutableBagDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializer.Boolean<ImmutableBooleanBag, MutableBooleanBag> {
        private static final long serialVersionUID = 1L;

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
            BaseCollectionDeserializer.Byte<ImmutableByteBag, MutableByteBag> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableBagDeserializer.Byte INSTANCE = new ImmutableBagDeserializer.Byte();

        public Byte() {
            super(ImmutableByteBag.class);
        }

        @Override
        protected MutableByteBag createIntermediate() {
            return ByteBags.mutable.empty();
        }

        @Override
        protected ImmutableByteBag finish(MutableByteBag objects) {
            return objects.toImmutable();
        }
    }

    public static final class Short extends
            BaseCollectionDeserializer.Short<ImmutableShortBag, MutableShortBag> {
        private static final long serialVersionUID = 1L;

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
            BaseCollectionDeserializer.Char<ImmutableCharBag, MutableCharBag> {
        private static final long serialVersionUID = 1L;

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
            BaseCollectionDeserializer.Int<ImmutableIntBag, MutableIntBag> {
        private static final long serialVersionUID = 1L;

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
            BaseCollectionDeserializer.Float<ImmutableFloatBag, MutableFloatBag> {
        private static final long serialVersionUID = 1L;

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
            BaseCollectionDeserializer.Long<ImmutableLongBag, MutableLongBag> {
        private static final long serialVersionUID = 1L;

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
            BaseCollectionDeserializer.Double<ImmutableDoubleBag, MutableDoubleBag> {
        private static final long serialVersionUID = 1L;

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
