package tools.jackson.datatype.guava.deser.util;

import java.lang.reflect.Field;

import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.TypeResolutionContext;

public class RangeHelper
{
    public static class RangeProperties implements java.io.Serializable
    {
        private static final long serialVersionUID = 2L;

        public final String lowerEndpoint, upperEndpoint;
        public final String lowerBoundType, upperBoundType;

        protected RangeProperties() {
            this("lowerEndpoint", "upperEndpoint",
                    "lowerBoundType", "upperBoundType");
        }

        public RangeProperties(String lowerEP, String upperEP,
                String lowerBT, String upperBT) {
            lowerEndpoint = lowerEP;
            upperEndpoint = upperEP;
            lowerBoundType = lowerBT;
            upperBoundType = upperBT;
        }

        protected Field[] fields() {
            return new Field[] {
                    _field(lowerEndpoint),
                    _field(upperEndpoint),
                    _field(lowerBoundType),
                    _field(upperBoundType)
            };
        }

        private Field _field(String name) {
            try {
                return getClass().getField(name);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private final static RangeProperties STD_NAMES = new RangeProperties();

    private final static Field[] FIELDS = STD_NAMES.fields();

    public static RangeProperties standardNames() {
        return STD_NAMES;
    }

    public static RangeProperties getPropertyNames(MapperConfig<?> config, PropertyNamingStrategy pns) {
        if (pns == null) {
            return STD_NAMES;
        }
        final TypeResolutionContext typeCtxt = new TypeResolutionContext.Empty(config.getTypeFactory());
        return new RangeProperties(
                _find(config, typeCtxt, pns, FIELDS[0]),
                _find(config, typeCtxt, pns, FIELDS[1]),
                _find(config, typeCtxt, pns, FIELDS[2]),
                _find(config, typeCtxt, pns, FIELDS[3])
        );
    }

    private static String _find(MapperConfig<?> config, TypeResolutionContext typeCtxt,
            PropertyNamingStrategy pns, Field field) {
        AnnotatedField af = new AnnotatedField(typeCtxt, field, null);
        return pns.nameForField(config, af, field.getName());
    }
}
