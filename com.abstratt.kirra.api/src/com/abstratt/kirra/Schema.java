package com.abstratt.kirra;

import java.util.ArrayList;
import java.util.List;

public class Schema {
	private static final long serialVersionUID = 1L;
	private List<Namespace> namespaces;
	private String applicationName;
	private String build;

	public List<Namespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}
	
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

	public String getBuild() {
		return build;
	}
    public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
    
    public void setBuild(String build) {
		this.build = build;
	}
}
