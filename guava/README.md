One of standard [Jackson](../../../..jackson) Collection type [Datatype modules](../../..).
Supports JSON serialization and deserialization of
[Guava](http://code.google.com/p/guava-libraries/) data types.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-guava</artifactId>
  <version>2.11.1</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Guava compatibility

Jackson 2.x versions up to and including 2.11.x:

* Should work on Guava versions `15.0` and later (currently up to 29.0-jre)
* Require Guava version `20.0` or later to build (some tests depend on newer versions for verification)

Minimum supported version is likely to be increased for Jackson 2.12 or later.

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
// New (2.10+)
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new GuavaModule())
    .build();

// Old (before 2.10, but works on all 2.x)
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new GuavaModule());
```

after which functionality is available for all normal Jackson operations.

### Configuration

Configurable settings of the module are:

* `configureAbsentsAsNulls` (default: true) (added in 2.6)
    * If enabled, will consider `Optional.absent()` to be "null-equivalent", and NOT serialized if inclusion is defined as `Include.NON_NULL`
    * If disabled, `Optional.absent()` behaves as standard referential type, and is included with `Include.NON_NULL`
    * In either case, `Optional.absent()` values are always excluded with Inclusion values of:
        * NON_EMPTY
        * NON_ABSENT (new in Jackson 2.6)
