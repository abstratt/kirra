package com.abstratt.kirra;

import java.util.List;
import java.util.Map;

import com.abstratt.kirra.KirraException.Kind;
import com.abstratt.kirra.TypeRef.TypeKind;

public class InstanceManagementSnapshot implements InstanceManagement {
	
	private final Map<TypeRef, List<Instance>> instances;

	public InstanceManagementSnapshot(Map<TypeRef, List<Instance>> instances) {
		this.instances = instances;
	}
	
	@Override
	public List<Operation> getEnabledEntityActions(Entity entity) {
		return null;
	}

	@Override
	public Instance newInstance(String namespace, String name) {
		return null;
	}

	@Override
	public Instance createInstance(Instance instance) {
		return null;
	}

	@Override
	public void validateInstance(Instance toValidate) {
	}

	@Override
	public void deleteInstance(Instance instance) {
	}

	@Override
	public void linkInstances(Relationship relationship, String sourceId, String destinationId) {
	}

	@Override
	public void unlinkInstances(Relationship relationship, String sourceId, String destinationId) {
	}

	@Override
	public void deleteInstance(String namespace, String name, String id) {
	}

	@Override
	public Instance getInstance(String namespace, String name, String externalId, boolean full) {
		List<Instance> entityInstances = instances.get(new TypeRef(namespace, name, TypeKind.Entity));
		if (entityInstances == null)
			throw new KirraException("Unknown", Kind.ENTITY);
		for (Instance instance : entityInstances)
			if (instance.getObjectId().equals(externalId))
				return instance;
		return null;
	}

	@Override
	
	public List<Instance> getInstances(String namespace, String name, boolean full) {
		TypeRef typeRef = new TypeRef(namespace, name, TypeKind.Entity);
		List<Instance> entityInstances = instances.get(typeRef);
		if (entityInstances == null)
			throw new KirraException("Unknown Entity: " + typeRef, Kind.ENTITY);
		return entityInstances;
	}

	@Override
	public List<Instance> getRelatedInstances(String namespace, String name, String externalId, String relationship, boolean full) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Instance> getParameterDomain(Entity entity, String externalId, Operation action, Parameter parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Instance> getRelationshipDomain(Entity entity, String objectId, Relationship relationship) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instance updateInstance(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<?> executeOperation(Operation operation, String externalId, List<?> arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void zap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveContext() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Instance getCurrentUser() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
