package com.abstratt.kirra;

import java.util.ArrayList;
import java.util.List;

/**
 * The schema for an application. Provides access to application basic metadata, and its namespaces.
 */
public class Schema {
    protected String applicationName;
    protected String applicationLabel;
    protected String applicationLogo;

	protected String build;
    protected List<Namespace> namespaces;
	private Entity userEntity;

	public Namespace findNamespace(String name) {
        for (Namespace namespace : namespaces)
            if (namespace.getName().equals(name))
                return namespace;
        return null;
    }

    public List<Entity> getAllEntities() {
        List<Entity> allEntities = new ArrayList<Entity>();
        for (Namespace namespace : namespaces)
            allEntities.addAll(namespace.getEntities());
        return allEntities;
    }

    public List<Service> getAllServices() {
        List<Service> allServices = new ArrayList<Service>();
        for (Namespace namespace : namespaces)
            allServices.addAll(namespace.getServices());
        return allServices;
    }

    public List<TupleType> getAllTupleTypes() {
        List<TupleType> allTupleTypes = new ArrayList<TupleType>();
        for (Namespace namespace : namespaces)
            allTupleTypes.addAll(namespace.getTupleTypes());
        return allTupleTypes;
    }

    public String getApplicationName() {
        return applicationName;
    }
    
    public String getApplicationLabel() {
        return applicationLabel;
    }
    public void setApplicationLabel(String applicationLabel) {
		this.applicationLabel = applicationLabel;
	}
    
    public String getApplicationLogo() {
    	return applicationLogo;
    }
    
    public void setApplicationLogo(String applicationLogo) {
    	this.applicationLogo = applicationLogo;
    }

    public String getBuild() {
        return build;
    }

    public List<Namespace> getNamespaces() {
        return namespaces;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public void setNamespaces(List<Namespace> namespaces) {
        this.namespaces = namespaces;
    }

    /** Returns this application's entity that represents users. */
	public Entity getUserEntity() {
		return userEntity;
	}
	
	public void setUserEntity(Entity userEntity) {
		this.userEntity = userEntity;
	}
}
