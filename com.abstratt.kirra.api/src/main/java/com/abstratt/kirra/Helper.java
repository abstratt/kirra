package com.abstratt.kirra;

public class Helper {
    public static DataScope resolveDataScope(SchemaManagement schema, TypeRef typeRef) {
        switch (typeRef.kind) {
        case Entity:
            return schema.getEntity(typeRef);
        case Tuple:
            return schema.getTupleType(typeRef);
        default:
            throw new IllegalArgumentException("" + typeRef);
        }
    }
}
