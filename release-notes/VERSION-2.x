Project: jackson-modules-base
Modules:
  jackson-datatype-guava
  jackson-datatype-hppc
  jackson-datatype-pcollections
  jackson-datatype-eclipse-collections (since 2.9.6)

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.10.0 (not yet released)

#34: (guava) Allow use of Guava versions up to 25 (from 22)
 (suggested by Philipp H, AUTplayed@github)
#37: (eclipse-collections) Implement pair deserialization
 (contributed by Jonas K)

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

2.8.10 (not yet released)

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


