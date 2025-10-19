package com.fasterxml.jackson.datatype.guava.deser.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * @since 2.10
 */
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

    private final static Pattern PATTERN_DOUBLE_DOT = Pattern.compile("\\.\\.");

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

    // @since 2.21
    public static Range<? extends Comparable> getRangeFromString(String rangeInterval, DeserializationContext context, KeyDeserializer fromStringDeserializer, JavaType rangeType, Class<?> targetClass) throws IOException {
        if (_isValidBracketNotation(rangeInterval)) {
            BoundType lowerBoundType = rangeInterval.startsWith("[") ? BoundType.CLOSED : BoundType.OPEN;
            BoundType upperBoundType = rangeInterval.endsWith("]") ? BoundType.CLOSED : BoundType.OPEN;

            rangeInterval = rangeInterval.substring(1, rangeInterval.length() - 1);
            String[] parts = PATTERN_DOUBLE_DOT.split(rangeInterval);

            if (parts.length == 2) {
                boolean isLowerInfinite = parts[0].equals("-∞");
                boolean isUpperInfinite = parts[1].equals("+∞");

                if (isLowerInfinite && isUpperInfinite) {
                    return RangeFactory.all();
                } else if (isLowerInfinite) {
                    return RangeFactory.upTo(deserializeStringified(context, parts[1], fromStringDeserializer, rangeType), upperBoundType);
                } else if (isUpperInfinite) {
                    return RangeFactory.downTo(deserializeStringified(context, parts[0], fromStringDeserializer, rangeType), lowerBoundType);
                } else {
                    return RangeFactory.range(deserializeStringified(context, parts[0], fromStringDeserializer, rangeType),
                            lowerBoundType,
                            deserializeStringified(context, parts[1], fromStringDeserializer, rangeType),
                            upperBoundType);
                }
            }
        } else {
            String msg = "Invalid Range: should start with '[' or '(', end with ')' or ']'";
            return (Range<?>) context.handleWeirdStringValue(targetClass, rangeInterval, msg);
        }

        // Give generic failure if no specific reason can be given.
        // Although most likely will happen because `..` is absent, since we are validating brackets above.
        return (Range<?>) context.handleWeirdStringValue(targetClass, rangeInterval,
                "Invalid bracket-notation representation (possibly missing \"..\" delimiter in your Stringified Range)");
    }

    // @since 2.21
    private static Comparable<?> deserializeStringified(DeserializationContext context, String value, KeyDeserializer fromStringDeserializer, JavaType rangeType) throws IOException {
        Object obj = fromStringDeserializer.deserializeKey(value, context);
        if (!(obj instanceof Comparable)) {
            // 02-Jan-2024, tatu: Not sure this is possible but let's double-check
            context.reportBadDefinition(rangeType,
                    String.format(
                            "Stringified endpoint '%s' deserialized to a %s, which does not implement `Comparable`",
                            value,
                            ClassUtil.classNameOf(obj)));
        }
        return (Comparable<?>) obj;
    }


    private static boolean _isValidBracketNotation(String range) {
        if (range.isEmpty()) {
            return false;
        }
        char first = range.charAt(0);
        char last = range.charAt(range.length() - 1);

        return (first == '[' || first == '(') && (last == ']' || last == ')');
    }
}
