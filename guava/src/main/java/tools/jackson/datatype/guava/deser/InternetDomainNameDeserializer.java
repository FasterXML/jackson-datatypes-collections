package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.FromStringDeserializer;

import com.google.common.net.InternetDomainName;

public class InternetDomainNameDeserializer extends FromStringDeserializer<InternetDomainName>
{
    public final static InternetDomainNameDeserializer std = new InternetDomainNameDeserializer();
    
    public InternetDomainNameDeserializer() { super(InternetDomainName.class); }

    @Override
    protected InternetDomainName _deserialize(String value, DeserializationContext ctxt)
        throws JacksonException
    {
        return InternetDomainName.from(value);
    }
}
