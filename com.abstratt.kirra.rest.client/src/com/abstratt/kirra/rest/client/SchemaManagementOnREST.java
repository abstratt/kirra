package com.abstratt.kirra.rest.client;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Namespace;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.Schema;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.Service;
import com.abstratt.kirra.TupleType;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.json.EntityLinkJSONRepresentation;

public class SchemaManagementOnREST implements SchemaManagement {

    private RestHelper restHelper; 
	
	@Override
	public List<Entity> getEntities(String namespace) {
		Entity restHelper.getList("entities/" + namespace, EntityLinkJSONRepresentation.class);
	}

	@Override
	public List<Service> getServices(String namespace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TupleType> getTupleTypes(String namespace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getEntity(String namespace, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getEntity(TypeRef typeRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Service getService(String namespace, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Service getService(TypeRef typeRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TupleType getTupleType(String namespace, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TupleType getTupleType(TypeRef typeRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Operation> getEntityOperations(String namespace, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Property> getEntityProperties(String namespace, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Relationship> getEntityRelationships(String namespace,
			String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getNamespaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Namespace getNamespace(String namespaceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> getTopLevelEntities(String namespace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Relationship getOpposite(Relationship relationship) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<TypeRef> getEntityNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> getAllEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Service> getAllServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TupleType> getAllTupleTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
