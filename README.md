## Overview

This is a multi-module umbrella project for various [Jackson](../../../jackson)
Datatype modules to support 3rd party Collection libraries.

Currently included are:

* [Guava](guava/) datatype ([Guava](http://code.google.com/p/guava-libraries/))
* [HPPC](hppc/) datatype ([High-Performance Primitive Collections](https://labs.carrotsearch.com/hppc.html))
* [PCollections](pcollections/) datatype ([Persistent Java Collections](http://pcollections.org))

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-datatypes-collections.svg)](https://travis-ci.org/FasterXML/jackson-datatypes-collections)

## Usage

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

