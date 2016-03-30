package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.google.common.net.InternetDomainName;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;

public class InternetDomainNameDeserializer extends FromStringDeserializer<InternetDomainName>
{
    private static final long serialVersionUID = 1L;

    public final static InternetDomainNameDeserializer std = new InternetDomainNameDeserializer();
    
    public InternetDomainNameDeserializer() { super(InternetDomainName.class); }

    @Override
    protected InternetDomainName _deserialize(String value, DeserializationContext ctxt)
            throws IOException {
        return InternetDomainName.from(value);
    }
}
