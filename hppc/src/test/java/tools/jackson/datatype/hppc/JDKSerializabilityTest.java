package tools.jackson.datatype.hppc;

import java.io.*;

import tools.jackson.databind.ObjectMapper;

import com.carrotsearch.hppc.ShortArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JDKSerializabilityTest extends ModuleTestBase
{
    public void testMapperWithModule() throws Exception {
        ObjectMapper mapper = mapperWithModule();
    
        // very simple validation: should still work wrt serialization
        ObjectMapper unfrozenMapper = serializeAndDeserialize(mapper);
        ShortArrayList array = new ShortArrayList();
        array.add((short)-12, (short)0);
        assertEquals("[-12,0]", unfrozenMapper.writeValueAsString(array));

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
