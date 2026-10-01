package tools.jackson.datatype.fastutil;

import tools.jackson.databind.module.SimpleAbstractTypeResolver;

import it.unimi.dsi.fastutil.bytes.*;
import it.unimi.dsi.fastutil.chars.*;
import it.unimi.dsi.fastutil.doubles.*;
import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.shorts.*;

/**
 * Maps abstract fastutil collection and map types that are not handled by
 * {@link FastutilDeserializers} (that is, containers of references, like
 * {@code ObjectList} or {@code Int2ReferenceMap}) to default implementations,
 * to be deserialized by standard Jackson {@code Collection} and {@code Map}
 * deserializers.
 */
public class FastutilAbstractTypeResolver extends SimpleAbstractTypeResolver
{
    private static final long serialVersionUID = 1L;

    public FastutilAbstractTypeResolver() {
        // Collections of references
        addMapping(ObjectCollection.class, ObjectArrayList.class);
        addMapping(ObjectList.class, ObjectArrayList.class);
        addMapping(ObjectBigList.class, ObjectBigArrayBigList.class);
        addMapping(ObjectSet.class, ObjectOpenHashSet.class);
        addMapping(ObjectSortedSet.class, ObjectRBTreeSet.class);
        addMapping(ReferenceCollection.class, ReferenceArrayList.class);
        addMapping(ReferenceList.class, ReferenceArrayList.class);
        addMapping(ReferenceBigList.class, ReferenceBigArrayBigList.class);
        addMapping(ReferenceSet.class, ReferenceOpenHashSet.class);

        // Maps with reference keys and values
        addMapping(Object2ObjectMap.class, Object2ObjectOpenHashMap.class);
        addMapping(Object2ObjectSortedMap.class, Object2ObjectRBTreeMap.class);
        addMapping(Object2ReferenceMap.class, Object2ReferenceOpenHashMap.class);
        addMapping(Object2ReferenceSortedMap.class, Object2ReferenceRBTreeMap.class);
        addMapping(Reference2ObjectMap.class, Reference2ObjectOpenHashMap.class);
        addMapping(Reference2ReferenceMap.class, Reference2ReferenceOpenHashMap.class);

        // Maps with identity-based reference keys and primitive values
        addMapping(Reference2BooleanMap.class, Reference2BooleanOpenHashMap.class);
        addMapping(Reference2ByteMap.class, Reference2ByteOpenHashMap.class);
        addMapping(Reference2ShortMap.class, Reference2ShortOpenHashMap.class);
        addMapping(Reference2CharMap.class, Reference2CharOpenHashMap.class);
        addMapping(Reference2IntMap.class, Reference2IntOpenHashMap.class);
        addMapping(Reference2LongMap.class, Reference2LongOpenHashMap.class);
        addMapping(Reference2FloatMap.class, Reference2FloatOpenHashMap.class);
        addMapping(Reference2DoubleMap.class, Reference2DoubleOpenHashMap.class);

        // Maps with primitive keys and identity-based reference values
        addMapping(Byte2ReferenceMap.class, Byte2ReferenceOpenHashMap.class);
        addMapping(Byte2ReferenceSortedMap.class, Byte2ReferenceRBTreeMap.class);
        addMapping(Short2ReferenceMap.class, Short2ReferenceOpenHashMap.class);
        addMapping(Short2ReferenceSortedMap.class, Short2ReferenceRBTreeMap.class);
        addMapping(Char2ReferenceMap.class, Char2ReferenceOpenHashMap.class);
        addMapping(Char2ReferenceSortedMap.class, Char2ReferenceRBTreeMap.class);
        addMapping(Int2ReferenceMap.class, Int2ReferenceOpenHashMap.class);
        addMapping(Int2ReferenceSortedMap.class, Int2ReferenceRBTreeMap.class);
        addMapping(Long2ReferenceMap.class, Long2ReferenceOpenHashMap.class);
        addMapping(Long2ReferenceSortedMap.class, Long2ReferenceRBTreeMap.class);
        addMapping(Float2ReferenceMap.class, Float2ReferenceOpenHashMap.class);
        addMapping(Float2ReferenceSortedMap.class, Float2ReferenceRBTreeMap.class);
        addMapping(Double2ReferenceMap.class, Double2ReferenceOpenHashMap.class);
        addMapping(Double2ReferenceSortedMap.class, Double2ReferenceRBTreeMap.class);
    }
}
