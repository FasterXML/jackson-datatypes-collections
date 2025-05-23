Project: jackson-datatypes-collections
Modules:
  jackson-datatype-guava
  jackson-datatype-hppc
  jackson-datatype-pcollections
  jackson-datatype-eclipse-collections (since 2.10)

Active Maintainers:

* Steven Schlansker (@stevenschlansker): original author of Guava module
* Jonas Konrad (@yawkat): author of Eclipse Collections module
* Tatu Saloranta (@cowtowncoder): author for HPPC, Pcollections modules;
   co-author for Guava, Eclipse collections

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.20.0 (not yet released)

#190: Add unit tests to verify goodness of SPI metadata for Modules
- Generate SBOMs [JSTEP-14]

2.19.0 (24-Apr-2025)

#1: (guava) Add deserialization support for `Table<R, C, V>`
 (contributed by Abhishek K)
#174: Unify testing structure/tools [JSTEP-10]
 (contributed by Joo-Hyuk K)

2.18.4 (06-May-2025)
2.18.3 (28-Feb-2025)
2.18.2 (27-Nov-2024)
2.18.1 (28-Oct-2024)

No changes since 2.18.0

2.18.0 (26-Sep-2024)

#160: (guava) Defect guava OSGi dependency version specified?
 (reported by @jpstotz)
- HPPC dependency upgraded to 0.9.1 (from 0.8.2)

2.17.3 (01-Nov-2024)
2.17.2 (05-Jul-2024)

No changes since 2.17.1

2.17.1 (04-May-2024)

#149: (guava) Update CI to use Matrix Build to test against different
  Guava versions
- HPPC dependency upgraded to 0.8.2 (from 0.8.1)

2.17.0 (12-Mar-2024)

#118: (guava) Support @JsonFormat(shape=STRING) on Range<T>
 (contributed by Muhammad K)
#124: (guava) Some deserializers throw unexpected `NullPointerException` when
  handling invalid input
 (contributed by Arthur C)
#135 (guava) Add deserialization support for bracket notation Range
 (contributed by Muhammad K)
#136 (guava) Fix for failing Guava `Optional` test
 (contributed by Muhammad K)
#138 (guava) `GuavaCollectionDeserializer` still throws NPE in some circumstances
 (contributed by Arthur C)
#140 (guava) `Cache` deserialization fails with NPE for `null` valued entries
#142 (guava) `RangeSet` deserializer fails for content `null`s with NPE

2.16.2 (09-Mar-2024)

#145: `GuavaOptionalDeserializer.getEmptyValue()` should not call itself recursively

2.16.1 (24-Dec-2023)

No changes since 2.16.0

2.16.0 (15-Nov-2023)

#90: (guava) Cache Serialization serializes empty contents
 (reported by Wolff B)
 (fix contributed by Joo-Hyuk K)
#113: (guava) Update default Guava dependency for Jackson 2.16 from Guava 23.x to 25.x
#116: (guava) Suppport simple deserialization of `Cache`
 (contributed by Joo-Hyuk K)
#117: (guava) `ImmutableRangeSet` fails to deserialize without explicit deserializer
 (contributed by Joo-Hyuk K)
#122: PCollections module info (`module-info.class`) incorrect: update to 4.0.1
 (reported by Ethan M)

2.15.4 (15-Feb-2024)
2.15.3 (12-Oct-2023)
2.15.2 (30-May-2023)
2.15.1 (16-May-2023)

No changes since 2.15.0

2.15.0 (23-Apr-2023)

#7: (guava) Add support for `WRITE_SORTED_MAP_ENTRIES` for Guava `Multimap`s
 (contributed by Joo-Hyuk K)
#92: (guava) `@JsonDeserialize.contentConverter` does not work for non-builtin collections
 (reported by Marcin W)
 (fix contributed by Joo-Hyuk K)
#102: (guava) accept lowerCase enums for `Range` `BoundType` serialization
 (requested by Andrey B)
 (fix contributed by Joo-Hyuk K)
#105: (guava) Update Guava dependency to 23.6.1-jre (from 21.0)
#106: (guava) There maybe a misusage in GuavaMultimapDeserializer.findTransformer method
 (contributed by @magical-l)

2.14.3 (05-May-2023)

#92: (guava) `@JsonDeserialize.contentConverter` does not work for non-builtin collections
 (reported by Marcin W)
 (fix contributed by Joo-Hyuk K)
#104: `ArrayListMultimapDeserializer` does not support multimaps inside
  another object as a property
 (reported by @magical-l)

2.14.2 (28-Jan-2023)
2.14.1 (21-Nov-2022)

No changes since 2.14.1

2.14.0 (05-Nov-2022)

No changes since 2.13

2.13.4 (03-Sep-2022)

#94: Eclipse Collection serialization for Pairs does not work when upgrading to EC version 11.0.0
 (fixed by yawkat)

2.13.3 (14-May-2022)
2.13.2 (06-Mar-2022)
2.13.1 (19-Dec-2021)

No changes since 2.13.0

2.13.0 (30-Sep-2021)

