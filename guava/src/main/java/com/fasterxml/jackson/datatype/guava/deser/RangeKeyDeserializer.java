package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeFactory;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeHelper;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Jackson deserializer for a Guava {@link Range}.
 * <p>
 * TODO: I think it would make sense to reimplement this deserializer to
 * use Delegating Deserializer, using a POJO as an intermediate form (properties
 * could be of type {@link Object})
 * This would also also simplify the implementation a bit.
 */
public class RangeKeyDeserializer
        extends KeyDeserializer
        implements ContextualKeyDeserializer {
    private static final long serialVersionUID = 1L;

    protected final static Pattern PATTERN_DOUBLE_DOT = Pattern.compile("\\.\\.");

    protected final JavaType _rangeType;

    protected final KeyDeserializer _fromStringDeserializer;

    public RangeKeyDeserializer(JavaType type) {
        this(type, null);
    }

    /**
     * @since 2.17
     */
    @SuppressWarnings("unchecked")
    protected RangeKeyDeserializer(JavaType rangeType, KeyDeserializer rangeDeserializer) {
        _rangeType = rangeType;
        _fromStringDeserializer = rangeDeserializer;
    }

    @Override
    public KeyDeserializer createContextual(DeserializationContext ctxt,
                                            BeanProperty property) throws JsonMappingException {
        JavaType endpointType = _rangeType.containedType(0);
        if (endpointType == null) { // should this ever occur?
            endpointType = TypeFactory.unknownType();
        }
        KeyDeserializer kd = _fromStringDeserializer;
            kd = ctxt.findKeyDeserializer(endpointType, property);
        if ((kd != _fromStringDeserializer)) {
            return new RangeKeyDeserializer(_rangeType, kd);
        }
        return this;
    }

    /*
    /**********************************************************
    /* Actual deserialization
    /**********************************************************
     */

    @Override
    public Object deserializeKey(String rangeInterval, DeserializationContext context) throws IOException {
        if (rangeInterval.isEmpty()) {
            return null;
        }

        if (isValidBracketNotation(rangeInterval)) {
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
                    return RangeFactory.upTo(deserializeStringified(context, parts[1]), upperBoundType);
                } else if (isUpperInfinite) {
                    return RangeFactory.downTo(deserializeStringified(context, parts[0]), lowerBoundType);
                } else {
                    return RangeFactory.range(deserializeStringified(context, parts[0]),
                            lowerBoundType,
                            deserializeStringified(context, parts[1]),
                            upperBoundType);
                }
            }
        } else {
            String msg = "Invalid Range: should start with '[' or '(', end with ')' or ']'";
            return (Range<?>) context.handleWeirdStringValue(Range.class, rangeInterval, msg);
        }

        // Give generic failure if no specific reason can be given.
        // Although most likely will happen because `..` is absent, since we are validating brackets above.
        return (Range<?>) context.handleWeirdStringValue(Range.class, rangeInterval,
                "Invalid bracket-notation representation (possibly missing \"..\" delimiter in your Stringified Range)");
    }

    private Comparable<?> deserializeStringified(DeserializationContext context, String value) throws IOException {
        Object obj = _fromStringDeserializer.deserializeKey(value, context);
        if (!(obj instanceof Comparable)) {
            // 02-Jan-2024, tatu: Not sure this is possible but let's double-check
            context.reportBadDefinition(_rangeType,
                    String.format(
                            "Stringified endpoint '%s' deserialized to a %s, which does not implement `Comparable`",
                            value,
                            ClassUtil.classNameOf(obj)));
        }
        return (Comparable<?>) obj;
    }


    private boolean isValidBracketNotation(String range) {
        char first = range.charAt(0);
        char last = range.charAt(range.length() - 1);

        return (first == '[' || first == '(') && (last == ']' || last == ')');
    }
}
