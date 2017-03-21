package com.abstratt.kirra;

import java.io.Serializable;

public class TypeRef implements Serializable {
    public enum TypeKind {
        Entity, Enumeration, Namespace, Primitive, Service, Tuple, Blob
    }

    public static String sanitize(String externalTypeName) {
        if (externalTypeName.indexOf(TypeRef.COLON_SEPARATOR) > 0)
            return externalTypeName.replace(TypeRef.COLON_SEPARATOR, TypeRef.SEPARATOR);
        return externalTypeName;
    }

    public static String toString(String entityNamespace, String typeName) {
        return entityNamespace == null ? typeName : entityNamespace + TypeRef.SEPARATOR + typeName;
    }
    
    private static String toString(String entityNamespace, String typeName, TypeKind kind) {
        return entityNamespace == null ? typeName : entityNamespace + TypeRef.SEPARATOR + typeName + (kind == null ? "" : ("("+ kind.toString() + ")"));
    }

    private static final String COLON_SEPARATOR = "::";
    private static final String SEPARATOR = ".";
    private static final long serialVersionUID = 1L;
    protected String entityNamespace;

    protected TypeKind kind;

    protected String typeName;

    public TypeRef() {
    }

    public TypeRef(String namespace, String name, TypeKind kind) {
        this.typeName = name;
        this.entityNamespace = namespace;
        this.kind = kind;
    }

    public TypeRef(String typeName, TypeKind kind) {
        typeName = TypeRef.sanitize(typeName);
        String[] components = splitName(typeName);
        this.entityNamespace = components[0];
        this.typeName = components[1];
        this.kind = kind;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeRef other = (TypeRef) obj;
        if (kind != other.kind)
            return false;
        if (typeName == null) {
            if (other.typeName != null)
                return false;
        } else if (!typeName.equals(other.typeName))
            return false;
        if (entityNamespace == null) {
            if (other.entityNamespace != null)
                return false;
        } else if (!entityNamespace.equals(other.entityNamespace))
            return false;
        return true;
    }

    public String getEntityNamespace() {
        return entityNamespace;
    }

    public String getFullName() {
        return TypeRef.toString(entityNamespace, typeName);
    }

    public TypeKind getKind() {
        return kind;
    }

    public String getNamespace() {
        return entityNamespace;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (typeName == null ? 0 : typeName.hashCode());
        result = prime * result + (entityNamespace == null ? 0 : entityNamespace.hashCode());
        return result;
    }

    public void setEntityNamespace(String entityNamespace) {
        this.entityNamespace = entityNamespace;
    }

    public void setKind(TypeKind kind) {
        this.kind = kind;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return TypeRef.toString(entityNamespace, typeName);
    }
    
    public static String[] splitName(String typeName) {
    	return splitName(typeName, null);
    }
    
    public static String[] splitName(String typeName, String defaultNamespace) {
        String[] result = {defaultNamespace, typeName};
        int separatorIndex = typeName.lastIndexOf(TypeRef.SEPARATOR);
        if (separatorIndex >= 0) {
            result[0] = typeName.substring(0, separatorIndex);
            result[1] = typeName.substring(separatorIndex + 1);
        }
        return result;
    }
}
