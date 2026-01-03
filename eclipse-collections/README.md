One of standard [Jackson](https://github.com/FasterXML/jackson) Collection type [Datatype modules](https://github.com/FasterXML/jackson-datatypes-collections/wiki).
Supports JSON serialization and deserialization of
[Eclipse Collections](https://www.eclipse.org/collections/) data types.

Licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Status

[![Maven Central](https://img.shields.io/maven-central/v/tools.jackson.datatype/jackson-datatype-eclipse-collections.svg)](https://central.sonatype.com/artifact/tools.jackson.datatype/jackson-datatype-eclipse-collections)
[![Javadoc](https://javadoc.io/badge/tools.jackson.datatype/jackson-datatype-eclipse-collections.svg)](https://www.javadoc.io/doc/tools.jackson.datatype/jackson-datatype-eclipse-collections)

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>tools.jackson.datatype</groupId>
  <artifactId>jackson-datatype-eclipse-collections</artifactId>
  <version>3.0.3</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new EclipseCollectionsModule())
    .build();
```

after which functionality is available for all normal Jackson operations.

