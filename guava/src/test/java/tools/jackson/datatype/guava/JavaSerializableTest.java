package tools.jackson.datatype.guava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public class JavaSerializableTest extends ModuleTestBase {

    public void testSerializable() throws Exception {
        ObjectMapper mapper = mapperWithModule();

        //validate we can still use it to deserialize jackson objects
        ObjectMapper deserializedMapper = serializeAndDeserialize(mapper);
        ImmutableSet<String> set = deserializedMapper.readValue("[\"abc\"]",
                new TypeReference<ImmutableSet<String>>() { });
        assertEquals(1, set.size());
        assertTrue(set.contains("abc"));
    }

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

        //validate the object
        return (ObjectMapper) deserializedObject;
    }
}
