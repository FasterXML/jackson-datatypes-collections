[Jackson](../../../jackson) datatype module (jar)
to support JSON serialization and deserialization of
[PCollections](https://pcollections.org/) data types.

Licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Status

[![Maven Central](https://img.shields.io/maven-central/v/tools.jackson.datatype/jackson-datatype-pcollections.svg)](https://maven-badges.herokuapp.com/maven-central/tools.jackson.datatype/jackson-datatype-pcollections/)
[![Javadoc](https://javadoc.io/badge/tools.jackson.datatype/jackson-datatype-pcollections.svg)](https://www.javadoc.io/doc/tools.jackson.datatype/jackson-datatype-pcollections)

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>tools.jackson.datatype</groupId>
  <artifactId>jackson-datatype-pcollections</artifactId>
  <version>3.0.3</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new PCollectionsModule())
    .build();
```

after which functionality is available for all normal Jackson operations.

## More

See [Wiki](../../wiki) for more information (javadocs, downloads).
