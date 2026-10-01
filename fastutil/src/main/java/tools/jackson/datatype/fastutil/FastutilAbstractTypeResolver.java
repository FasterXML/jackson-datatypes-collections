package tools.jackson.datatype.fastutil;

import tools.jackson.databind.module.SimpleAbstractTypeResolver;
import tools.jackson.datatype.fastutil.util.OptionalTypes;

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

    // Types missing from `fastutil-core` (a subset of `fastutil` that, for example,
    // has no `Reference` types) are skipped
    public FastutilAbstractTypeResolver() {
        // Collections of references
        _addMappingIfPresent(() -> addMapping(ObjectCollection.class, ObjectArrayList.class));
        _addMappingIfPresent(() -> addMapping(ObjectList.class, ObjectArrayList.class));
        _addMappingIfPresent(() -> addMapping(ObjectBigList.class, ObjectBigArrayBigList.class));
        _addMappingIfPresent(() -> addMapping(ObjectSet.class, ObjectOpenHashSet.class));
        _addMappingIfPresent(() -> addMapping(ObjectSortedSet.class, ObjectRBTreeSet.class));
        _addMappingIfPresent(() -> addMapping(ReferenceCollection.class, ReferenceArrayList.class));
        _addMappingIfPresent(() -> addMapping(ReferenceList.class, ReferenceArrayList.class));
        _addMappingIfPresent(() -> addMapping(ReferenceBigList.class, ReferenceBigArrayBigList.class));
        _addMappingIfPresent(() -> addMapping(ReferenceSet.class, ReferenceOpenHashSet.class));

        // Maps with reference keys and values
        _addMappingIfPresent(() -> addMapping(Object2ObjectMap.class, Object2ObjectOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Object2ObjectSortedMap.class, Object2ObjectRBTreeMap.class));
        _addMappingIfPresent(() -> addMapping(Object2ReferenceMap.class, Object2ReferenceOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Object2ReferenceSortedMap.class, Object2ReferenceRBTreeMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2ObjectMap.class, Reference2ObjectOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2ReferenceMap.class, Reference2ReferenceOpenHashMap.class));

        // Maps with identity-based reference keys and primitive values
        _addMappingIfPresent(() -> addMapping(Reference2BooleanMap.class, Reference2BooleanOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2ByteMap.class, Reference2ByteOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2ShortMap.class, Reference2ShortOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2CharMap.class, Reference2CharOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2IntMap.class, Reference2IntOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2LongMap.class, Reference2LongOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2FloatMap.class, Reference2FloatOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Reference2DoubleMap.class, Reference2DoubleOpenHashMap.class));

        // Maps with primitive keys and identity-based reference values
        _addMappingIfPresent(() -> addMapping(Byte2ReferenceMap.class, Byte2ReferenceOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Byte2ReferenceSortedMap.class, Byte2ReferenceRBTreeMap.class));
        _addMappingIfPresent(() -> addMapping(Short2ReferenceMap.class, Short2ReferenceOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Short2ReferenceSortedMap.class, Short2ReferenceRBTreeMap.class));
        _addMappingIfPresent(() -> addMapping(Char2ReferenceMap.class, Char2ReferenceOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Char2ReferenceSortedMap.class, Char2ReferenceRBTreeMap.class));
        _addMappingIfPresent(() -> addMapping(Int2ReferenceMap.class, Int2ReferenceOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Int2ReferenceSortedMap.class, Int2ReferenceRBTreeMap.class));
        _addMappingIfPresent(() -> addMapping(Long2ReferenceMap.class, Long2ReferenceOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Long2ReferenceSortedMap.class, Long2ReferenceRBTreeMap.class));
        _addMappingIfPresent(() -> addMapping(Float2ReferenceMap.class, Float2ReferenceOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Float2ReferenceSortedMap.class, Float2ReferenceRBTreeMap.class));
        _addMappingIfPresent(() -> addMapping(Double2ReferenceMap.class, Double2ReferenceOpenHashMap.class));
        _addMappingIfPresent(() -> addMapping(Double2ReferenceSortedMap.class, Double2ReferenceRBTreeMap.class));
    }

    private static void _addMappingIfPresent(Runnable addMapping) {
        OptionalTypes.registerIfPresent(addMapping);
    }
}
