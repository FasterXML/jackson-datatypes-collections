[Jackson](../../../jackson) datatype module (jar)
to support JSON serialization and deserialization of
[High-Performance Primitive Collections](https://labs.carrotsearch.com/hppc.html) datatypes.

Licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Status

[![Maven Central](https://img.shields.io/maven-central/v/tools.jackson.datatype/jackson-datatype-hppc.svg)](https://maven-badges.herokuapp.com/maven-central/tools.jackson.datatype/jackson-datatype-hppc/)
[![Javadoc](https://javadoc.io/badge/tools.jackson.datatype/jackson-datatype-hppc.svg)](https://www.javadoc.io/doc/tools.jackson.datatype/jackson-datatype-hppc)

Currently, the following things are supported:

* Serializing of all 'XxxContainer' (IntContainer, IntSet, IntArrayList, LongContainer, DoubleContainer etc) types
* Deserialization of all `int` valued container types

and following are not yet supported:

* Deserialization for other primitive types
* Handling of 'map' types (ie. 'XYAssociateContainer' implementations)

Plan is to support full fidelity of Jackson annotation configurability; meaning that all generic types (ones with 'Object' in name, and with generic type parameter) could be supported; as well as use of included type information.

However, due to on-going competing work, at this point (May 2015) the best way to get additional coverage is to
contribute code. Jackson team is happy to merge code contributions, and help with implementation details.

## Usage

### JDK version

Starting with version 3.0 of the module, minimum JDK is 17.

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>tools.jackson.datatype</groupId>
  <artifactId>jackson-datatype-hppc</artifactId>
  <version>3.0.3</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

To use the the Module in Jackson, simply register it with the ObjectMapper instance:
Modules are registered through ObjectMapper, like so:

```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new HppcModule())
    .build();
```

after which you can read JSON as HPPC types, as well as write HPPC types as JSON. It's really that simple; convenient and efficient.
