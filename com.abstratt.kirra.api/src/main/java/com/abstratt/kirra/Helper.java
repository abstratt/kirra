package com.abstratt.kirra;

/**
 * Do not use - will be removed.
 */
@Deprecated
//TODO-RC remove this class and any downstream usage
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
