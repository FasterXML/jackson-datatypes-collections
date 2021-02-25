package com.fasterxml.jackson.datatype.eclipsecollections.deser.set;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ValueDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializers;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.*;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.*;

public final class MutableSetDeserializer {
    private MutableSetDeserializer() {
    }

    public static final class Ref extends
            BaseRefCollectionDeserializer<MutableSet<?>, MutableSet<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(MutableSet.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableSet<Object> createIntermediate() {
            return Sets.mutable.empty();
        }

        @Override
        protected MutableSet<?> finish(MutableSet<Object> objects) {
            return objects;
        }

        @Override
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new MutableSetDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializers.Boolean<MutableBooleanSet, MutableBooleanSet> {
        public static final MutableSetDeserializer.Boolean INSTANCE = new MutableSetDeserializer.Boolean();

        public Boolean() {
            super(BooleanSet.class);
        }

        @Override
        protected MutableBooleanSet createIntermediate() {
            return BooleanSets.mutable.empty();
        }

        @Override
        protected MutableBooleanSet finish(MutableBooleanSet objects) {
            return objects;
        }
    }

    public static final class Byte extends
            BaseCollectionDeserializers.Byte<MutableByteSet, MutableByteSet> {
        public static final MutableSetDeserializer.Byte INSTANCE = new MutableSetDeserializer.Byte();

        public Byte() {
            super(ByteSet.class);
        }

        @Override
        protected MutableByteSet createIntermediate() {
            return ByteSets.mutable.empty();
        }

        @Override
        protected MutableByteSet finish(MutableByteSet objects) {
            return objects;
        }
    }

    public static final class Short extends
            BaseCollectionDeserializers.Short<MutableShortSet, MutableShortSet> {
        public static final MutableSetDeserializer.Short INSTANCE = new MutableSetDeserializer.Short();

        public Short() {
            super(ShortSet.class);
        }

        @Override
        protected MutableShortSet createIntermediate() {
            return ShortSets.mutable.empty();
        }

        @Override
        protected MutableShortSet finish(MutableShortSet objects) {
            return objects;
        }
    }

    public static final class Char extends
            BaseCollectionDeserializers.Char<MutableCharSet, MutableCharSet> {
        public static final MutableSetDeserializer.Char INSTANCE = new MutableSetDeserializer.Char();

        public Char() {
            super(CharSet.class);
        }

        @Override
        protected MutableCharSet createIntermediate() {
            return CharSets.mutable.empty();
        }

        @Override
        protected MutableCharSet finish(MutableCharSet objects) {
            return objects;
        }
    }

    public static final class Int extends
            BaseCollectionDeserializers.Int<MutableIntSet, MutableIntSet> {
        public static final MutableSetDeserializer.Int INSTANCE = new MutableSetDeserializer.Int();

        public Int() {
            super(IntSet.class);
        }

        @Override
        protected MutableIntSet createIntermediate() {
            return IntSets.mutable.empty();
        }

        @Override
        protected MutableIntSet finish(MutableIntSet objects) {
            return objects;
        }
    }

    public static final class Float extends
            BaseCollectionDeserializers.Float<MutableFloatSet, MutableFloatSet> {
        public static final MutableSetDeserializer.Float INSTANCE = new MutableSetDeserializer.Float();

        public Float() {
            super(FloatSet.class);
        }

        @Override
        protected MutableFloatSet createIntermediate() {
            return FloatSets.mutable.empty();
        }

        @Override
        protected MutableFloatSet finish(MutableFloatSet objects) {
            return objects;
        }
    }

    public static final class Long extends
            BaseCollectionDeserializers.Long<MutableLongSet, MutableLongSet> {
        public static final MutableSetDeserializer.Long INSTANCE = new MutableSetDeserializer.Long();

        public Long() {
            super(LongSet.class);
        }

        @Override
        protected MutableLongSet createIntermediate() {
            return LongSets.mutable.empty();
        }

        @Override
        protected MutableLongSet finish(MutableLongSet objects) {
            return objects;
        }
    }

    public static final class Double extends
            BaseCollectionDeserializers.Double<MutableDoubleSet, MutableDoubleSet> {
        public static final MutableSetDeserializer.Double INSTANCE = new MutableSetDeserializer.Double();

        public Double() {
            super(DoubleSet.class);
        }

        @Override
        protected MutableDoubleSet createIntermediate() {
            return DoubleSets.mutable.empty();
        }

        @Override
        protected MutableDoubleSet finish(MutableDoubleSet objects) {
            return objects;
        }
    }
}
