package tools.jackson.datatype.eclipsecollections;

import java.util.*;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.Deserializers;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionLikeType;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapLikeType;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.type.ReferenceType;
import tools.jackson.datatype.eclipsecollections.deser.bag.ImmutableBagDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.bag.ImmutableSortedBagDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.bag.MutableBagDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.bag.MutableSortedBagDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.list.FixedSizeListDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.list.ImmutableListDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.list.MutableListDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.map.EclipseMapDeserializers;
import tools.jackson.datatype.eclipsecollections.deser.set.ImmutableSetDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.set.ImmutableSortedSetDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.set.MutableSetDeserializer;
import tools.jackson.datatype.eclipsecollections.deser.set.MutableSortedSetDeserializer;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.InternalIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.UnsortedBag;
import org.eclipse.collections.api.bag.primitive.BooleanBag;
import org.eclipse.collections.api.bag.primitive.ByteBag;
import org.eclipse.collections.api.bag.primitive.CharBag;
import org.eclipse.collections.api.bag.primitive.DoubleBag;
import org.eclipse.collections.api.bag.primitive.FloatBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.ImmutableByteBag;
import org.eclipse.collections.api.bag.primitive.ImmutableCharBag;
import org.eclipse.collections.api.bag.primitive.ImmutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.ImmutableFloatBag;
import org.eclipse.collections.api.bag.primitive.ImmutableIntBag;
import org.eclipse.collections.api.bag.primitive.ImmutableLongBag;
import org.eclipse.collections.api.bag.primitive.ImmutableShortBag;
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
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bag.sorted.SortedBag;
import org.eclipse.collections.api.collection.FixedSizeCollection;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableByteCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableCharCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableIntCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableShortCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.list.primitive.ShortList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.ordered.ReversibleIterable;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.ImmutableSetIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.MutableSetIterable;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.api.set.primitive.BooleanSet;
import org.eclipse.collections.api.set.primitive.ByteSet;
import org.eclipse.collections.api.set.primitive.CharSet;
import org.eclipse.collections.api.set.primitive.DoubleSet;
import org.eclipse.collections.api.set.primitive.FloatSet;
import org.eclipse.collections.api.set.primitive.ImmutableBooleanSet;
import org.eclipse.collections.api.set.primitive.ImmutableByteSet;
import org.eclipse.collections.api.set.primitive.ImmutableCharSet;
import org.eclipse.collections.api.set.primitive.ImmutableDoubleSet;
import org.eclipse.collections.api.set.primitive.ImmutableFloatSet;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.api.set.primitive.ImmutableLongSet;
import org.eclipse.collections.api.set.primitive.ImmutableShortSet;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.LongSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.set.primitive.MutableByteSet;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.set.primitive.MutableDoubleSet;
import org.eclipse.collections.api.set.primitive.MutableFloatSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.api.set.primitive.MutableShortSet;
import org.eclipse.collections.api.set.primitive.ShortSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;

public final class EclipseCollectionsDeserializers extends Deserializers.Base {
    // for the read-only interfaces (Bag, RichIterable etc) we tend to use the mutable collections because they
    // are faster to construct.

    // initialized below
    private static final Map<Class<? extends PrimitiveIterable>, ValueDeserializer<?>> PRIMITIVE_DESERIALIZERS
            = new HashMap<>();
    @SuppressWarnings("rawtypes")
    private static final Set<Class<? extends InternalIterable>> REFERENCE_TYPES = new HashSet<>();

    @Override
    public ValueDeserializer<?> findCollectionDeserializer(
            CollectionType type,
            DeserializationConfig config,
            BeanDescription.Supplier beanDescRef,
            TypeDeserializer elementTypeDeserializer,
            ValueDeserializer<?> elementDeserializer
    ) {
        if (REFERENCE_TYPES.contains(type.getRawClass())) {
            return findReferenceDeserializer(type, type.getContentType(),
                                             elementTypeDeserializer, elementDeserializer);
        }
        return null;
    }

    @Override
    public ValueDeserializer<?> findCollectionLikeDeserializer(
            CollectionLikeType type,
            DeserializationConfig config,
            BeanDescription.Supplier beanDescRef,
            TypeDeserializer elementTypeDeserializer,
            ValueDeserializer<?> elementDeserializer
    ) {
        if (REFERENCE_TYPES.contains(type.getRawClass())) {
            return findReferenceDeserializer(type, type.getContentType(),
                                             elementTypeDeserializer, elementDeserializer);
        }
        return null;
    }

    @Override
    public ValueDeserializer<?> findMapDeserializer(
            MapType type,
            DeserializationConfig config,
            BeanDescription.Supplier beanDescRef,
            KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer,
            ValueDeserializer<?> elementDeserializer
    ) {
        return findBeanDeserializer(type, config, beanDescRef);
    }

    @Override
    public ValueDeserializer<?> findMapLikeDeserializer(
            MapLikeType type,
            DeserializationConfig config,
            BeanDescription.Supplier beanDescRef,
            KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer,
            ValueDeserializer<?> elementDeserializer
    ) {
        return findBeanDeserializer(type, config, beanDescRef);
    }

