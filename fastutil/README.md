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
  `Object2IntMap<K>`, `Long2ObjectMap<V>`), (de)serialized without boxing.
* Containers of references (`ObjectList`, `ObjectSet`, `Object2ObjectMap`, `Reference2IntMap`,
  `Int2ReferenceMap`...): abstract types are mapped to default implementations
  (`ObjectArrayList`, `ObjectOpenHashSet`, `Object2ObjectOpenHashMap`...) and handled by the
  standard Jackson `Collection` and `Map` (de)serializers.

When deserializing into an interface, the default implementation is `XArrayList`
(`XCollection`, `XList`), `XBigArrayBigList` (`XBigList`), `XOpenHashSet` (`XSet`),
`XRBTreeSet` (`XSortedSet`), `X2YOpenHashMap` (`X2YMap`) or `X2YRBTreeMap` (`X2YSortedMap`).
Concrete types with a no-arguments constructor are used as-is.

### Polymorphic typing

With polymorphic type handling (for example `activateDefaultTyping()`), type ids name the
runtime class, which may be one of the wrappers created by fastutil helper classes, like
`IntLists.unmodifiable(...)`, `Int2LongMaps.synchronize(...)`, `IntSets.singleton(...)` or
`IntLists.emptyList()`. These are deserialized into an order-preserving implementation that is
then wrapped using the same helper method; singleton and empty instances become unmodifiable
wrappers. `XImmutableList` is built from an `XArrayList`.

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
| `ser/map/PrimitiveRefMapSerializers.java` | Serializers for maps with primitive keys and reference values (like `Int2ObjectMap`) |
| `ser/map/RefPrimitiveMapSerializers.java` | Serializers for maps with reference keys and primitive values (like `Object2IntMap`) |

Collection (de)serializers only need one class per primitive type, so they are regular,
hand-written sources under `src/main/java/`.
