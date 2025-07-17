One of standard [Jackson](../../../..jackson) Collection type [Datatype modules](../../..).
Supports JSON serialization and deserialization of
[Guava](https://github.com/google/guava) data types.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-guava</artifactId>
  <version>2.19.1</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Guava compatibility

Although specific version of this module is built against particular Guava library version,
and similarly defines dependency to just that version, module itself works against wider
range of Guava versions.

Following table shows the tested working ranges for recent module versions.

| Module version | Min Guava | Default Guava | Max Guava |
| -------------- | --------- | ------------- | --------- |
| 2.20           | 22.0      | 25.1-jre      | 33.4.8-jre|
| 2.19           | 20.0      | 25.1-jre      | 33.1.0-jre|
| 2.18           | 20.0      | 25.1-jre      | 33.1.0-jre|
| 2.17           | 20.0      | 25.1-jre      | 33.1.0-jre|
| 2.16           | 20.0      | 25.1-jre      | 33.1.0-jre|
| 2.15           | 14.0      | 23.6.1        | 31.1-jre  |
| 2.14           | 14.0      | 21.0          | 31.1-jre  |
| 2.13           | 14.0      | 21.0          | 30.1-jre  |
| 2.12           | 14.0      | 21.0          | 30.1-jre  |
| 2.11           | 14.0      | 20.0          | 30.1-jre  |
| 2.10           | 14.0      | 20.0          | 30.1-jre  |
| 2.9            | 12.0      | 18.0          | 30.1-jre  |

Notes:

* At the point of testing of 2.14 - 2.19,, `31.1-jre` was the latest available Guava library
version, so all versions work with the latest Guava
* "Min Guava" means the earliest version that integration tests pass with
* "Default Guava" is the dependency specified in module's `pom.xml`: it is used for build, unit tests
* Build (compilation, running of unit tests) works on a subset of specified compatibility range: typically version needed is above "Min Guava", but lower than "Max Guava"

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows (Jackson 2.x up to 2.9)

```java
// New (2.10+)
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new GuavaModule())
    .build();

// Old (before 2.10, but works on all 2.x)
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new GuavaModule());
```

OR, the new method added in 2.10 (old method will work with 2.x but not 3.x):

```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new GuavaModule())
    .build();
```

after which functionality is available with all normal Jackson operations.

### Configuration

Configurable settings of the module are:

* `configureAbsentsAsNulls` (default: true) (added in 2.6)
    * If enabled, will consider `Optional.absent()` to be "null-equivalent", and NOT serialized if inclusion is defined as `Include.NON_NULL`
    * If disabled, `Optional.absent()` behaves as standard referential type, and is included with `Include.NON_NULL`
    * In either case, `Optional.absent()` values are always excluded with Inclusion values of:
        * NON_EMPTY
        * NON_ABSENT (new in Jackson 2.6)
