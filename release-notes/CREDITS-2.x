Here are people who have contributed to the development of Jackson JSON processor
Std Collections
(version numbers in brackets indicate release in which the problem was fixed)

Tatu Saloranta (tatu.saloranta@iki.fi): author for HPPC, Pcollections modules;
   co-author for Guava, Eclipse collections
Steven Schlansker (steven@nesscomputing.com): author of Guava module
Jonas Konrad (yawkat@github): author of Eclipse Collections module

--------------------------------------------------------------------------------
Credits for individual projects, before 2.7.1
--------------------------------------------------------------------------------

Stephan Schroevers: (Stephan202@github)
  * Contributed #56: Add support `HashCode`
   (2.5.0)
  * Contributed #65: Add deserialization support for SortedMultiset and ImmutableSortedMultiset
   (2.5.3)

Michael Hixson:
  * Contributed fix for #67: Support deserializing ImmutableSetMultimaps
   (2.6.0)

Alexey Kobyakov (akobiakov@github)
  * Reported #64: `@JsonUnwrapped` annotation is ignored when a field is an Optional
   (2.6.0)

Jose Thomas (josethomas@github)
  * Contributed #52: Guava collection types do not allow null values
   (2.6.2)

Jonathan Rodrigues de Oliveira (jorool@github)
  * Contributed #79: New configuration for Guava Range default bound type.
   (2.7.0)

Micahel Jameson (mjameson-se@github)
  * Reported #87: OSGi import missing for `com.fasterxml.jackson.annotation.JsonInclude$Value`
   (2.7.1)

--------------------------------------------------------------------------------
Fixes for `jackson-datatypes-collections` (versions 2.7.3+)
--------------------------------------------------------------------------------

Nate Bauernfeind (nbauernfeind@github)

  * Fixed #4:Fix GuavaOptionalSerializer.isEmpty()
   (2.7.4)

Lokesh Kumar N (LokeshN@github)
  * Fixed #6: `Multimap` deserializer does not honor
    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY
   (2.8.0)

Neelav Rana (neelav@github)
  * Reported #27: Null value handling not supported for `Optional` within `Multimap`
   (2.9.5)

Jonas Konrad (Eclipse collections)
  * Contributed #29: Add eclipse-collections support
   (2.9.6)

Felix König (Felk@github)
  * Contributed #50: (guava) Add Serializer and Deserializer for `RangeSet`
   (2.10.0)

Jon Freedman (jonfreedman@github)
  * Reported #53: (guava) `GuavaImmutableCollectionDeserializer` cannot deserialize
   an empty `Optional` from null
   (2.10.0)

Philip Leonard (philleonard@github)
  * Reported #56, contributed fix: Range property name (de)serialisation doesn't
    respect property naming strategy
   (2.10.0)

Tommy Becker (twbecker@github)
  * Reported #25: (guava) SetMultimap should be deserialized to a LinkedHashMultimap by default
   (2.12.0)

Migwel Gonzalez (Migwel@github)
  * Contributed fix for #25: (guava) SetMultimap should be deserialized to a LinkedHashMultimap
    by default
   (2.12.0)

Andrey Borisov (turbospaces@github)
  * Requested #102: (guava) accept lowerCase enums for `Range` `BoundType`
    serialization
   (2.15.0)

Marcin Wisnicki (mwisnick@github)
  * Reported #92: (guava) `@JsonDeserialize.contentConverter` does not work for non-builtin
    collections
   (2.15.0)

Joo Hyuk Kim (JooHyukKim@github)
  * Contributed #7: (guava) Add support for `WRITE_SORTED_MAP_ENTRIES` for
    Guava `Multimap`s
   (2.15.0)
  * Contributed fix for #92: (guava) `@JsonDeserialize.contentConverter` does not work
    for non-builtin collections
   (2.15.0)
  * Contributed fix for #102: (guava) accept lowerCase enums for `Range`
    `BoundType` serialization
   (2.15.0)
  * Contributed fix for #90: Cache Serialization serializes empty contents
   (2.16.0)

Wolff Bock von Wülfingen (wlfbck@github)
  * Reported #90: Cache Serialization serializes empty contents
   (2.16.0)
