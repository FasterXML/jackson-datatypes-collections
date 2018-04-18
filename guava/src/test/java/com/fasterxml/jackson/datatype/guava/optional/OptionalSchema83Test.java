package com.fasterxml.jackson.datatype.guava.optional;

import java.util.*;

import com.google.common.base.Optional;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.*;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;

public class OptionalSchema83Test
    extends ModuleTestBase
{
    static class TopLevel {
        @JsonProperty("values")
        public Optional<CollectionHolder<ValueHolder>> values;
    }

    static class ValueHolder {
        @JsonProperty("value")
        public String value;
    }

    static class CollectionHolder<T> {
        @JsonProperty("data")
        public Collection<T> data;
    }

    static class VisitorWrapper implements JsonFormatVisitorWrapper {
        private SerializerProvider serializerProvider;
        private final String baseName;
        private final Set<String> traversedProperties;

        public VisitorWrapper(SerializerProvider serializerProvider, String baseName, Set<String> traversedProperties) {
            this.serializerProvider = serializerProvider;
            this.baseName = baseName;
            this.traversedProperties = traversedProperties;
        }

        private VisitorWrapper createSubtraverser(String bn) {
            return new VisitorWrapper(getProvider(), bn, traversedProperties);
        }

        public Set<String> getTraversedProperties() {
            return traversedProperties;
        }

        @Override
        public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
            return new JsonObjectFormatVisitor.Base(serializerProvider) {
                @Override
                public void property(BeanProperty prop) throws JsonMappingException {
                    anyProperty(prop);
                }

                @Override
                public void optionalProperty(BeanProperty prop) throws JsonMappingException {
                    anyProperty(prop);
                }

                private void anyProperty(BeanProperty prop) throws JsonMappingException {
                    final String propertyName = prop.getFullName().toString();
                    traversedProperties.add(baseName + propertyName);
                    serializerProvider.findPrimaryPropertySerializer(prop.getType(), prop)
                            .acceptJsonFormatVisitor(createSubtraverser(baseName + propertyName + "."), prop.getType());
                }
            };
        }

        @Override
        public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
            serializerProvider.findValueSerializer(type.getContentType())
                    .acceptJsonFormatVisitor(createSubtraverser(baseName), type.getContentType());
            return new JsonArrayFormatVisitor.Base(serializerProvider);
        }

        @Override
        public JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException {
            return new JsonStringFormatVisitor.Base();
        }

        @Override
        public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
            return new JsonNumberFormatVisitor.Base();
        }

        @Override
        public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
            return new JsonIntegerFormatVisitor.Base();
        }

        @Override
        public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
            return new JsonBooleanFormatVisitor.Base();
        }

        @Override
        public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
            return new JsonNullFormatVisitor.Base();
        }

        @Override
        public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
            return new JsonAnyFormatVisitor.Base();
        }

        @Override
        public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
            return new JsonMapFormatVisitor.Base(serializerProvider);
        }

        @Override
        public SerializerProvider getProvider() {
            return serializerProvider;
        }

        @Override
        public void setProvider(SerializerProvider provider) {
            this.serializerProvider = provider;
        }
    }

    public void testOptionalTypeSchema83() throws Exception {
        VisitorWrapper wrapper = new VisitorWrapper(null, "", new HashSet<String>());
        mapperWithModule()
                .acceptJsonFormatVisitor(TopLevel.class, wrapper);
        Set<String> properties = wrapper.getTraversedProperties();

        assertTrue(properties.contains("values.data.value"));
    }
}
