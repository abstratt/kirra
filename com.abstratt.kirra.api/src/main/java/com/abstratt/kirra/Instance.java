package com.abstratt.kirra;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.abstratt.kirra.InstanceManagement.DataProfile;
import com.abstratt.kirra.TypeRef.TypeKind;

/**
 * Represents an object and its relationships.
 * <p>
 * Supports the idea of full vs. light instances (see {@link DataProfile}).
 * </p>
 */
public class Instance extends Tuple implements InstanceProtocol {
    private static final long serialVersionUID = 1L;
    /**
     * A map of disabled action names -> reasons.
     */
    protected Map<String, String> disabledActions = new HashMap<String, String>();
    protected DataProfile profile = DataProfile.Empty;
    protected Map<String, Instance> links = new LinkedHashMap<String, Instance>();
    protected String objectId;

    public Instance() {
    }

    public Instance(String namespace, String entity) {
        super(namespace, entity);
    }
    
    public Instance(String namespace, String entity, String objectId) {
        super(namespace, entity);
        this.objectId = objectId;
    }

    public Instance(TypeRef typeRef, String objectId) {
        super(typeRef);
        this.objectId = objectId;
    }

    public Map<String, String> getDisabledActions() {
        return disabledActions;
    }

    public String getEntityName() {
        return scopeName;
    }

    public String getEntityNamespace() {
        return scopeNamespace;
    }

    public Map<String, Instance> getLinks() {
        return links;
    }
    
    public String getObjectId() {
        return objectId;
    }

    public InstanceRef getReference() {
        return new InstanceRef(scopeNamespace, scopeName, objectId);
    }

    public Instance getRelated(String propertyName) {
        if (links == null)
            return null;
        return links.get(propertyName);
    }

    public Instance getSingleRelated(String reference) {
    	return getRelated(reference);
    }
    
    public boolean isInstanceOf(TypeRef type) {
        return getEntityName().equals(type.getTypeName()) && getEntityNamespace().equals(type.getEntityNamespace());
    }

    public boolean isNew() {
        return getObjectId() == null;
    }

    public void setDisabledActions(Map<String, String> disabledActions) {
        this.disabledActions = new HashMap<String, String>(disabledActions);
    }

    public void setEntityName(String entityName) {
        setScopeName(entityName);
    }

    public void setEntityNamespace(String namespace) {
        setScopeNamespace(namespace);
    }

    public void setLinks(Map<String, Instance> links) {
        this.links = new HashMap<String, Instance>(links);
    }
    
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setRelated(String propertyName, Instance toLink) {
        links.put(propertyName, toLink);
    }

    public void setSingleRelated(String propertyName, Instance toLink) {
        setRelated(propertyName, toLink);
    }

    @Override
    public String toString() {
        Map<String, Object> valuesToShow = getValues();
		Map<String, InstanceRef> linksToShow = links.entrySet()
			.stream()
			.collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().getReference()));
		return "Instance (" + getReference() + ") - values: " + valuesToShow + " - links: " + linksToShow;
    }

    @Override
    protected TypeKind getTypeKind() {
        return TypeKind.Entity;
    }
    
    public void setProfile(DataProfile profile) {
        this.profile = profile;
    }
    public DataProfile getProfile() {
        return profile;
    }
    public boolean isFull() {
        return profile == DataProfile.Full;
    }
}