    @Override
    public ValueDeserializer<?> findReferenceDeserializer(
            ReferenceType refType,
            DeserializationConfig config,
            BeanDescription.Supplier beanDescRef,
            TypeDeserializer contentTypeDeserializer,
            ValueDeserializer<?> contentDeserializer) {
        return findAnyEclipseDeserializer(refType, contentTypeDeserializer, contentDeserializer);
    }

    @Override
    public ValueDeserializer<?> findBeanDeserializer(
            JavaType type, DeserializationConfig config, BeanDescription.Supplier beanDescRef
    ) {
        return findAnyEclipseDeserializer(type, null, null);
    }

    /**
     * @param elementTypeDeserializer may be null
     * @param elementDeserializer may be null
     */
    private ValueDeserializer<?> findAnyEclipseDeserializer(JavaType type,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) {
        ValueDeserializer<?> deserializer = PRIMITIVE_DESERIALIZERS.get(type.getRawClass());
        if (deserializer != null) {
            return deserializer;
        }

        if (REFERENCE_TYPES.contains(type.getRawClass())) {
            return findReferenceDeserializer(type, type.containedTypeOrUnknown(0), elementTypeDeserializer, elementDeserializer);
        }

        return EclipseMapDeserializers.createDeserializer(type); // May return null
    }

    private ValueDeserializer<?> findReferenceDeserializer(
            JavaType containerType,
            JavaType elementType,
            TypeDeserializer elementTypeDeserializer,
            ValueDeserializer<?> elementDeserializer
    ) {
        Class<?> rawClass = containerType.getRawClass();

        // bags
        if (rawClass == MutableBag.class || rawClass == Bag.class || rawClass == UnsortedBag.class) {
            return new MutableBagDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }
        if (rawClass == ImmutableBag.class) {
            return new ImmutableBagDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }
        if (rawClass == MutableSortedBag.class || rawClass == SortedBag.class) {
            return new MutableSortedBagDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }
        if (rawClass == ImmutableSortedBag.class) {
            return new ImmutableSortedBagDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }

        // collections, lists, some iterables
        if (rawClass == MutableList.class || rawClass == MutableCollection.class ||
            rawClass == OrderedIterable.class || rawClass == ReversibleIterable.class ||
            rawClass == RichIterable.class || rawClass == InternalIterable.class) {
            return new MutableListDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }
        if (rawClass == ImmutableList.class || rawClass == ImmutableCollection.class) {
            return new ImmutableListDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }
        if (rawClass == FixedSizeList.class || rawClass == FixedSizeCollection.class) {
            return new FixedSizeListDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }

        // sets
        if (rawClass == MutableSet.class || rawClass == MutableSetIterable.class ||
            rawClass == SetIterable.class || rawClass == UnsortedSetIterable.class) {
            return new MutableSetDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }
        if (rawClass == ImmutableSet.class || rawClass == ImmutableSetIterable.class) {
            return new ImmutableSetDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }
        if (rawClass == MutableSortedSet.class || rawClass == SortedSetIterable.class) {
            return new MutableSortedSetDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }
        if (rawClass == ImmutableSortedSet.class) {
            return new ImmutableSortedSetDeserializer.Ref(elementType, elementTypeDeserializer, elementDeserializer);
        }

        throw new AssertionError(
                "Type " + rawClass + " in REFERENCE_TYPES but no deserializer found, should not happen");
    }

