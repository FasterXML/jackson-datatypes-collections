package tools.jackson.datatype.eclipsecollections.deser.set;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializers;
import tools.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.*;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.*;

public final class ImmutableSetDeserializer {
    private ImmutableSetDeserializer() {
    }

    public static final class Ref extends
            BaseRefCollectionDeserializer<ImmutableSet<?>, MutableSet<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
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
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new ImmutableSetDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializers.Boolean<ImmutableBooleanSet, MutableBooleanSet> {
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
            BaseCollectionDeserializers.Byte<ImmutableByteSet, MutableByteSet> {
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
            BaseCollectionDeserializers.Short<ImmutableShortSet, MutableShortSet> {
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
            BaseCollectionDeserializers.Char<ImmutableCharSet, MutableCharSet> {
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
            BaseCollectionDeserializers.Int<ImmutableIntSet, MutableIntSet> {
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
            BaseCollectionDeserializers.Float<ImmutableFloatSet, MutableFloatSet> {
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
            BaseCollectionDeserializers.Long<ImmutableLongSet, MutableLongSet> {
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
            BaseCollectionDeserializers.Double<ImmutableDoubleSet, MutableDoubleSet> {
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
