package com.abstratt.kirra;

import java.util.Collection;
import java.util.List;

import com.abstratt.kirra.KirraException.Kind;

public abstract class TypedElement<O extends NameScope> extends SubElement<O> {
    public static <T extends TypedElement<?>> T findElement(Collection<T> elements, String name, boolean mustFind) {
        for (T e : elements)
            if (e.getName().equals(name))
                return e;
        if (mustFind)
            throw new KirraException("No element found named " + name, null, Kind.SCHEMA);
        return null;
    }

    private static final long serialVersionUID = 1L;
    protected boolean defaultValue;

    protected List<String> enumerationLiterals;
    protected boolean multiple;
    protected boolean required;

    protected TypeRef typeRef;

    protected TypedElement() {

    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        TypedElement<?> other = (TypedElement<?>) obj;
        if (typeRef == null) {
            if (other.typeRef != null)
                return false;
        } else if (!typeRef.equals(other.typeRef))
            return false;
        return true;
    }

    /**
     * In the case this typed element is typed by an enumeration, these are the
     * enumeration values.
     */
    public List<String> getEnumerationLiterals() {
        return enumerationLiterals;
    }

    public String getType() {
        return this.typeRef != null ? this.typeRef.getTypeName() : null;
    }

    public TypeRef getTypeRef() {
        return typeRef;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (typeRef == null ? 0 : typeRef.getTypeName().hashCode());
        return result;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public boolean isRequired() {
        return required;
    }

    public void setDefaulValue(boolean defaulting) {
        this.defaultValue = defaulting;
    }

    public void setEnumerationLiterals(List<String> enumerationLiterals) {
        this.enumerationLiterals = enumerationLiterals;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setTypeRef(TypeRef typeRef) {
        this.typeRef = typeRef;
    }
}
