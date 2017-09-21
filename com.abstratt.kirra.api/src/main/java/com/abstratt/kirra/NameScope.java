package com.abstratt.kirra;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.abstratt.kirra.TypeRef.TypeKind;

public interface NameScope {
    TypeKind getTypeKind();

    TypeRef getTypeRef();
    
    public static <N extends NamedElement<?>> N find(Collection<N> elements, String name) {
        return tryToFind(elements, name).orElse(null);
    }

    public static <N extends NamedElement<?>> Optional<N> tryToFind(Collection<N> elements, String name) {
        return elements.stream().filter(it -> it.getName().equals(name)).findAny();
    }
    
    public static <N extends NamedElement<?>> N find(Collection<N> elements, String name, Supplier<Throwable> missingHandler) {
        return tryToFind(elements, name).orElseThrow(() -> (KirraException) missingHandler.get());
    }
}
