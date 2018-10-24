#!/usr/bin/env python3

key_types = ["Byte", "Short", "Char", "Int", "Float", "Long", "Double"]
value_types = ["Boolean", "Byte", "Short", "Char", "Int", "Float", "Long", "Double"]

template = """
    TypeHandlerPair<MutableKtypeVtypeMap, PrimitiveKVHandler.Ktype, PrimitiveKVHandler.Vtype> KTYPE_VTYPE =
            new TypeHandlerPair<MutableKtypeVtypeMap, PrimitiveKVHandler.Ktype, PrimitiveKVHandler.Vtype>() {
                @Override
                public PrimitiveKVHandler.Ktype keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Ktype.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Vtype valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Vtype.INSTANCE;
                }

                @Override
                public MutableKtypeVtypeMap createEmpty() {
                    return KtypeVtypeMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableKtypeVtypeMap target,
                        PrimitiveKVHandler.Ktype kh, PrimitiveKVHandler.Vtype vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };"""

print()
print()
print("TypeHandlerPairs:")
for k in key_types:
    print("    //region "+k+" -> Primitive")
    for v in value_types:
        print(template.replace("Ktype", k).replace("Vtype", v).replace("KTYPE", k.upper()).replace("VTYPE", v.upper()))
    print("    //endregion")

print()
print()
print("TypeHandlerPairs registrations:")
for k in key_types:
    for v in value_types:
        print("add("+k+v+"Map.class, TypeHandlerPair."+k.upper()+"_"+v.upper()+");")
        print("add(Mutable"+k+v+"Map.class, TypeHandlerPair."+k.upper()+"_"+v.upper()+");")
        print("add(Immutable"+k+v+"Map.class, TypeHandlerPair."+k.upper()+"_"+v.upper()+", "+k+v+"Map::toImmutable);")

print()
print()
print("PrimitivePrimitiveMapSerializers:")
for k in key_types:
    print("    //region "+k+" -> Primitive")
    for v in value_types:
        if v == "Boolean":
            write = "writeBoolean(v)"
        elif v == "Char":
            write = "writeString(new char[]{v}, 0, 1)"
        else:
            write = "writeNumber(v)"
        tpl = """    public static final PrimitiveMapSerializer<KtypeVtypeMap> """+k.upper()+"_"+v.upper()+""" =
            new PrimitiveMapSerializer<KtypeVtypeMap>(KtypeVtypeMap.class) {
                @Override
                protected void serializeEntries(KtypeVtypeMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen."""+write+""";
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };"""
        print(tpl.replace("Ktype", k).replace("Vtype", v))
    print("    //endregion")
print("    static {")
for k in key_types:
    for v in value_types:
        print("        INSTANCES.put("+k+v+"Map.class, "+k.upper()+"_"+v.upper()+");")
print("    }")


print()
print()
print("PairInstantiators:")
for one in value_types:
    print("    //region "+one+" -> Primitive")
    for two in value_types:
        print("""purePrimitiveInstantiator(OneTTwoTPair.class, oneT.class, twoT.class,
                                  (one, two) -> PrimitiveTuples.pair((oneT) one, (twoT) two));"""
              .replace("OneT", one).replace("TwoT", two).replace("oneT", one.lower()).replace("twoT", two.lower()))
    print("    //endregion")