package com.abstratt.kirra;

public abstract class TopLevelElement extends NamedElement<Namespace> implements NameScope {
    private static final long serialVersionUID = 1L;

    protected String namespace;

    public String getNamespace() {
        return namespace;
    }

    @Override
    public final TypeRef getTypeRef() {
        return new TypeRef(this.namespace, this.name, getTypeKind());
    }

    public boolean isA(TypeRef anotherType) {
        if (anotherType.getNamespace().equals(this.namespace) && anotherType.getTypeName().equals(this.name))
            return this.getTypeKind() == anotherType.getKind();
        return false;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
