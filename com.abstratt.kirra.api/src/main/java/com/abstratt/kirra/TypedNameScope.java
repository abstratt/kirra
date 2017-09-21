package com.abstratt.kirra;

import com.abstratt.kirra.TypeRef.TypeKind;

public interface TypedNameScope extends NameScope {
    TypeKind getTypeKind();

    TypeRef getTypeRef();
}
