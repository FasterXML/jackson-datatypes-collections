package com.fasterxml.jackson.datatype.eclipsecollections.deser.set;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.ImmutableBooleanSet;
import org.eclipse.collections.api.set.primitive.ImmutableByteSet;
import org.eclipse.collections.api.set.primitive.ImmutableCharSet;
import org.eclipse.collections.api.set.primitive.ImmutableDoubleSet;
import org.eclipse.collections.api.set.primitive.ImmutableFloatSet;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.api.set.primitive.ImmutableLongSet;
import org.eclipse.collections.api.set.primitive.ImmutableShortSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.set.primitive.MutableByteSet;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.set.primitive.MutableDoubleSet;
import org.eclipse.collections.api.set.primitive.MutableFloatSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.api.set.primitive.MutableShortSet;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.BooleanSets;
import org.eclipse.collections.impl.factory.primitive.ByteSets;
import org.eclipse.collections.impl.factory.primitive.CharSets;
import org.eclipse.collections.impl.factory.primitive.DoubleSets;
import org.eclipse.collections.impl.factory.primitive.FloatSets;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.LongSets;
import org.eclipse.collections.impl.factory.primitive.ShortSets;

public final class ImmutableSetDeserializer {
    private ImmutableSetDeserializer() {
    }

    public static final class Ref extends
            BaseCollectionDeserializer.Ref<ImmutableSet<?>, MutableSet<Object>> {
        private static final long serialVersionUID = 1L;

        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(ImmutableSet.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableSet<Object> createIntermediate() {
            return Sets.mutable.empty();
        }

        @Override
        protected ImmutableSet<?> finish(MutableSet<Object> objects) {
            return objects.toImmutable();
        }

        @Override
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new ImmutableSetDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializer.Boolean<ImmutableBooleanSet, MutableBooleanSet> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableSetDeserializer.Boolean INSTANCE = new ImmutableSetDeserializer.Boolean();

        public Boolean() {
            super(ImmutableBooleanSet.class);
        }

        @Override
        protected MutableBooleanSet createIntermediate() {
            return BooleanSets.mutable.empty();
        }

        @Override
        protected ImmutableBooleanSet finish(MutableBooleanSet objects) {
            return objects.toImmutable();
        }
    }

    public static final class Byte extends
            BaseCollectionDeserializer.Byte<ImmutableByteSet, MutableByteSet> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableSetDeserializer.Byte INSTANCE = new ImmutableSetDeserializer.Byte();

        public Byte() {
            super(ImmutableByteSet.class);
        }

        @Override
        protected MutableByteSet createIntermediate() {
            return ByteSets.mutable.empty();
        }

        @Override
        protected ImmutableByteSet finish(MutableByteSet objects) {
            return objects.toImmutable();
        }
    }

    public static final class Short extends
            BaseCollectionDeserializer.Short<ImmutableShortSet, MutableShortSet> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableSetDeserializer.Short INSTANCE = new ImmutableSetDeserializer.Short();

        public Short() {
            super(ImmutableShortSet.class);
        }

        @Override
        protected MutableShortSet createIntermediate() {
            return ShortSets.mutable.empty();
        }

        @Override
        protected ImmutableShortSet finish(MutableShortSet objects) {
            return objects.toImmutable();
        }
    }

    public static final class Char extends
            BaseCollectionDeserializer.Char<ImmutableCharSet, MutableCharSet> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableSetDeserializer.Char INSTANCE = new ImmutableSetDeserializer.Char();

        public Char() {
            super(ImmutableCharSet.class);
        }

        @Override
        protected MutableCharSet createIntermediate() {
            return CharSets.mutable.empty();
        }

        @Override
        protected ImmutableCharSet finish(MutableCharSet objects) {
            return objects.toImmutable();
        }
    }

    public static final class Int extends
            BaseCollectionDeserializer.Int<ImmutableIntSet, MutableIntSet> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableSetDeserializer.Int INSTANCE = new ImmutableSetDeserializer.Int();

        public Int() {
            super(ImmutableIntSet.class);
        }

        @Override
        protected MutableIntSet createIntermediate() {
            return IntSets.mutable.empty();
        }

        @Override
        protected ImmutableIntSet finish(MutableIntSet objects) {
            return objects.toImmutable();
        }
    }

    public static final class Float extends
            BaseCollectionDeserializer.Float<ImmutableFloatSet, MutableFloatSet> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableSetDeserializer.Float INSTANCE = new ImmutableSetDeserializer.Float();

        public Float() {
            super(ImmutableFloatSet.class);
        }

        @Override
        protected MutableFloatSet createIntermediate() {
            return FloatSets.mutable.empty();
        }

        @Override
        protected ImmutableFloatSet finish(MutableFloatSet objects) {
            return objects.toImmutable();
        }
    }

    public static final class Long extends
            BaseCollectionDeserializer.Long<ImmutableLongSet, MutableLongSet> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableSetDeserializer.Long INSTANCE = new ImmutableSetDeserializer.Long();

        public Long() {
            super(ImmutableLongSet.class);
        }

        @Override
        protected MutableLongSet createIntermediate() {
            return LongSets.mutable.empty();
        }

        @Override
        protected ImmutableLongSet finish(MutableLongSet objects) {
            return objects.toImmutable();
        }
    }

    public static final class Double extends
            BaseCollectionDeserializer.Double<ImmutableDoubleSet, MutableDoubleSet> {
        private static final long serialVersionUID = 1L;

        public static final ImmutableSetDeserializer.Double INSTANCE = new ImmutableSetDeserializer.Double();

        public Double() {
            super(ImmutableDoubleSet.class);
        }

        @Override
        protected MutableDoubleSet createIntermediate() {
            return DoubleSets.mutable.empty();
        }

        @Override
        protected ImmutableDoubleSet finish(MutableDoubleSet objects) {
            return objects.toImmutable();
        }
    }
}
