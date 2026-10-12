# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Jackson Datatypes Collections is a multi-module Maven project providing Jackson datatype support for third-party collection libraries. The repository contains 5 modules:

- **guava**: Support for Google Guava collections (most mature)
- **eclipse-collections**: Support for Eclipse Collections (uses JPSG code generation)
- **hppc**: Support for High-Performance Primitive Collections
- **pcollections**: Support for Persistent Java Collections
- **primitive-collections-base**: Shared base code for primitive collection modules

This is the **3.x development branch** for the next 3.x minor release (current: 3.3.0-SNAPSHOT). Maintenance branches `3.1` and `3.2` hold patch releases; fixes are merged forward from `2.x` into `3.1`, then `3.1` → `3.2` → `3.x`. Development follows the Jackson ecosystem patterns.

## Build Commands

### Basic Build and Test
```bash
./mvnw clean package
```

### Run Tests Only
```bash
./mvnw test
# Or with surefire explicitly:
./mvnw surefire:test
```

### Build a Single Module
```bash
cd guava
../mvnw clean package
```

### Run a Single Test Class
```bash
./mvnw test -Dtest=ClassName
# Example:
cd guava
../mvnw test -Dtest=ImmutableContainersTest
```

### Run a Single Test Method
```bash
./mvnw test -Dtest=ClassName#methodName
```

### Test with Different Guava Versions
The CI tests against multiple Guava versions. To test locally:
```bash
./mvnw clean test -Dversion.guava=25.1-jre
./mvnw clean test -Dversion.guava=33.5.0-jre
```

### Skip Tests
```bash
./mvnw clean package -DskipTests
```

### Generate JPSG Code (Eclipse Collections)
Eclipse Collections module uses JPSG (Java Primitive Specialization Generator) to generate code from templates in `src/main/javaTemplates` and `src/test/javaTemplates`:
```bash
cd eclipse-collections
../mvnw generate-sources
../mvnw generate-test-sources
```

Generated code appears in `target/generated-sources/jpsg` and `target/generated-test-sources/jpsg`.

## Architecture and Code Structure

### Module Pattern
Each datatype module follows a consistent pattern:

1. **Module class** (e.g., `GuavaModule`, `EclipseCollectionsModule`): Extends `JacksonModule` and implements `setupModule()` to register serializers/deserializers
2. **Serializers** (in `ser/` package): Handle writing collection types to JSON
3. **Deserializers** (in `deser/` package): Handle reading JSON into collection types
4. **Type modifiers**: Handle generic type resolution for parameterized collections

### Package Structure
Each module uses the pattern:
```
src/main/java/tools/jackson/datatype/<module>/
  ├── <Module>Module.java         # Main module registration
  ├── <Module>Serializers.java    # Serializer provider
  ├── <Module>Deserializers.java  # Deserializer provider
  ├── ser/                         # Individual serializers
  ├── deser/                       # Individual deserializers
  └── util/                        # Shared utilities (optional)
```

### Shared Base Module
`primitive-collections-base` provides common serialization/deserialization logic for primitive-focused collection libraries (HPPC, Eclipse Collections). If working on primitive collection support, check this module first to avoid duplication.

### Java Module System
All modules use Java 9+ module system with `module-info.java`. When adding dependencies or new packages, update the module descriptors accordingly.

### Version Generation
Each module auto-generates `PackageVersion.java` during build via the maven-replacer-plugin. Do not edit these generated files manually.

## Testing

- Tests use JUnit 5 (Jupiter)
- Test module descriptors are in `src/test/java/module-info.java`
- Test packages must be opened in module-info for JUnit access
- CI runs tests with Java 17, 21, and 25
- Guava module tests against multiple Guava versions (min: 25.1-jre, max: 33.5.0-jre)

## Jackson Integration Context

### Module Registration (3.x style)
```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new GuavaModule())
    .build();
```

Note: Jackson 3.x removed the old `mapper.registerModule()` pattern. Always use the builder pattern.

### Configuration
Some modules (like Guava) support configuration before registration:
```java
GuavaModule module = new GuavaModule()
    .configureAbsentsAsNulls(false)
    .defaultBoundType(BoundType.CLOSED);

ObjectMapper mapper = JsonMapper.builder()
    .addModule(module)
    .build();
```

## Dependencies

- Parent: `tools.jackson:jackson-base:3.3.0-SNAPSHOT`
- Core deps: `tools.jackson.core:jackson-core`, `tools.jackson.core:jackson-databind`
- Each module depends on its respective collection library
- Tests use JUnit 5 (Jupiter)

## Common Development Patterns

### Adding Support for a New Collection Type

1. Create serializer in `ser/` extending appropriate Jackson serializer base class
2. Create deserializer in `deser/` extending appropriate Jackson deserializer base class
3. Register in the module's `Serializers`/`Deserializers` class
4. Add tests covering serialization, deserialization, and round-trip
5. Update module-info.java if new packages are introduced

### Working with JPSG (Eclipse Collections)

JPSG generates specialized primitive implementations from templates. Template files use special syntax:
- Located in `src/main/javaTemplates` and `src/test/javaTemplates`
- Run `mvnw generate-sources` to generate code before building
- Generated files should NOT be committed to git
- If modifying primitive collection support, edit templates, not generated code

## Release Notes

Release notes are in the `release-notes/` directory: `VERSION` covers 3.x releases (`VERSION-2.x` for 2.x), and `CREDITS` lists contributors. When making significant changes on this branch, add an entry in `VERSION` under the unreleased `3.3.0` section at the top. (Fixes meant for a patch release belong on the `3.1` or `3.2` branch, under that branch's next patch version.)
