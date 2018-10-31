## Overview

This is a multi-module umbrella project for various [Jackson](../../../jackson)
Datatype modules to support 3rd party Collection libraries.

Currently included are:

* [Guava](guava/) datatype ([Guava](http://code.google.com/p/guava-libraries/)): `jackson-datatype-guava`
* [HPPC](hppc/) datatype ([High-Performance Primitive Collections](https://labs.carrotsearch.com/hppc.html)): `jackson-datatype-hppc`
* [PCollections](pcollections/) datatype ([Persistent Java Collections](http://pcollections.org)): `jackson-datatype-pcollections`
* [Eclipse Collections](eclipse-collections/) datatype ([https://github.com/eclipse/eclipse-collections](https://www.eclipse.org/collections/): `jackson-datatype-eclipse-collections`

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-datatypes-collections.svg)](https://travis-ci.org/FasterXML/jackson-datatypes-collections)

## Usage

### Maven dependencies

To use these format backends Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-[COLLECTION]</artifactId>
  <version>2.9.5</version>
</dependency>
```

where `COLLECTION` would be one of `guava`, `hppc` or `pcollections`.

NOTE! Parent pom itself only specifies defaults to individual modules but
DOES NOT include them, so you CAN NOT just add dependency to `jackson-datatypes-collections`.
Individual datatype modules need to be included explicitly (or via some other pom
that depends on them).

### Registration with ObjectMapper

Like all standard Jackson modules (libraries that implement Module interface), registration for Collections
datatypes is done as follows:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new GuavaModule())
    .registerModule(new HppcModule())
    .registerModule(new PCollectionsModule())
    ;
```

after which datatype read/write support is available for all normal Jackson operations,
including support for nested types.

### Usage with Spring Boot

```java
@Bean
public Jackson2ObjectMapperBuilderCustomizer customize()
{
    return builder -> builder.modules( new GuavaModule() );
}
```
