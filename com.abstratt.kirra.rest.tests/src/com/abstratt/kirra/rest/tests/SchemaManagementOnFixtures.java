package com.abstratt.kirra.rest.tests;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

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
		return loadFixture(Entity.class, Paths.ENTITIES, typeRef.getFullName());
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
		return loadFixture(new TypeToken<List<Entity>>() {}.getType(), Paths.ENTITIES);
	}

	private <T> T loadFixture(Type type, String... segments) {
		String resourcePath = "/fixtures/" + StringUtils.join(segments, "/") + ".json";
		try (InputStream contents = getClass().getResourceAsStream(resourcePath)) {
			Assert.assertNotNull(contents);
			return new Gson().fromJson(new InputStreamReader(contents), type);
		} catch (IOException e) {
			throw new KirraException("Unexpected", e, Kind.SCHEMA);
		}
	}

	@Override
	public List<Service> getAllServices() {
		return loadFixture(new TypeToken<List<Service>>() {}.getType(), Paths.SERVICES); 
	}

	@Override
	public List<TupleType> getAllTupleTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getApplicationName() {
		return getIndex().get("applicationName").toString();
	}

	@Override
	public String getBuild() {
		return getIndex().get("build").toString();
	}

	private Map<String, Object> getIndex() {
		return (Map<String, Object>) loadFixture(new TypeToken<Map<String, Object>>() {}.getType(), "index.json");
	}
}

