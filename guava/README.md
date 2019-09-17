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
  <version>2.9.9</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
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
