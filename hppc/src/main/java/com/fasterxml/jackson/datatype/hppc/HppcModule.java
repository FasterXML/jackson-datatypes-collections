package com.fasterxml.jackson.datatype.hppc;

import java.lang.reflect.Type;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.*;

import com.fasterxml.jackson.datatype.hppc.deser.HppcDeserializers;
import com.fasterxml.jackson.datatype.hppc.ser.*;

import com.carrotsearch.hppc.ObjectContainer;

public class HppcModule extends com.fasterxml.jackson.databind.Module
    implements java.io.Serializable
{
    private static final long serialVersionUID = 3L;

    private final String NAME = "HppcDatatypeModule";

    @Override public String getModuleName() { return NAME; }
    @Override public Version version() { return PackageVersion.VERSION; }
    
    @Override
    public void setupModule(SetupContext context)
    {
        // must add a "type modifier", to recognize HPPC collection/map types
        context.addTypeModifier(new HppcTypeModifier());
        context.addDeserializers(new HppcDeserializers());
        context.addSerializers(new HppcSerializers());
    }

    /*
    /**********************************************************************
    /* Helper classes
    /**********************************************************************
     */

    /**
     * Ww need to ensure that parameterized ("generic") containers are
     * recognized as Collection-/Map-like types, so that associated annotations
     * are processed, and key/value types passed as expected.
     */
    static class HppcTypeModifier extends TypeModifier
    {
        @Override
        public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings,
                TypeFactory typeFactory)
        {
            Class<?> raw = type.getRawClass();
            if (ObjectContainer.class.isAssignableFrom(raw)) {
                JavaType[] params = typeFactory.findTypeParameters(type, ObjectContainer.class);
                return typeFactory.constructCollectionLikeType(raw, params[0]);
            }
            return type;
        }
    }
    
}