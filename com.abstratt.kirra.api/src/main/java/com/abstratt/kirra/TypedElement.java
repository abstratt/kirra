package com.abstratt.kirra;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.abstratt.kirra.KirraException.Kind;

public abstract class TypedElement<O extends NameScope> extends SubElement<O> {
    public static <T extends TypedElement<?>> T findElement(Collection<T> elements, String name, boolean mustFind) {
        if (mustFind)
            return NameScope.tryToFind(elements, name).orElseThrow(() -> new KirraException("No element found named " + name, null, Kind.SCHEMA));
        return NameScope.find(elements, name);
    }

    private static final long serialVersionUID = 1L;
    protected boolean hasDefault;

    protected Map<String, String> enumerationLiterals;
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
    public Map<String, String> getEnumerationLiterals() {
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

    public boolean isHasDefault() {
        return hasDefault;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public boolean isRequired() {
        return required;
    }

    public void setHasDefault(boolean defaulting) {
        this.hasDefault = defaulting;
    }

    public void setEnumerationLiterals(Map<String, String> enumerationLiterals) {
        this.enumerationLiterals = enumerationLiterals;
    }
    
    public void setEnumerationLiterals(List<String> enumerationLiterals) {
        this.enumerationLiterals = enumerationLiterals == null ? null : enumerationLiterals.stream().collect(Collectors.toMap(it -> it, it -> it));
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
