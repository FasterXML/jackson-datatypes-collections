package com.fasterxml.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.InternetDomainName;

import static org.junit.jupiter.api.Assertions.*;

public class ScalarTypesTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testInternetDomainNameSerialization() throws Exception
    {
        final String INPUT = "google.com";
        InternetDomainName name = InternetDomainName.from(INPUT);
        assertEquals(quote(INPUT), MAPPER.writeValueAsString(name));
    }

    @Test
    public void testInternetDomainNameDeserialization() throws Exception
    {
        final String INPUT = "google.com";
//        InternetDomainName name = MAPPER.readValue(quote(INPUT), InternetDomainName.class);
        InternetDomainName name = new ObjectMapper().readValue(quote(INPUT), InternetDomainName.class);
        assertNotNull(name);
        assertEquals(INPUT, name.toString());
    }
}