    static {
        REFERENCE_TYPES.add(MutableBag.class);
        REFERENCE_TYPES.add(Bag.class);
        REFERENCE_TYPES.add(UnsortedBag.class);
        REFERENCE_TYPES.add(ImmutableBag.class);
        REFERENCE_TYPES.add(MutableSortedBag.class);
        REFERENCE_TYPES.add(SortedBag.class);
        REFERENCE_TYPES.add(ImmutableSortedBag.class);
        REFERENCE_TYPES.add(MutableList.class);
        REFERENCE_TYPES.add(MutableCollection.class);
        REFERENCE_TYPES.add(OrderedIterable.class);
        REFERENCE_TYPES.add(ReversibleIterable.class);
        REFERENCE_TYPES.add(RichIterable.class);
        REFERENCE_TYPES.add(InternalIterable.class);
        REFERENCE_TYPES.add(ImmutableList.class);
        REFERENCE_TYPES.add(ImmutableCollection.class);
        REFERENCE_TYPES.add(FixedSizeCollection.class);
        REFERENCE_TYPES.add(FixedSizeList.class);
        REFERENCE_TYPES.add(MutableSet.class);
        REFERENCE_TYPES.add(MutableSetIterable.class);
        REFERENCE_TYPES.add(SetIterable.class);
        REFERENCE_TYPES.add(UnsortedSetIterable.class);
        REFERENCE_TYPES.add(ImmutableSet.class);
        REFERENCE_TYPES.add(ImmutableSetIterable.class);
        REFERENCE_TYPES.add(MutableSortedSet.class);
        REFERENCE_TYPES.add(SortedSetIterable.class);
        REFERENCE_TYPES.add(ImmutableSortedSet.class);

        PRIMITIVE_DESERIALIZERS.put(BooleanBag.class, MutableBagDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableBooleanBag.class, MutableBagDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableBooleanBag.class, ImmutableBagDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(BooleanIterable.class, MutableListDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(BooleanList.class, MutableListDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableBooleanList.class, MutableListDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableBooleanCollection.class, MutableListDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableBooleanList.class, ImmutableListDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableBooleanCollection.class, ImmutableListDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(BooleanSet.class, MutableSetDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableBooleanSet.class, MutableSetDeserializer.Boolean.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableBooleanSet.class, ImmutableSetDeserializer.Boolean.INSTANCE);

        PRIMITIVE_DESERIALIZERS.put(ByteBag.class, MutableBagDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableByteBag.class, MutableBagDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableByteBag.class, ImmutableBagDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ByteIterable.class, MutableListDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ByteList.class, MutableListDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableByteList.class, MutableListDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableByteCollection.class, MutableListDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableByteList.class, ImmutableListDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableByteCollection.class, ImmutableListDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ByteSet.class, MutableSetDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableByteSet.class, MutableSetDeserializer.Byte.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableByteSet.class, ImmutableSetDeserializer.Byte.INSTANCE);

        PRIMITIVE_DESERIALIZERS.put(ShortBag.class, MutableBagDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableShortBag.class, MutableBagDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableShortBag.class, ImmutableBagDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ShortIterable.class, MutableListDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ShortList.class, MutableListDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableShortList.class, MutableListDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableShortCollection.class, MutableListDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableShortList.class, ImmutableListDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableShortCollection.class, ImmutableListDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ShortSet.class, MutableSetDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableShortSet.class, MutableSetDeserializer.Short.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableShortSet.class, ImmutableSetDeserializer.Short.INSTANCE);

        PRIMITIVE_DESERIALIZERS.put(CharBag.class, MutableBagDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableCharBag.class, MutableBagDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableCharBag.class, ImmutableBagDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(CharIterable.class, MutableListDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(CharList.class, MutableListDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableCharList.class, MutableListDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableCharCollection.class, MutableListDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableCharList.class, ImmutableListDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableCharCollection.class, ImmutableListDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(CharSet.class, MutableSetDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableCharSet.class, MutableSetDeserializer.Char.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableCharSet.class, ImmutableSetDeserializer.Char.INSTANCE);

        PRIMITIVE_DESERIALIZERS.put(IntBag.class, MutableBagDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableIntBag.class, MutableBagDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableIntBag.class, ImmutableBagDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(IntIterable.class, MutableListDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(IntList.class, MutableListDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableIntList.class, MutableListDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableIntCollection.class, MutableListDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableIntList.class, ImmutableListDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableIntCollection.class, ImmutableListDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(IntSet.class, MutableSetDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableIntSet.class, MutableSetDeserializer.Int.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableIntSet.class, ImmutableSetDeserializer.Int.INSTANCE);

        PRIMITIVE_DESERIALIZERS.put(FloatBag.class, MutableBagDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableFloatBag.class, MutableBagDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableFloatBag.class, ImmutableBagDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(FloatIterable.class, MutableListDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(FloatList.class, MutableListDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableFloatList.class, MutableListDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableFloatCollection.class, MutableListDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableFloatList.class, ImmutableListDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableFloatCollection.class, ImmutableListDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(FloatSet.class, MutableSetDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableFloatSet.class, MutableSetDeserializer.Float.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableFloatSet.class, ImmutableSetDeserializer.Float.INSTANCE);

        PRIMITIVE_DESERIALIZERS.put(LongBag.class, MutableBagDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableLongBag.class, MutableBagDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableLongBag.class, ImmutableBagDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(LongIterable.class, MutableListDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(LongList.class, MutableListDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableLongList.class, MutableListDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableLongCollection.class, MutableListDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableLongList.class, ImmutableListDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableLongCollection.class, ImmutableListDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(LongSet.class, MutableSetDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableLongSet.class, MutableSetDeserializer.Long.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableLongSet.class, ImmutableSetDeserializer.Long.INSTANCE);

        PRIMITIVE_DESERIALIZERS.put(DoubleBag.class, MutableBagDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableDoubleBag.class, MutableBagDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableDoubleBag.class, ImmutableBagDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(DoubleIterable.class, MutableListDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(DoubleList.class, MutableListDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableDoubleList.class, MutableListDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableDoubleCollection.class, MutableListDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableDoubleList.class, ImmutableListDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableDoubleCollection.class, ImmutableListDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(DoubleSet.class, MutableSetDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(MutableDoubleSet.class, MutableSetDeserializer.Double.INSTANCE);
        PRIMITIVE_DESERIALIZERS.put(ImmutableDoubleSet.class, ImmutableSetDeserializer.Double.INSTANCE);
    }

    @Override
    public boolean hasDeserializerFor(DeserializationConfig config,
            Class<?> valueType)
    {
        // 08-Nov-2019, tatu: Is this sufficient?
        return REFERENCE_TYPES.contains(valueType);
    }
}
