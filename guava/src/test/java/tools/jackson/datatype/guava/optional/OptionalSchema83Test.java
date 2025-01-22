package tools.jackson.datatype.guava.optional;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.google.common.base.Optional;

import com.fasterxml.jackson.annotation.*;

import tools.jackson.databind.*;
import tools.jackson.databind.jsonFormatVisitors.*;
import tools.jackson.datatype.guava.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

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
        SerializationContext serializationContext;
        final String baseName;
        final Set<String> traversedProperties;

        public VisitorWrapper(SerializationContext serializerProvider, String baseName, Set<String> traversedProperties) {
            this.serializationContext = serializerProvider;
            this.baseName = baseName;
            this.traversedProperties = traversedProperties;
        }

        VisitorWrapper createSubtraverser(String bn) {
            return new VisitorWrapper(getContext(), bn, traversedProperties);
        }

        public Set<String> getTraversedProperties() {
            return traversedProperties;
        }

        @Override
        public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
            return new JsonObjectFormatVisitor.Base(serializationContext) {
                @Override
                public void property(BeanProperty prop) {
                    anyProperty(prop);
                }

                @Override
                public void optionalProperty(BeanProperty prop) {
                    anyProperty(prop);
                }

                private void anyProperty(BeanProperty prop) {
                    final String propertyName = prop.getFullName().toString();
                    traversedProperties.add(baseName + propertyName);
                    serializationContext.findPrimaryPropertySerializer(prop.getType(), prop)
                            .acceptJsonFormatVisitor(createSubtraverser(baseName + propertyName + "."), prop.getType());
                }
            };
        }

        @Override
        public JsonArrayFormatVisitor expectArrayFormat(JavaType type) {
            serializationContext.findValueSerializer(type.getContentType())
                    .acceptJsonFormatVisitor(createSubtraverser(baseName), type.getContentType());
            return new JsonArrayFormatVisitor.Base(serializationContext);
        }

        @Override
        public JsonStringFormatVisitor expectStringFormat(JavaType type) {
            return new JsonStringFormatVisitor.Base();
        }

        @Override
        public JsonNumberFormatVisitor expectNumberFormat(JavaType type) {
            return new JsonNumberFormatVisitor.Base();
        }

        @Override
        public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
            return new JsonIntegerFormatVisitor.Base();
        }

        @Override
        public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) {
            return new JsonBooleanFormatVisitor.Base();
        }

        @Override
        public JsonNullFormatVisitor expectNullFormat(JavaType type) {
            return new JsonNullFormatVisitor.Base();
        }

        @Override
        public JsonAnyFormatVisitor expectAnyFormat(JavaType type) {
            return new JsonAnyFormatVisitor.Base();
        }

        @Override
        public JsonMapFormatVisitor expectMapFormat(JavaType type) {
            return new JsonMapFormatVisitor.Base(serializationContext);
        }

        @Override
        public SerializationContext getContext() {
            return serializationContext;
        }

        @Override
        public void setContext(SerializationContext provider) {
            this.serializationContext = provider;
        }
    }

    @Test
    public void testOptionalTypeSchema83() throws Exception {
        VisitorWrapper wrapper = new VisitorWrapper(null, "", new HashSet<String>());
        mapperWithModule()
                .acceptJsonFormatVisitor(TopLevel.class, wrapper);
        Set<String> properties = wrapper.getTraversedProperties();

        assertTrue(properties.contains("values.data.value"));
    }
}
