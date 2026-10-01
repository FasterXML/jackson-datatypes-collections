package tools.jackson.datatype.hppc;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.carrotsearch.hppc.*;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for round-tripping HPPC containers with polymorphic default typing
 * enabled. Note: the module currently only supports deserialization of
 * {@code int} containers, so only those are covered here.
 */
public class DefaultTypingTest extends ModuleTestBase
{
    static class NoCheckSubTypeValidator
        extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    static class IntContainers {
        public IntContainer container;
        public IntIndexedContainer indexed;
        public IntSet set;
        public IntDeque deque;
        public IntArrayList arrayList;
        public IntHashSet hashSet;
        public IntArrayDeque arrayDeque;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IntContainers)) return false;
            IntContainers other = (IntContainers) o;
            return Objects.equals(container, other.container)
                    && Objects.equals(indexed, other.indexed)
                    && Objects.equals(set, other.set)
                    && Objects.equals(deque, other.deque)
                    && Objects.equals(arrayList, other.arrayList)
                    && Objects.equals(hashSet, other.hashSet)
                    && Objects.equals(arrayDeque, other.arrayDeque);
        }

        @Override
        public int hashCode() {
            return Objects.hash(container, indexed, set, deque, arrayList, hashSet, arrayDeque);
        }
    }

    static class ObjectHolder {
        public Object value;
    }

    private ObjectMapper mapperWithTyping(DefaultTyping typing) {
        return JsonMapper.builder()
                .addModule(new HppcModule())
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                .build();
    }

    /*
    /**********************************************************************
    /* Root values
    /**********************************************************************
     */

    @Test
    public void testRootValuesNonFinal() throws Exception {
        _testRootValues(mapperWithTyping(DefaultTyping.NON_FINAL));
    }

    @Test
    public void testRootValuesObjectAndNonConcrete() throws Exception {
        _testRootValues(mapperWithTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE));
    }

    private void _testRootValues(ObjectMapper mapper) throws Exception {
        // concrete types
        _rootRoundTrip(mapper, IntArrayList.from(1, -3, 7), IntArrayList.class);
        _rootRoundTrip(mapper, IntHashSet.from(-1234, 0, 42), IntHashSet.class);
        _rootRoundTrip(mapper, IntArrayDeque.from(0, 13, 99), IntArrayDeque.class);
        _rootRoundTrip(mapper, new IntArrayList(), IntArrayList.class);

        // abstract types
        _rootRoundTrip(mapper, IntArrayList.from(1, 2, 3), IntContainer.class);
        _rootRoundTrip(mapper, IntHashSet.from(4, 5), IntContainer.class);
        _rootRoundTrip(mapper, IntArrayDeque.from(6, 7), IntContainer.class);
        _rootRoundTrip(mapper, IntArrayList.from(1, -3), IntIndexedContainer.class);
        _rootRoundTrip(mapper, IntHashSet.from(-1234, 0), IntSet.class);
        _rootRoundTrip(mapper, IntArrayDeque.from(0, 13), IntDeque.class);
    }

    private <T> void _rootRoundTrip(ObjectMapper mapper, T input, Class<? super T> declaredType)
        throws Exception
    {
        String json = mapper.writerFor(declaredType).writeValueAsString(input);
        Object result = mapper.readValue(json, declaredType);
        assertEquals(input.getClass(), result.getClass(), "JSON: " + json);
        assertEquals(input, result, "JSON: " + json);
    }

    /*
    /**********************************************************************
    /* POJO properties
    /**********************************************************************
     */

    @Test
    public void testPojoPropertiesNonFinal() throws Exception {
        _testPojoProperties(mapperWithTyping(DefaultTyping.NON_FINAL));
    }

    @Test
    public void testPojoPropertiesObjectAndNonConcrete() throws Exception {
        _testPojoProperties(mapperWithTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE));
    }

    private void _testPojoProperties(ObjectMapper mapper) throws Exception {
        IntContainers input = new IntContainers();
        // declared as interface, but with non-default implementations, so that
        // type information is needed to get the original type back
        input.container = IntHashSet.from(1, 2, 3);
        input.indexed = IntArrayList.from(-1, 0, 1);
        input.set = IntHashSet.from(10, 20);
        input.deque = IntArrayDeque.from(5, 6, 7);
        input.arrayList = IntArrayList.from(100, 200);
        input.hashSet = IntHashSet.from(-5, 5);
        input.arrayDeque = IntArrayDeque.from(8, 9);

        String json = mapper.writeValueAsString(input);
        IntContainers result = mapper.readValue(json, IntContainers.class);
        assertEquals(IntHashSet.class, result.container.getClass(), "JSON: " + json);
        assertEquals(input, result, "JSON: " + json);

        // and a variant where the IntContainer is a deque
        input.container = IntArrayDeque.from(3, 2, 1);
        json = mapper.writeValueAsString(input);
        result = mapper.readValue(json, IntContainers.class);
        assertEquals(IntArrayDeque.class, result.container.getClass(), "JSON: " + json);
        assertEquals(input, result, "JSON: " + json);

        // and with null values
        IntContainers empty = new IntContainers();
        json = mapper.writeValueAsString(empty);
        assertEquals(empty, mapper.readValue(json, IntContainers.class));
    }

    @Test
    public void testObjectPropertyNonFinal() throws Exception {
        _testObjectProperty(mapperWithTyping(DefaultTyping.NON_FINAL));
    }

    @Test
    public void testObjectPropertyObjectAndNonConcrete() throws Exception {
        _testObjectProperty(mapperWithTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE));
    }

    private void _testObjectProperty(ObjectMapper mapper) throws Exception {
        for (Object value : new Object[] {
                IntArrayList.from(1, 2, 3),
                IntHashSet.from(4, 5, 6),
                IntArrayDeque.from(7, 8, 9) }) {
            ObjectHolder input = new ObjectHolder();
            input.value = value;
            String json = mapper.writeValueAsString(input);
            ObjectHolder result = mapper.readValue(json, ObjectHolder.class);
            assertEquals(value.getClass(), result.value.getClass(), "JSON: " + json);
            assertEquals(value, result.value, "JSON: " + json);
        }
    }
}
