package com.fasterxml.jackson.datatype.guava;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.pojo.AddOp;
import com.fasterxml.jackson.datatype.guava.pojo.MathOp;
import com.fasterxml.jackson.datatype.guava.pojo.MulOp;
import com.google.common.base.Optional;
import com.google.common.collect.*;

import static com.google.common.collect.TreeMultimap.create;

/**
 * Unit tests to verify handling of various {@link Multimap}s.
 *
 * @author steven@nesscomputing.com
 */
public class XXXTestMultimaps extends ModuleTestBase
{
    // Test for issue #13 on github, provided by stevenschlansker
    public static enum MyEnum {
        YAY,
        BOO
    }

    // [Issue#41]
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class MultiMapWrapper {
        @JsonProperty
        Multimap<String, String> map = ArrayListMultimap.create();
    }

    static class MultiMapWithIgnores {
        @JsonIgnoreProperties({ "x", "y" })
        public Multimap<String, String> map = ArrayListMultimap.create();
        
        public MultiMapWithIgnores()
        {
            map.put("a", "foo");
            map.put("x", "bar");
        }
    }

    public static class ImmutableMultimapWrapper {

        private ImmutableMultimap<String, MathOp> multimap;

        public ImmutableMultimapWrapper() {
        }

        public ImmutableMultimapWrapper(ImmutableMultimap<String, MathOp> f) {
            this.multimap = f;
        }

        public ImmutableMultimap<String, MathOp> getMultimap() {
            return multimap;
        }

        public void setMultimap(ImmutableMultimap<String, MathOp> multimap) {
            this.multimap = multimap;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.multimap != null ? this.multimap.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ImmutableMultimapWrapper other = (ImmutableMultimapWrapper) obj;
            return !(this.multimap != other.multimap && (this.multimap == null || !this.multimap.equals(other.multimap)));
        }

    }
    
    private static final String STRING_STRING_MULTIMAP =
            "{\"first\":[\"abc\",\"abc\",\"foo\"]," + "\"second\":[\"bar\"]}";

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testHashMultimap() throws IOException {
        SetMultimap<String, String> map =
                _verifyMultiMapRead(new TypeReference<HashMultimap<String, String>>() {
                });
        assertTrue(map instanceof HashMultimap);
    }

    private SetMultimap<String, String> _verifyMultiMapRead(TypeReference<?> type)
        throws IOException
    {
        SetMultimap<String, String> map = MAPPER.readValue(STRING_STRING_MULTIMAP, type);
        assertEquals(3, map.size());
        assertTrue(map.containsEntry("first", "abc"));
        assertTrue(map.containsEntry("first", "foo"));
        assertTrue(map.containsEntry("second", "bar"));
        return map;
    }
}
