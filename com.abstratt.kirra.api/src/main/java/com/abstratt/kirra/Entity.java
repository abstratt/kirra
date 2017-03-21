package com.abstratt.kirra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.abstratt.kirra.TypeRef.TypeKind;

public class Entity extends TopLevelElement implements BehaviorScope, DataScope {
    private static final long serialVersionUID = 1L;
    protected boolean concrete;
    protected boolean instantiable;
    protected boolean standalone;
    protected boolean topLevel;
    protected boolean role;
    protected boolean user;
    protected String mnemonicSlot;
    protected Collection<TypeRef> superTypes;
    protected Collection<TypeRef> subTypes;
    /**
     * A map of disabled action names -> reasons.
     */
    protected Map<String, String> disabledActions;
    protected Map<String, Operation> operations;
    protected Map<String, Property> properties;
    protected Map<String, Relationship> relationships;
    protected List<String> orderedDataElements;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Entity other = (Entity) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        if (operations == null) {
            if (other.operations != null)
                return false;
        } else if (!operations.equals(other.operations))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (topLevel != other.topLevel)
            return false;
        return true;
    }

    public Map<String, String> getDisabledActions() {
        return disabledActions;
    }

    public String getEntityNamespace() {
        return getNamespace();
    }

    public Operation getOperation(String name) {
        return operations.get(name);
    }

    @Override
    public List<Operation> getOperations() {
        return operations == null ? null : new ArrayList<Operation>(operations.values());
    }

    public List<Property> getProperties() {
        return properties == null ? null : new ArrayList<Property>(properties.values());
    }

    public Property getProperty(String name) {
        return properties.get(name);
    }

    public Relationship getRelationship(String name) {
        return relationships.get(name);
    }

    public List<Relationship> getRelationships() {
        return relationships == null ? null : new ArrayList<Relationship>(relationships.values());
    }

    public Collection<TypeRef> getSuperTypes() {
        return superTypes;
    }

    @Override
    public TypeKind getTypeKind() {
        return TypeKind.Entity;
    }
    
    public String getMnemonicSlot() {
		return mnemonicSlot;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (namespace == null ? 0 : namespace.hashCode());
        result = prime * result + (operations == null ? 0 : operations.hashCode());
        result = prime * result + (properties == null ? 0 : properties.hashCode());
        result = prime * result + (topLevel ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean isA(TypeRef anotherType) {
        return super.isA(anotherType) || superTypes != null && superTypes.contains(anotherType);
    }

    public boolean isConcrete() {
        return concrete;
    }

    /**
     * Returns whether this entity is standalone. An entity is standalone if it
     * has no required container references.
     * 
     * @return
     */
    public boolean isStandalone() {
        return standalone;
    }

    /**
     * Returns whether this entity is top-level. An entity is top-level if:
     * <ul>
     * <li>it is standalone</li>
     * <li>or is explicitly annotated as top-level or at least one super type is
     * a top-level class</li>
     * </ul>
     * 
     * @param toTry
     * @return <code>true</code> if the given class corresponds to a top-level
     *         entity, <code>false</code> otherwise
     */
    public boolean isTopLevel() {
        return topLevel && concrete;
    }

    public void setConcrete(boolean concrete) {
        this.concrete = concrete;
    }

    public void setDisabledActions(Map<String, String> disabledActions) {
        this.disabledActions = new HashMap<String, String>(disabledActions);
    }

    public void setOperations(List<Operation> operations) {
        this.operations = new LinkedHashMap<String, Operation>();
        for (Operation operation : operations) {
        	if (this.operations.containsKey(operation.getName()))
        		// stick to the first one found
        		continue;
            operation.setOwner(this);
            this.operations.put(operation.getName(), operation);
        }
    }

    public void setProperties(List<Property> properties) {
        this.properties = new LinkedHashMap<String, Property>();
        for (Property property : properties) {
        	if (this.properties.containsKey(property.getName()))
        		// stick to the first one found
        		continue;
            property.setOwner(this);
            this.properties.put(property.getName(), property);
        }
    }

    public void setRelationships(List<Relationship> entityRelationships) {
        this.relationships = new LinkedHashMap<String, Relationship>();
        for (Relationship relationship : entityRelationships) {
        	if (relationships.containsKey(relationship.getName()))
        		// stick to the first one found
        		continue;
            relationship.setOwner(this);
            this.relationships.put(relationship.getName(), relationship);
        }
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    public void setSuperTypes(Collection<TypeRef> superTypes) {
        this.superTypes = superTypes;
    }
    
    public void setSubTypes(Collection<TypeRef> subTypes) {
        this.subTypes = subTypes;
    }

    public void setTopLevel(boolean topLevel) {
        this.topLevel = topLevel;
    }


    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }
    
    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }
    
    public void setMnemonicSlot(String mnemonicSlot) {
		this.mnemonicSlot = mnemonicSlot;
	}

    public void setInstantiable(boolean instantiable) {
        this.instantiable = instantiable;
    }
    
    public boolean isInstantiable() {
        return instantiable;
    }
    
    public List<String> getOrderedDataElements() {
		return orderedDataElements;
	}
    public void setOrderedDataElements(List<String> orderedDataElements) {
		this.orderedDataElements = orderedDataElements;
	}
}
