Jackson](../../../jackson) datatype module (jar)
to support JSON serialization and deserialization of
[PCollections](http://pcollections.org/) data types.

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-datatype-pcollections.svg)](https://travis-ci.org/FasterXML/jackson-datatype-pcollections)

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-pcollections</artifactId>
  <version>2.7.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new PCollectionsModule());
```

after which functionality is available for all normal Jackson operations.

## More

See [Wiki](../../wiki) for more information (javadocs, downloads).
