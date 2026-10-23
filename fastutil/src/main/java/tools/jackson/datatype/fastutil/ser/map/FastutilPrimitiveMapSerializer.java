package tools.jackson.datatype.fastutil.ser.map;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.ser.jdk.MapSerializer;
import tools.jackson.datatype.fastutil.util.ContentSerializers;
import tools.jackson.datatype.primitive_collections_base.ser.map.PrimitiveMapSerializer;

/**
 * Base class for serializers of fastutil maps with primitive keys and primitive values,
 * which write entries without boxing keys or values.
 *<p>
 * Since fastutil maps also implement {@link java.util.Map}, the standard Jackson
 * {@link MapSerializer} is used instead when settings only it supports apply
 * (like {@code @JsonInclude(content = NON_DEFAULT)}, {@code @JsonSerialize(contentUsing = ...)}
 * or {@code @JsonFormat(with = WRITE_SORTED_MAP_ENTRIES)}). Implementations need to
 * support {@link tools.jackson.databind.SerializationFeature#ORDER_MAP_ENTRIES_BY_KEYS}
 * themselves, since it may change for each call.
 */
public abstract class FastutilPrimitiveMapSerializer<T> extends PrimitiveMapSerializer<T>
{
    protected FastutilPrimitiveMapSerializer(Class<T> type) {
        super(type);
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property)
    {
        if (_requiresStandardSerializer(ctxt, property)) {
            return MapSerializer.construct(ctxt.constructType(handledType()),
                    false, null, null, null, null, null, null)
                .createContextual(ctxt, property);
        }
        return this;
    }

    protected boolean _requiresStandardSerializer(SerializationContext ctxt, BeanProperty property)
    {
        JsonInclude.Value incl = findIncludeOverrides(ctxt, property, Map.class);
        if (incl != null) {
            switch (incl.getContentInclusion()) {
            case ALWAYS, USE_DEFAULTS, NON_NULL, NON_ABSENT: // no effect on primitive values
                break;
            default:
                return true;
            }
        }
        if (Boolean.TRUE.equals(findFormatFeature(ctxt, property, Map.class,
                JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES))) {
            return true;
        }
        if (ContentSerializers.hasCustomContentSerialization(ctxt, property)) {
            return true;
        }
        final AnnotatedMember member = (property == null) ? null : property.getMember();
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (member == null || intr == null) {
            return false;
        }
        final SerializationConfig config = ctxt.getConfig();
        return (intr.findKeySerializer(config, member) != null)
                || Boolean.TRUE.equals(intr.findSerializationSortAlphabetically(config, member))
                || (intr.findFilterId(config, member) != null)
                || !intr.findPropertyIgnoralByName(config, member).findIgnoredForSerialization().isEmpty()
                || (intr.findPropertyInclusionByName(config, member).getIncluded() != null);
    }
}
