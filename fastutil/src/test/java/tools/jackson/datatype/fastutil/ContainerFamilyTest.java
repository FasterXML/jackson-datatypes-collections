package tools.jackson.datatype.fastutil;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.ValueInstantiationException;
import tools.jackson.datatype.fastutil.deser.ContainerFamily;

import it.unimi.dsi.fastutil.ints.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for resolution (and caching) of creators by {@link ContainerFamily}, and
 * reporting of failures to create containers.
 */
public class ContainerFamilyTest extends ModuleTestBase
{
    public static class FailingList extends IntArrayList {
        public FailingList() {
            throw new IllegalStateException("no-args failure");
        }
    }

    // No default constructor: built from an `IntArrayList` using copy constructor
    public static class FailingCopyList extends IntArrayList {
        public FailingCopyList(IntList list) {
            throw new IllegalStateException("copy failure");
        }
    }

    static class FailingBean {
        public FailingCopyList list;
    }

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

    @Test
    void testCreationFailures() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule();

        ValueInstantiationException e = assertThrows(ValueInstantiationException.class,
                () -> mapper.readValue("[1]", FailingList.class));
        assertTrue(e.getMessage().contains("no-args failure"), e.getMessage());
        assertInstanceOf(IllegalStateException.class, e.getCause());
        assertNotNull(e.getLocation());

        e = assertThrows(ValueInstantiationException.class,
                () -> mapper.readValue("{\"list\":[1]}", FailingBean.class));
        assertTrue(e.getMessage().contains("copy failure"), e.getMessage());
        assertEquals("list", e.getPath().get(0).getPropertyName());
    }
}
