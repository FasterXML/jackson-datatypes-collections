package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaSerializableTest extends ModuleTestBase {

    public void testSerializable() throws IOException, ClassNotFoundException {
        ObjectMapper mapper = mapperWithModule();

        //validate we can still use it to deserialize jackson objects
        ObjectMapper deserializedMapper = serializeAndDeserialize(mapper);
        ImmutableSet<String> set = deserializedMapper.readValue("[\"abc\"]",
                new TypeReference<ImmutableSet<String>>() { });
        assertEquals(1, set.size());
        assertTrue(set.contains("abc"));
    }

    public void testSerializableConfigureAbsentsAsNull() throws IOException, ClassNotFoundException {
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

    private ObjectMapper serializeAndDeserialize(ObjectMapper mapper) throws IOException, ClassNotFoundException {
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
        assertTrue("Deserialized object should be an instance of ObjectMapper",
                ObjectMapper.class == deserializedObject.getClass());

        return (ObjectMapper) deserializedObject;
    }
}
