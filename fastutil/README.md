One of standard [Jackson](https://github.com/FasterXML/jackson) Collection type [Datatype modules](https://github.com/FasterXML/jackson-datatypes-collections/wiki).
Supports JSON serialization and deserialization of
[fastutil](https://fastutil.di.unimi.it/) collection and map types.

Licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Status

[![Maven Central](https://img.shields.io/maven-central/v/tools.jackson.datatype/jackson-datatype-fastutil.svg)](https://central.sonatype.com/artifact/tools.jackson.datatype/jackson-datatype-fastutil)
[![Javadoc](https://javadoc.io/badge/tools.jackson.datatype/jackson-datatype-fastutil.svg)](https://www.javadoc.io/doc/tools.jackson.datatype/jackson-datatype-fastutil)

Module is new in Jackson 3.3.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>tools.jackson.datatype</groupId>
  <artifactId>jackson-datatype-fastutil</artifactId>
  <version>3.3.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

The module does not bring in fastutil itself (the dependency is `optional`): add one of the
two fastutil jars to your project as well.

### `fastutil` or `fastutil-core`

fastutil is published as two jars with the same package and class names:

* [`it.unimi.dsi:fastutil`](https://central.sonatype.com/artifact/it.unimi.dsi/fastutil): all types (about 23 MB)
* [`it.unimi.dsi:fastutil-core`](https://central.sonatype.com/artifact/it.unimi.dsi/fastutil-core): a subset (about 6 MB) with
  * all collections and maps of `int`, `long` and `double` (and objects), including maps between
    these types (like `Int2LongMap` or `Object2DoubleMap`)
  * only lists (`XCollection`, `XList`, `XBigList`, `XArrayList`, `XImmutableList`) of `short`, `char` and `float`
  * no collections of `boolean` or `byte`, no maps with `byte`, `short`, `char`, `float` or `boolean`
    keys or values, and no `Reference` types (like `ReferenceList` or `Int2ReferenceMap`)

The module works with either jar: support for types that are not available is skipped. Use only
one of them, since they contain the same classes.

```xml
<dependency>
  <groupId>it.unimi.dsi</groupId>
  <artifactId>fastutil</artifactId> <!-- or `fastutil-core` -->
  <version>8.5.19</version>
</dependency>
```

Either jar may also be used on the module path: although the Java module name of `fastutil-core`
(`it.unimi.dsi.fastutil.core`) differs from that of `fastutil` (`it.unimi.dsi.fastutil`, which the
module descriptor of this module refers to), the module adds the needed "reads" edge at runtime.

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new FastutilModule())
    .build();
```

after which functionality is available for all normal Jackson operations.

## Supported types

All fastutil containers implement the matching `java.util` interfaces (`IntList` is a
`List<Integer>`, `Int2LongMap` is a `Map<Integer, Long>`), so Jackson can serialize them
without this module, but with boxing of every element, and it cannot deserialize
abstract types like `IntList` at all. This module adds:

* Collections of primitives (`boolean`, `byte`, `short`, `char`, `int`, `long`, `float`, `double`):
  `XCollection`, `XList`, `XBigList`, `XSet`, `XSortedSet` and their implementations
  (`XArrayList`, `XImmutableList`, `XBigArrayBigList`, `XOpenHashSet`, `XLinkedOpenHashSet`,
  `XArraySet`, `XRBTreeSet`, `XAVLTreeSet`, `XOpenHashBigSet`...), (de)serialized without boxing.
* Maps with primitive keys and/or values: `X2YMap`, `X2YSortedMap` and their implementations
  (`X2YOpenHashMap`, `X2YLinkedOpenHashMap`, `X2YArrayMap`, `X2YRBTreeMap`, `X2YAVLTreeMap`),
  for every primitive or `Object` key `X` and value `Y` (for example `Int2LongMap`,
  `Object2IntMap<K>`, `Long2ObjectMap<V>`), deserialized without boxing. Maps with primitive
  keys and values (like `Int2LongMap`) are also serialized without boxing; maps with `Object`
  keys or values are serialized by the standard Jackson `Map` serializer, so that all its
  features (like `@JsonInclude(content = ...)`) are supported.
* Maps with identity-based (`Reference`) keys and primitive values (`Reference2IntMap`...):
  deserialized like the matching `Object2YMap` (so values are handled the same way), and
  copied into a `Reference2YOpenHashMap` (`Reference2YLinkedOpenHashMap` for `Reference2YSortedMap`).
* Other containers of references (`ObjectList`, `ObjectSet`, `Object2ObjectMap`,
  `Int2ReferenceMap`...): abstract types are mapped to default implementations
  (`ObjectArrayList`, `ObjectOpenHashSet`, `Object2ObjectOpenHashMap`...) and handled by the
  standard Jackson `Collection` and `Map` (de)serializers.

When deserializing into an interface, the default implementation is `XArrayList`
(`XCollection`, `XList`), `XBigArrayBigList` (`XBigList`), `XOpenHashSet` (`XSet`),
`XRBTreeSet` (`XSortedSet`), `X2YOpenHashMap` (`X2YMap`) or `X2YRBTreeMap` (`X2YSortedMap`).
Abstract classes (like `AbstractIntSet`) use the default implementation of the most specific of
these interfaces they implement. Concrete types with a no-arguments constructor are used as-is.

Other Jackson features work as for the equivalent `java.util` types, including merging
(`@JsonMerge`, `ObjectMapper.readerForUpdating()`: elements are added, map entries replaced),
`@JsonSetter(nulls = Nulls.AS_EMPTY)`, `@JsonSetter(contentNulls = Nulls.SKIP)` (or `FAIL`),
per-property `@JsonFormat(with = ACCEPT_SINGLE_VALUE_AS_ARRAY)` and
`SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS`.
Custom serializers or deserializers for primitive elements, keys or values (like
`@JsonDeserialize(contentUsing = ...)`) are also supported, but take boxed values: containers
that use them are (de)serialized by the standard Jackson handlers, and copied into the fastutil
container when deserializing.

### Polymorphic typing

With polymorphic type handling (for example `activateDefaultTyping()`), type ids name the
runtime class, which may be one of the wrappers created by fastutil helper classes, like
`IntLists.unmodifiable(...)`, `Int2LongMaps.synchronize(...)`, `IntSets.singleton(...)` or
`IntLists.emptyList()`. For collections of primitives and maps with primitive keys and/or values,
these are deserialized into an order-preserving implementation that is then wrapped using the
same helper method; singleton and empty instances become unmodifiable wrappers. `XImmutableList`
is built from an `XArrayList`.

Wrappers of containers of references (like `ObjectLists.unmodifiable(...)` or
`ObjectSets.synchronize(...)`) are generally not supported: these are handled by standard
Jackson deserializers, which cannot create them.

Note that a `PolymorphicTypeValidator` has to allow the fastutil types, for example:

```java
PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
    .allowIfSubType("it.unimi.dsi.fastutil.")
    .build();
```

### Output format

Collections are written as JSON Arrays and maps as JSON Objects, in the same format Jackson
uses for the equivalent `java.util` types, with these exceptions:

* `char` collections are written as a single JSON String (`"abc"`), unless
  `SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS` is enabled (`["a","b","c"]`); both
  forms are accepted when deserializing. This matches the Eclipse Collections and HPPC modules.

## Implementation notes: generated sources (JPSG)

fastutil has a separate type for every combination of primitive key and value type: there are
71 `X2YMap` families (8 key types times 9 value types, excluding `Object2ObjectMap`). Rather
than writing a deserializer and a serializer by hand for each, this module generates them
at build time with the
[Java Primitive Specializations Generator](https://github.com/TimeAndSpaceIO/java-primitive-specializations-generator)
(`jpsg-maven-plugin`), the same tool used by the Eclipse Collections module.

Templates live in `src/main/javaTemplates/` and are valid Java source written for one
combination of types: for example `TypeHandlerPairs.java` is written for `Byte2ShortMap`.
Comment directives control the expansion:

```java
/* with
    byte|char|short|int|long|float|double|object key
    short|byte|char|int|long|float|double|object|boolean value
*/
private static final PrimitiveMapSerializer<Byte2ShortMap> BYTE_SHORT = ...
    g.writeName(String.valueOf(e.getByteKey()));
    /* if !(char|boolean value) */
    g.writeNumber(e.getShortValue());
    /* elif boolean value //
    g.writeBoolean(e.getShortValue());
    // endif */
/* endwith */
```

* `/* with ... */ ... /* endwith */` repeats the enclosed block once for every combination of
  the listed dimensions; the first option of each dimension is the one the template is
  written for.
* Within the block, type names are replaced in identifiers in all their forms: `Byte` becomes
  `Int` (`Byte2ShortMap` to `Int2ShortMap`, `getByteKey()` to `getIntKey()`), `BYTE` becomes
  `INT` and `byte` becomes `int`.
* `/* if ... */ ... /* elif ... // ... // endif */` keeps only the branch that matches the
  current combination; `/* define */` declares reusable snippets.

During the `generate-sources` phase the plugin writes the expanded Java files to
`target/generated-sources/jpsg/`, and they are compiled with the rest of the module. Generated
files are not checked in: to change them, edit the templates. The templates are:

| Template | Generates |
|---|---|
| `deser/map/TypeHandlerPairs.java` | Key/value handlers and the registration of all 71 map families for deserialization |
| `ser/map/PrimitivePrimitiveMapSerializers.java` | Serializers for maps with primitive keys and values (like `Int2LongMap`) |

Collection (de)serializers only need one class per primitive type, so they are regular,
hand-written sources under `src/main/java/`.

## Implementation notes: optional types (`fastutil-core`)

Since classes may be missing at runtime when `fastutil-core` is used, handlers are registered
through `OptionalTypes.registerIfPresent(...)` (or `ContainerFamily.addIfPresent(...)`), with all
references to fastutil types (class literals like `IntSet.class`, method references like
`IntOpenHashSet::new`) inside the lambda passed to it: these load the referenced classes when
evaluated, so a `NoClassDefFoundError` then only skips that one registration. Keep to this pattern
when adding support for more types, including in the JPSG templates.

The build runs `FastutilCoreTest` a second time with only `fastutil-core` on the class path (see the
`test-fastutil-core` Surefire execution in `pom.xml`). In the normal test run,
`RoundTripMatrixTest` verifies that no handler is skipped with the full `fastutil` jar.
