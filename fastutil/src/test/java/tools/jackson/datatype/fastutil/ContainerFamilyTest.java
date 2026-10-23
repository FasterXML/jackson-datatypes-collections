package tools.jackson.datatype.fastutil;

import org.junit.jupiter.api.Test;

import tools.jackson.datatype.fastutil.deser.ContainerFamily;

import it.unimi.dsi.fastutil.ints.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for resolution (and caching) of creators by {@link ContainerFamily}.
 */
public class ContainerFamilyTest
{
    private final ContainerFamily<IntCollection> FAMILY = new ContainerFamily<IntCollection>(IntCollection.class)
            .add(IntSet.class, IntOpenHashSet::new, IntLinkedOpenHashSet::new)
            .add(IntList.class, IntArrayList::new, IntArrayList::new)
            .add(IntCollection.class, IntArrayList::new, IntArrayList::new);

    @Test
    void testCreatorsCached()
    {
        for (Class<?> type : new Class<?>[] { IntList.class, IntLinkedOpenHashSet.class,
                AbstractIntSet.class, IntLists.UnmodifiableList.class }) {
            ContainerFamily.Creator<IntCollection> creator = FAMILY.findCreator(type);
            assertNotNull(creator, type.getName());
            assertSame(creator, FAMILY.findCreator(type), type.getName());
        }
    }

    @Test
    void testUnsupportedTypes()
    {
        // non-public view type: no creator (and same result on repeated calls)
        Class<?> subList = IntArrayList.of(1, 2).subList(0, 1).getClass();
        assertNull(FAMILY.findCreator(subList));
        assertNull(FAMILY.findCreator(subList));
        // not a member of the family at all
        assertNull(FAMILY.findCreator(String.class));
    }
}