#85: (eclipse-collections) Update eclipse-collections to version 10 and implement Triple deserialization. Still compatible with older EC versions
- (guava) Serialize `Cache` instances as empty Objects (see #90)

2.12.7 (26-May-2022)
2.12.6 (15-Dec-2021)
2.12.5 (27-Aug-2021)
2.12.4 (06-Jul-2021)
2.12.3 (12-Apr-2021)

No changes since 2.12.2

2.12.2 (03-Mar-2021)

- Add missing "provides Module" for eclipse-collections module-info (JPMS)

2.12.1 (08-Jan-2021)

No changes since 2.12.0

2.12.0 (29-Nov-2020)

#25: (guava) SetMultimap should be deserialized to a LinkedHashMultimap by default
 (requested by Tommy B; fix contributed by Migwel G)
#79: (guava)  Guava's RangeHelper causing NPE in PropertyNamingStrategy
 (reported by arpit-pp@github)
- Add Gradle Module Metadata (https://blog.gradle.org/alignment-with-gradle-module-metadata)
- Update "preferred" Guava version to 21.0

2.11.4 (12-Dec-2020)

* Add missing "LICENSE" file for Guava- and Eclipse-collections modules
* Add missing SPI metadata for auto-detecti Eclipse-collections module

2.11.3 (02-Oct-2020)

#71 (eclipse-collections) Can not deserialize concrete class instance inside nested
  immutable eclipse-collection
 (fixed by yawkat)

2.11.2 (02-Aug-2020)

#68: Support for Guava 29
 (suggested by Mark W)

2.11.1 (25-Jun-2020)
2.11.0 (26-Apr-2020)

No changes since 2.10.x

2.10.5 (21-Jul-2020)

#67: Guava collection deserialization failure with `Nulls.AS_EMPTY`
 (reported by ari-talja-rovio@github)

2.10.4 (03-May-2020)
2.10.3 (03-Mar-2020)
2.10.2 (05-Jan-2020)

No changes since 2.10.1

2.10.1 (09-Nov-2019)

- (guava) Minor fix to `module-info` to guava module

2.10.0 (26-Sep-2019)

#34: (guava) Allow use of Guava versions up to 25 (from 22)
 (suggested by Philipp H, AUTplayed@github)
#37: (eclipse-collections) Implement pair deserialization
 (contributed by Jonas K)
#48: (all) Add simple module-info for JDK9+, using Moditect
#50: (guava) Add Serializer and Deserializer for `RangeSet`
 (contributed by Felix K)
#53: (guava) `GuavaImmutableCollectionDeserializer` cannot deserialize
  an empty `Optional` from null
 (reported by Jon F)
#56: (guava) Range property name (de)serialisation doesn't respect property naming strategy
 (reported, fix contributed by Philip L)
#58: (guava) Drop support for Guava v10 - v13 to simplify `RangeFactory`
- Support nested `Optional` values wrt null provider (see [databind#2303]
- Update "preferred" Guava version to 20.0

2.9.10 (21-Sep-2019)

No changes since 2.9.9

2.9.9 (16-May-2019)

#45: HostAndPortDeserializer rely on older version property name
 (reported by Jakub B)

2.9.8 (15-Dec-2018)
2.9.7 (19-Sep-2018)

No changes since 2.9.6

2.9.6 (12-Jun-2018)

- First experimental version of `jackson-datatype-eclipse-collections`!
 (by 

2.9.5 (26-Mar-2018)

(guava) #27: Null value handling not supported for `Optional` within `Multimap`
 (reported by Neelav R)
(e-collections) #29: Add eclipse-collections support
 (contributed by Jonas K)

2.9.4 (24-Jan-2018)
2.9.3 (09-Dec-2017)
2.9.2 (14-Oct-2017)
2.9.1 (07-Sep-2017)

No changes since 2.9.0

2.9.0 (30-Jul-2017)

(guava) #15: Add support for java serialization to guava module
 (suggested by gcooney@github)
- (guava) Upgrade guava dependency from 16 to 18

2.8.11 (24-Dec-2017)

No changes since 2.8.10

2.8.10 (24-Aug-2017)

#19: Multimap serializer produces wrong Schema structure

2.8.9 (12-Jun-2017)

No changes since 2.8.8

2.8.8 (05-Apr-2017)

#12: `Range` deserialization fails when `ObjectMapper` has default typing enabled
 (reported by kringol@github)

2.8.7 (21-Feb-2017)
2.8.6 (12-Jan-2017)

No changes since 2.8.5

2.8.5 (14-Nov-2016)

- Further fix for #6 (see below): problem with `Multimap`,
  `DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY`
 (contributed by CookieAroundTheBend@github)

2.8.4 (14-Oct-2016)
2.8.3 (17-Sep-2016)
2.8.2 (30-Aug-2016)
2.8.1 (20-Jul-2016)

No changes since 2.8.0.

2.8.0 (04-Jul-2016)

#6: `Multimap` deserializer does not honor DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY
 (contributed by Lokesh N)

2.7.8 (26-Sep-2016)
2.7.7 (27-Aug-2016)
2.7.6 (23-Jul-2016)
2.7.5 (11-Jun-2016)

No changes since 2.7.5

2.7.4 (29-Apr-2016)

(Guava) #4: Fix GuavaOptionalSerializer.isEmpty()
 (fixed by Nate B)

2.7.3 (16-Mar-2016)
2.7.2 (27-Feb-2016)

No changes since 2.7.1

2.7.1 (02-Feb-2016)

First release from multi-module project; no functional changes since 2.7.0


