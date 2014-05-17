package com.abstratt.kirra.rest.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

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

public class SchemaManagementOnREST implements SchemaManagement {

	/**
	 * The application's base URI.
	 */
	private URI baseUri;
	private RestClient restClient;

	public SchemaManagementOnREST(URI baseUri) {
		this.baseUri = baseUri;
		this.restClient = new RestClient();
	}

	@Override
	public List<Entity> getEntities(String namespace) {
		URI namespaceUri = URI.create(baseUri.toASCIIString() + namespace + "/");
		GetMethod getEntities = new GetMethod(namespaceUri.resolve("entities").toString());
		ArrayNode entityListNodes = (ArrayNode) restClient.executeMethod(getEntities);
        List<Entity> entities = new ArrayList<Entity>(entityListNodes.size());
        for (int i = 0; i < entityListNodes.size(); i++)
        	entities.add(buildEntity((ObjectNode) entityListNodes.get(i)));
		return entities;
	}

	private Entity buildEntity(ObjectNode objectNode) {
		Entity entity = new Entity();
		entity.setNamespace(objectNode.get("namespace").asText());
		entity.setName(objectNode.get("name").asText());
		entity.setLabel(objectNode.get("label").asText());
		entity.setDescription(objectNode.get("description").asText());
		return entity;
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
	public List<Relationship> getEntityRelationships(String namespace, String name) {
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