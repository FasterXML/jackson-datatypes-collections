One of standard [Jackson](https://github.com/FasterXML/jackson) Collection type [Datatype modules](https://github.com/FasterXML/jackson-datatypes-collections/wiki).
Supports JSON serialization and deserialization of
[Eclipse Collections](https://www.eclipse.org/collections/) data types.

Licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Status

[![Maven Central](https://img.shields.io/maven-central/v/com.fasterxml.jackson.datatype/jackson-datatype-eclipse-collections.svg)](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.jackson.datatype/jackson-datatype-eclipse-collections/)
[![Javadoc](https://javadoc.io/badge/com.fasterxml.jackson.datatype/jackson-datatype-eclipse-collections.svg)](https://www.javadoc.io/doc/com.fasterxml.jackson.datatype/jackson-datatype-eclipse-collections)

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-eclipse-collections</artifactId>
  <version>2.14.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new EclipseCollectionsModule());
```

after which functionality is available for all normal Jackson operations.

