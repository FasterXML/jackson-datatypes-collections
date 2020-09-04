Project: jackson-datatypes-collections
Modules:
  jackson-datatype-guava
  jackson-datatype-hppc
  jackson-datatype-pcollections
  jackson-datatype-eclipse-collections (since 2.10)

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.11.3 (not yet released)


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


