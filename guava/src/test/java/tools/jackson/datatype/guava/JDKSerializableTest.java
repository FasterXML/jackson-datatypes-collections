package tools.jackson.datatype.guava;

import java.io.*;

import org.junit.jupiter.api.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class JDKSerializableTest extends ModuleTestBase
{
    @Test
    public void testSerializable() throws Exception {
        ObjectMapper mapper = mapperWithModule();

        //validate we can still use it to deserialize jackson objects
        ObjectMapper deserializedMapper = serializeAndDeserialize(mapper);
        ImmutableSet<String> set = deserializedMapper.readValue("[\"abc\"]",
                new TypeReference<ImmutableSet<String>>() { });
        assertEquals(1, set.size());
        assertTrue(set.contains("abc"));
    }

    @Test
    public void testSerializableConfigureAbsentsAsNull() throws Exception {
        ObjectMapper mapper = mapperWithModule(true);

        //validate we can still use it to deserialize jackson objects
        ObjectMapper deserializedMapper = serializeAndDeserialize(mapper);
        ImmutableSet<String> set = deserializedMapper.readValue("[\"abc\"]",
                new TypeReference<ImmutableSet<String>>() { });
        assertEquals(1, set.size());
        assertTrue(set.contains("abc"));

        Optional<?> value = mapper.readValue("\"simpleString\"", new TypeReference<Optional<String>>() {});
        assertTrue(value.isPresent());
        assertEquals("simpleString", value.get());
    }

    private ObjectMapper serializeAndDeserialize(ObjectMapper mapper) throws Exception {
        //verify serialization
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

        outputStream.writeObject(mapper);
        byte[] serializedBytes = byteArrayOutputStream.toByteArray();

        //verify deserialization
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedBytes);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);

        Object deserializedObject = inputStream.readObject();

        // Actually `JsonMapper` but basic `ObjectMapper` works here
        return (ObjectMapper) deserializedObject;
    }
}
