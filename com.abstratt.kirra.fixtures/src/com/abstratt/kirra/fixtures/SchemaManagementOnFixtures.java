package com.abstratt.kirra.fixtures;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.KirraException.Kind;
import com.abstratt.kirra.Namespace;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.Schema;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.Service;
import com.abstratt.kirra.TopLevelElement;
import com.abstratt.kirra.TupleType;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SchemaManagementOnFixtures implements SchemaManagement {

	@Override
	public List<Entity> getEntities(String namespace) {
		// TODO Auto-generated method stub
		return null;
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
		return getEntity(new TypeRef(namespace, name, TypeKind.Entity));
	}

	@Override
	public Entity getEntity(TypeRef typeRef) {
		String[] segments = { Paths.ENTITIES, typeRef.getFullName() };
		return FixtureHelper.loadFixture(Entity.class, segments);
	}

	@Override
	public Service getService(String namespace, String name) {
		return getService(new TypeRef(namespace, name, TypeKind.Service));
	}
	
	private <T extends TopLevelElement> T findByType(Stream<T> elements, TypeRef type) {
		return elements.filter(it -> it.isA(type)).findAny().orElseThrow(() -> new KirraException("Not found", Kind.SCHEMA));
	}

	@Override
	public Service getService(TypeRef typeRef) {
		return findByType(getAllServices().stream(), typeRef);
	}

	@Override
	public TupleType getTupleType(String namespace, String name) {
		return getTupleType(new TypeRef(namespace, name, TypeKind.Tuple));
	}

	@Override
	public TupleType getTupleType(TypeRef typeRef) {
		return findByType(getAllTupleTypes().stream(), typeRef);
	}

	@Override
	public List<Operation> getEntityOperations(String namespace, String name) {
		return getEntity(namespace, name).getOperations();
	}

	@Override
	public List<Property> getEntityProperties(String namespace, String name) {
		return getEntity(namespace, name).getProperties();
	}

	@Override
	public List<Relationship> getEntityRelationships(String namespace, String name) {
		return getEntity(namespace, name).getRelationships();
	}

	@Override
	public List<String> getNamespaces() {
		return getAllEntities().stream().map(e -> e.getName()).collect(toList());
	}

	@Override
	public Namespace getNamespace(String namespaceName) {
		return null;
	}

	@Override
	public List<Entity> getTopLevelEntities(String namespace) {
		return getAllEntities().stream().filter(Entity::isTopLevel).collect(toList());
	}

	@Override
	public Relationship getOpposite(Relationship relationship) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<TypeRef> getEntityNames() {
		return getAllEntities().stream().map(Entity::getTypeRef).collect(toList());
	}

	@Override
	public List<Entity> getAllEntities() {
		String[] segments = { Paths.ENTITIES };
		List<Entity> fixture = FixtureHelper.loadFixture(new TypeToken<List<Entity>>() {}.getType(), segments);
		if (fixture == null)
			return Arrays.<Entity>asList();
		return fixture;
	}

	@Override
	public List<Service> getAllServices() {
		String[] segments = { Paths.SERVICES };
		List<Service> fixture = FixtureHelper.loadFixture(new TypeToken<List<Service>>() {}.getType(), segments);
		if (fixture == null)
			return Arrays.<Service>asList();
		return fixture; 
	}

	@Override
	public List<TupleType> getAllTupleTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getApplicationName() {
		return (String) getIndex().get("applicationName");
	}

	@Override
	public String getBuild() {
		return (String) getIndex().get("build");
	}

	private Map<String, Object> getIndex() {
		String[] segments = { "index.json" };
		Map<String, Object> index = FixtureHelper.loadFixture(new TypeToken<Map<String, Object>>() {}.getType(), segments);
		return index == null ? Collections.emptyMap() : index;
	}
}

