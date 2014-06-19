package com.abstratt.kirra;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.abstratt.kirra.TypeRef.TypeKind;

/**
 * Represents structured information. No identity, no behavior, and no
 * relationships, other than nested records.
 *
 * No metadata?
 */
public class Tuple implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String scopeName;
    protected String scopeNamespace;
    protected String shorthand;
    protected Map<String, Object> values = new LinkedHashMap<String, Object>();

    public Tuple() {
    }

    public Tuple(TypeRef typeRef) {
        this.scopeName = typeRef.getTypeName();
        this.scopeNamespace = typeRef.getEntityNamespace();
    }

    public String getScopeName() {
        return scopeName;
    }

    public String getScopeNamespace() {
        return scopeNamespace;
    }

    public String getShorthand() {
        return shorthand;
    }

    public TypeRef getTypeRef() {
        return new TypeRef(scopeNamespace, scopeName, getTypeKind());
    }

    public Object getValue(String propertyName) {
        return values == null ? null : values.get(propertyName);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public boolean hasValueFor(String propertyName) {
        return this.values.containsKey(propertyName);
    }

    public void setShorthand(String shorthand) {
        this.shorthand = shorthand;
    }

    public void setValue(String propertyName, Object value) {
        values.put(propertyName, value);
    }

    public void setValues(Map<String, Object> values) {
        this.values = new LinkedHashMap<String, Object>(values);
    }

    @Override
    public String toString() {
        return "" + values;
    }

    protected TypeKind getTypeKind() {
        return TypeKind.Tuple;
    }

}
