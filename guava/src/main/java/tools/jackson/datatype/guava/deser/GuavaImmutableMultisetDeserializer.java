package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableMultiset;

/**
 * @since 3.3
 */
abstract class GuavaImmutableMultisetDeserializer<T extends ImmutableMultiset<Object>>
    extends GuavaImmutableCollectionDeserializer<T>
{
    /**
     * Whether entries are expected as {@code {"element":...,"count":...}}
     * (if {@code true}) or as repeated elements (if {@code false}).
     */
    protected final boolean _asEntries;

    GuavaImmutableMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, boolean asEntries) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
        _asEntries = asEntries;
    }

    @Override
    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (!_asEntries) {
            return super._deserializeContents(p, ctxt);
        }
        ImmutableMultiset.Builder<Object> builder = (ImmutableMultiset.Builder<Object>) createBuilder();
        _deserializeMultisetEntries(p, ctxt, builder::addCopies);
        return _build(ctxt, builder);
    }

    @Override
    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (!_asEntries) {
            return super._deserializeFromSingleValue(p, ctxt);
        }
        ImmutableMultiset.Builder<Object> builder = (ImmutableMultiset.Builder<Object>) createBuilder();
        _deserializeMultisetEntry(p, ctxt, builder::addCopies);
        return _build(ctxt, builder);
    }

    @SuppressWarnings("unchecked")
    private T _build(DeserializationContext ctxt, ImmutableMultiset.Builder<Object> builder)
        throws JacksonException
    {
        try {
            return (T) builder.build();
        } catch (ClassCastException | IllegalArgumentException e) {
            // sorted builder fails this way if elements are not mutually comparable
            return _reportMultisetFailure(ctxt, e);
        }
    }
}
