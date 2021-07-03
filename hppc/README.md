[Jackson](../../../jackson) datatype module (jar)
to support JSON serialization and deserialization of
[High-Performance Primitive Collections](http://labs.carrotsearch.com/hppc.html) datatypes.

Licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

## Status

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.jackson.datatype/jackson-datatype-hppc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.jackson.datatype/jackson-datatype-hppc/)
[![Javadoc](https://javadoc.io/badge/com.fasterxml.jackson.datatype/jackson-datatype-hppc.svg)](http://www.javadoc.io/doc/com.fasterxml.jackson.datatype/jackson-datatype-hppc)

Currently (2.7) following things are supported:

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

Starting with version 2.6 of the module, minimum JDK is 1.7. This is because
HPPC 0.7.1 requires it. Jackson core components only require 1.6.

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-hppc</artifactId>
  <version>2.5.3</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

To use the the Module in Jackson, simply register it with the ObjectMapper instance:
Modules are registered through ObjectMapper, like so:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.register(new HppcModule());
```

after which you can read JSON as HPPC types, as well as write HPPC types as JSON. It's really that simple; convenient and efficient.
