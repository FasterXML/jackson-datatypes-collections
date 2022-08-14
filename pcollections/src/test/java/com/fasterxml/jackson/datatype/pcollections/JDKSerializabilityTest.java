package com.fasterxml.jackson.datatype.pcollections;

import static org.junit.Assert.assertEquals;

import java.io.*;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.pcollections.PCollection;

public class JDKSerializabilityTest extends ModuleTestBase
{
    @Test
    public void testMapperWithModule() throws Exception {
        ObjectMapper mapper = mapperWithModule();
    
        // very simple validation: should still work wrt serialization
        ObjectMapper unfrozenMapper = serializeAndDeserialize(mapper);
        PCollection<Integer> list = unfrozenMapper.readValue("[1,2,3]",
                new TypeReference<PCollection<Integer>>() { });
        assertEquals(3, list.size());
    }

    private ObjectMapper serializeAndDeserialize(ObjectMapper mapper) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

        outputStream.writeObject(mapper);
        byte[] serializedBytes = byteArrayOutputStream.toByteArray();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedBytes);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
        Object deserializedObject = inputStream.readObject();
        return (ObjectMapper) deserializedObject;
    }
}
