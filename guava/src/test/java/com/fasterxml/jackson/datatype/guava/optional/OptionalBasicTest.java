package com.fasterxml.jackson.datatype.guava.optional;

import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;

import com.google.common.base.Optional;

public class OptionalBasicTest extends ModuleTestBase
{
    public static final class OptionalData {
        public Optional<String> myString;
    }

    public static final class OptionalGenericData<T>{
        public Optional<T> myData;
    }

    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
    public static class Unit
    {
//        @JsonIdentityReference(alwaysAsId=true)
        public Optional<Unit> baseUnit;
        
        public Unit() { }
        public Unit(Optional<Unit> u) { baseUnit = u; }
        
        public void link(Unit u) {
            baseUnit = Optional.of(u);
        }
    }

    // To test handling of polymorphic value types
    
    public static class Container {
        public Optional<Contained> contained;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY)
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "ContainedImpl", value = ContainedImpl.class),
    })
    public static interface Contained { }

    public static class ContainedImpl implements Contained { }

    static class CaseChangingStringWrapper {
        @JsonSerialize(contentUsing=UpperCasingSerializer.class)
        @JsonDeserialize(contentUsing=LowerCasingDeserializer.class)
        public Optional<String> value;

        CaseChangingStringWrapper() { }
        public CaseChangingStringWrapper(String s) { value = Optional.of(s); }
    }

    public static class UpperCasingSerializer extends StdScalarSerializer<String>
    {
        public UpperCasingSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator gen,
                SerializerProvider provider) {
            gen.writeString(value.toUpperCase());
        }
    }

    public static class LowerCasingDeserializer extends StdScalarDeserializer<String>
    {
        public LowerCasingDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) {
            return p.getText().toLowerCase();
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();
    
    public void testOptionalTypeResolution() throws Exception
    {
        // With 2.6, we need to recognize it as ReferenceType
        JavaType t = MAPPER.constructType(Optional.class);
        assertNotNull(t);
        assertEquals(Optional.class, t.getRawClass());
        assertTrue(t.isReferenceType());
    }
    
    public void testDeserAbsent() throws Exception {
        Optional<?> value = MAPPER.readValue("null", new TypeReference<Optional<String>>() {});
        assertFalse(value.isPresent());
    }
    
    public void testDeserSimpleString() throws Exception{
        Optional<?> value = MAPPER.readValue("\"simpleString\"", new TypeReference<Optional<String>>() {});
        assertTrue(value.isPresent());
        assertEquals("simpleString", value.get());
    }
    
    public void testDeserInsideObject() throws Exception {
        OptionalData data = MAPPER.readValue("{\"myString\":\"simpleString\"}", OptionalData.class);
        assertTrue(data.myString.isPresent());
        assertEquals("simpleString", data.myString.get());
    }
    
    public void testDeserComplexObject() throws Exception {
        TypeReference<Optional<OptionalData>> type = new TypeReference<Optional<OptionalData>>() {};
        Optional<OptionalData> data = MAPPER.readValue("{\"myString\":\"simpleString\"}", type);
        assertTrue(data.isPresent());
        assertTrue(data.get().myString.isPresent());
        assertEquals("simpleString", data.get().myString.get());
    }

    public void testDeserGeneric() throws Exception {
        TypeReference<Optional<OptionalGenericData<String>>> type = new TypeReference<Optional<OptionalGenericData<String>>>() {};
        Optional<OptionalGenericData<String>> data = MAPPER.readValue("{\"myData\":\"simpleString\"}", type);
        assertTrue(data.isPresent());
        assertTrue(data.get().myData.isPresent());
        assertEquals("simpleString", data.get().myData.get());
    }

    public void testSerAbsent() throws Exception {
        String value = MAPPER.writeValueAsString(Optional.absent());
        assertEquals("null", value);
    }

    public void testSerSimpleString() throws Exception {
        String value = MAPPER.writeValueAsString(Optional.of("simpleString"));
        assertEquals("\"simpleString\"", value);
    }

    public void testSerInsideObject() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = Optional.of("simpleString");
        String value = MAPPER.writeValueAsString(data);
        assertEquals("{\"myString\":\"simpleString\"}", value);
    }

    public void testSerComplexObject() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = Optional.of("simpleString");
        String value = MAPPER.writeValueAsString(Optional.of(data));
        assertEquals("{\"myString\":\"simpleString\"}", value);
    }

    public void testSerPropInclusionAlways() throws Exception {
        OptionalGenericData<String> data = new OptionalGenericData<String>();
        data.myData = Optional.of("simpleString");
        // NOTE: pass 'true' to ensure "legacy" setting
        String value = builderWithModule(true)
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT,
                        JsonInclude.Include.ALWAYS))
                .build()
                .writeValueAsString(data);
        assertEquals("{\"myData\":\"simpleString\"}", value);
    }

    public void testSerPropInclusionNonNull() throws Exception {
        OptionalGenericData<String> data = new OptionalGenericData<String>();
        data.myData = Optional.of("simpleString");
        // NOTE: pass 'true' to ensure "legacy" setting
        String value = builderWithModule(true)
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT,
                        JsonInclude.Include.NON_NULL))
                .build()
                .writeValueAsString(data);
        assertEquals("{\"myData\":\"simpleString\"}", value);
    }

    public void testSerPropInclusionNonAbsent() throws Exception {
        OptionalGenericData<String> data = new OptionalGenericData<String>();
        data.myData = Optional.of("simpleString");
        // NOTE: pass 'true' to ensure "legacy" setting
        String value = builderWithModule(true)
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT,
                        JsonInclude.Include.NON_ABSENT))
                .build()
                .writeValueAsString(data);
        assertEquals("{\"myData\":\"simpleString\"}", value);
    }

    public void testSerPropInclusionNonEmpty() throws Exception {
        OptionalGenericData<String> data = new OptionalGenericData<String>();
        data.myData = Optional.of("simpleString");
        // NOTE: pass 'true' to ensure "legacy" setting
        String value = builderWithModule(true)
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT,
                        JsonInclude.Include.NON_EMPTY))
                .build()
                .writeValueAsString(data);
        assertEquals("{\"myData\":\"simpleString\"}", value);
    }

    public void testSerGeneric() throws Exception {
        OptionalGenericData<String> data = new OptionalGenericData<String>();
        data.myData = Optional.of("simpleString");
        String value = MAPPER.writeValueAsString(Optional.of(data));
        assertEquals("{\"myData\":\"simpleString\"}", value);
    }

    public void testSerNonNull() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = Optional.absent();
        // NOTE: pass 'true' to ensure "legacy" setting
        String value = builderWithModule(true)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build()
                .writeValueAsString(data);
        assertEquals("{}", value);
    }

    public void testSerOptDefault() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = Optional.absent();
        String value = builderWithModule()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS))
                .build()
                .writeValueAsString(data);
        assertEquals("{\"myString\":null}", value);
    }

    public void testSerOptNull() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = builderWithModule()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build()
                .writeValueAsString(data);
        assertEquals("{}", value);
    }

    // for [dataformat-guava#66]
    public void testSerOptDisableAsNull() throws Exception {
        final OptionalData data = new OptionalData();
        data.myString = Optional.absent();

        ObjectMapper mapper = mapperWithModule(false);
        assertEquals("{\"myString\":null}", mapper.writeValueAsString(data));

        // but do exclude with NON_EMPTY
        mapper = builderWithModule(false)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build();
        assertEquals("{}", mapper.writeValueAsString(data));

        // and with NON_ABSENT
        mapper = builderWithModule(false)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertEquals("{}", mapper.writeValueAsString(data));
    }
    
    public void testSerOptNonEmpty() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = builderWithModule()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build()
                .writeValueAsString(data);
        assertEquals("{}", value);
    }

    public void testSerOptNonDefault() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = builderWithModule()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build()
                .writeValueAsString(data);
        assertEquals("{}", value);
    }
    
    public void testWithTypingEnabled() throws Exception
    {
		final ObjectMapper mapper = builderWithModule()
		        .activateDefaultTyping(new NoCheckSubTypeValidator(),
		                DefaultTyping.OBJECT_AND_NON_CONCRETE)
		        .build();
		final OptionalData myData = new OptionalData();
		myData.myString = Optional.fromNullable("abc");

		final String json = mapper.writeValueAsString(myData);
		final OptionalData deserializedMyData = mapper.readValue(json, OptionalData.class);
		assertEquals(myData.myString, deserializedMyData.myString);
    }

    // [datatype-guava#17]
    public void testObjectId() throws Exception
    {
        final Unit input = new Unit();
        input.link(input);
        String json = MAPPER.writeValueAsString(input);
        Unit result = MAPPER.readValue(json, Unit.class);
        assertNotNull(result);
        assertNotNull(result.baseUnit);
        assertTrue(result.baseUnit.isPresent());
        Unit base = result.baseUnit.get();
        assertSame(result, base);
    }

    public void testOptionalCollection() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        TypeReference<List<Optional<String>>> typeReference =
            new TypeReference<List<Optional<String>>>() {};

        List<Optional<String>> list = new ArrayList<Optional<String>>();
        list.add(Optional.of("2014-1-22"));
        list.add(Optional.<String>absent());
        list.add(Optional.of("2014-1-23"));

        String str = mapper.writeValueAsString(list);
        assertEquals("[\"2014-1-22\",null,\"2014-1-23\"]", str);

        List<Optional<String>> result = mapper.readValue(str, typeReference);
        assertEquals(list.size(), result.size());
        for (int i = 0; i < list.size(); ++i) {
            assertEquals("Entry #"+i, list.get(i), result.get(i));
        }
    }

    // [datatype-guava#81]
    public void testPolymorphic() throws Exception
    {
        final Container dto = new Container();
        dto.contained = Optional.of((Contained) new ContainedImpl());
        
        final String json = MAPPER.writeValueAsString(dto);

        final Container fromJson = MAPPER.readValue(json, Container.class);
        assertNotNull(fromJson.contained);
        assertTrue(fromJson.contained.isPresent());
        assertSame(ContainedImpl.class, fromJson.contained.get().getClass());
    }

    public void testWithCustomDeserializer() throws Exception
    {
        CaseChangingStringWrapper w = MAPPER.readValue(aposToQuotes("{'value':'FoobaR'}"),
                CaseChangingStringWrapper.class);
        assertEquals("foobar", w.value.get());
    }

    public void testCustomSerializer() throws Exception
    {
        final String VALUE = "fooBAR";
        String json = MAPPER.writeValueAsString(new CaseChangingStringWrapper(VALUE));
        assertEquals(json, aposToQuotes("{'value':'FOOBAR'}"));
    }
}
