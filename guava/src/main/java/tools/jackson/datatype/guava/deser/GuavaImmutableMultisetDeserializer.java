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

    /**
     * Maximum number of elements (sum of counts) allowed when reading entries.
     */
    protected final int _maxSize;

    GuavaImmutableMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, boolean asEntries, int maxSize) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
        _asEntries = asEntries;
        _maxSize = maxSize;
    }

    @Override
    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (!_asEntries) {
            return super._deserializeContents(p, ctxt);
        }
        ImmutableMultiset.Builder<Object> builder = (ImmutableMultiset.Builder<Object>) createBuilder();
        MultisetEntryReader r = new MultisetEntryReader(this, _maxSize);
        while (r.nextEntry(p, ctxt)) {
            _addEntry(p, ctxt, builder, r.element(), r.count());
        }
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
        MultisetEntryReader r = new MultisetEntryReader(this, _maxSize);
        if (r.readEntry(p, ctxt)) {
            _addEntry(p, ctxt, builder, r.element(), r.count());
        }
        return _build(ctxt, builder);
    }

    private void _addEntry(JsonParser p, DeserializationContext ctxt,
            ImmutableMultiset.Builder<Object> builder, Object value, int count)
        throws JacksonException
    {
        // Avoid lambda allocation for the common non-null case
        if (value == null) {
            // NOTE: parser is at END_OBJECT of the entry, as "count" may follow "element"
            _addElement(p, ctxt, p.currentToken(), null,
                    v -> _addCopies(ctxt, builder, v, count));
        } else {
            _addCopies(ctxt, builder, value, count);
        }
    }

    private ImmutableMultiset.Builder<Object> _addCopies(DeserializationContext ctxt,
            ImmutableMultiset.Builder<Object> builder, Object value, int count)
        throws JacksonException
    {
        // sorted builders never compare a single element, so need explicit check
        _verifyComparable(ctxt, value);
        try {
            builder.addCopies(value, count);
        } catch (ClassCastException e) {
            // Sorted builders may sort (and fail) while elements are still being added
            _reportBuildFailure(ctxt, e.getMessage());
        }
        return builder;
    }
}
