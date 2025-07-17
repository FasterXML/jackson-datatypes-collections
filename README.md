## Overview

This is a multi-module umbrella project for various [Jackson](../../../jackson)
Datatype modules to support 3rd party Collection libraries.

Currently included are:

* [Eclipse Collections](eclipse-collections/) datatype (for [Eclipse Collections](https://www.eclipse.org/collections/)): `jackson-datatype-eclipse-collections` (since 2.10)
* [Guava](guava/) datatype (for [Guava library](https://github.com/google/guava)): `jackson-datatype-guava`
* [HPPC](hppc/) datatype (for [High-Performance Primitive Collections](https://labs.carrotsearch.com/hppc.html)): `jackson-datatype-hppc`
* [PCollections](pcollections/) datatype (for [Persistent Java Collections](https://pcollections.org/)): `jackson-datatype-pcollections`

## License

All modules are licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).

## Build Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-datatypes-collections.svg)](https://travis-ci.org/FasterXML/jackson-datatypes-collections)

## Usage, general

### Maven dependencies

To use these format backends Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-[COLLECTION]</artifactId>
  <version>2.19.1</version>
</dependency>
```

where `COLLECTION` would be one of `guava`, `hppc`, `pcollections`, or `eclipse-collections`
(replace version with the latest available).

You may also use [jackson-bom](https://github.com/FasterXML/jackson-bom) for defining
consistent sets of versions of various Jackson components.

NOTE! Parent pom itself only specifies defaults to individual modules but
DOES NOT include them, so you CAN NOT just add dependency to `jackson-datatypes-collections`.
Individual datatype modules need to be included explicitly (or via some other pom
that depends on them).

### Registration with ObjectMapper

Like all standard Jackson modules (libraries that implement Module interface), registration for Collections
datatypes is done using one of 2 mechanisms:

```java
ObjectMapper mapper;

// New; 2.10.x / 3.0:
mapper = JsonMapper.builder() // or mapper for other formats
    .addModule(new GuavaModule())
    .addModule(new HppcModule())
    .addModule(new PCollectionsModule())
    .build();

// Old (2.x), not available on 3.x:
mapper = new ObjectMapper() // or mapper for other formats
    .registerModule(new GuavaModule())
    .registerModule(new HppcModule())
    .registerModule(new PCollectionsModule())
    .registerModule(new EclipseCollectionsModule())
    ;
```

after which datatype read/write support is available for all normal Jackson operations,
including support for nested types.

## Usage, per-datatype

See READMEs of individual modules for datatype-specific configuration, options
and so on:

* [jackson-datatype-eclipse-collections](eclipse-collections/)
* [jackson-datatype-guava](guava/)
* [jackson-datatype-hpcc](hppc/)
* [jackson-datatype-pcollections](pcollections/)

### Usage with Spring Boot

```java
@Bean
public Jackson2ObjectMapperBuilderCustomizer customize()
{
    return builder -> builder.modules( new GuavaModule() );
}
```
