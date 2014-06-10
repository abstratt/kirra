package com.abstratt.kirra;

import java.util.Collections;
import java.util.List;

import com.abstratt.kirra.TypeRef.TypeKind;


public class Namespace extends NamedElement<NameScope> implements NameScope {
	private static final long serialVersionUID = 1L;
	protected List<Entity> entities = Collections.emptyList();
	protected List<Service> services = Collections.emptyList();
	protected List<TupleType> tupleTypes = Collections.emptyList();
	
	protected String timestamp;

	public List<Entity> getEntities() {
		return entities;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
		for (Entity entity : entities)
			entity.setNamespace(this.name);
	}

	public Namespace(String namespaceName) {
		this.name = namespaceName;
	}

	public Entity findEntity(String name) {
		return findElement(entities, name);
	}
	
	public Service findService(String name) {
		return findElement(services, name);
	}
	
	public TupleType findTupleType(String name) {
		return findElement(tupleTypes, name);
	}
	
	public <NE extends NamedElement<?>> NE findElement(List<NE> elements, String name) {
		for (NE element : elements)
			if (element.getName().equals(name))
				return element;
		return null;
	}
	
	@Override
	public TypeRef getTypeRef() {
		return new TypeRef(name, TypeKind.Namespace);
	}
	@Override
	public TypeKind getTypeKind() {
		return TypeKind.Namespace;
	}

	public List<Service> getServices() {
		return services;
	}
	
	public void setServices(List<Service> services) {
		this.services = services;
		for (Service service : services)
			service.setNamespace(this.name);
	}

	public List<TupleType> getTupleTypes() {
		return tupleTypes;
	}
	
	public void setTupleTypes(List<TupleType> tupleTypes) {
		this.tupleTypes = tupleTypes;
		for (TupleType tupleType : tupleTypes)
			tupleType.setNamespace(this.name);
	}

}
