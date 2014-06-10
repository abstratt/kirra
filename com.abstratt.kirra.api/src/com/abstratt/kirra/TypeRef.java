package com.abstratt.kirra;

import java.io.Serializable;

public class TypeRef implements Serializable {
	public enum TypeKind {
		Entity, Enumeration, Primitive, Service, Tuple, Namespace
	}

	private static final long serialVersionUID = 1L;
	private static final String COLON_SEPARATOR = "::";
	private static final String SEPARATOR = ".";
	protected String entityNamespace;
	protected String typeName;
	protected TypeKind kind;

	public static String sanitize(String externalTypeName) {
		if (externalTypeName.indexOf(COLON_SEPARATOR) > 0)
			return externalTypeName.replace(COLON_SEPARATOR, SEPARATOR);
		return externalTypeName;
	}
	
	public TypeRef(String typeName, TypeKind kind) {
		typeName = sanitize(typeName);
		if (typeName.indexOf(SEPARATOR) > 0) {
			this.typeName = typeName
					.substring(typeName.lastIndexOf(SEPARATOR) + 1);
			this.entityNamespace = typeName.substring(0,
					typeName.lastIndexOf(SEPARATOR));
		} else {
			this.entityNamespace = "";
			this.typeName = typeName;
		}
		this.kind = kind;
	}

	protected void parseNameWithDot(String typeName) {
		this.typeName = typeName.substring(typeName.lastIndexOf(SEPARATOR) + 1);
		this.entityNamespace = typeName.substring(0,
				typeName.lastIndexOf(SEPARATOR));
	}

	protected void parseName(String typeName) {
		if (typeName.indexOf(SEPARATOR) > 0) {
			parseNameWithDot(typeName);
		} else
			this.typeName = typeName;

	}

	public TypeRef() {
	}
	
	public TypeRef(String namespace, String name, TypeKind kind) {
		this.typeName = name;
		this.entityNamespace = namespace;
		this.kind = kind;
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

	public TypeKind getKind() {
		return kind;
	}

	@Override
	public String toString() {
		return toString(entityNamespace, typeName);
	}

	public static String toString(String entityNamespace, String typeName) {
		return entityNamespace == null ? typeName : (entityNamespace
				+ SEPARATOR + typeName);
	}

	public String getEntityNamespace() {
		return entityNamespace;
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
		result = prime * result
				+ ((typeName == null) ? 0 : typeName.hashCode());
		result = prime * result
				+ ((entityNamespace == null) ? 0 : entityNamespace.hashCode());
		return result;
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

	public String getFullName() {
		return toString(entityNamespace, typeName);
	}
}
