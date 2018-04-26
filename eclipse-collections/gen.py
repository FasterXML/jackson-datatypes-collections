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

for k in key_types:
    print("    //region "+k+" -> Primitive")
    for v in value_types:
        print(template.replace("Ktype", k).replace("Vtype", v).replace("KTYPE", k.upper()).replace("VTYPE", v.upper()))
    print("    //endregion")

for k in key_types:
    for v in value_types:
        print("add("+k+v+"Map.class, TypeHandlerPair."+k.upper()+"_"+v.upper()+");")
        print("add(Mutable"+k+v+"Map.class, TypeHandlerPair."+k.upper()+"_"+v.upper()+");")
        print("add(Immutable"+k+v+"Map.class, TypeHandlerPair."+k.upper()+"_"+v.upper()+", "+k+v+"Map::toImmutable);")
