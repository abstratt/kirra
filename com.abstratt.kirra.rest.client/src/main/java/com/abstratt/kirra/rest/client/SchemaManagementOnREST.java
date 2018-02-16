package com.abstratt.kirra.rest.client;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.reflect.TypeToken;

public class SchemaManagementOnREST implements SchemaManagement {

    /**
     * The application's base URI.
     */
    private URI baseUri;
    private RestClient restClient;

    public SchemaManagementOnREST(URI baseUri) {
    	this(new RestClient(), baseUri);
    }
    
    public SchemaManagementOnREST(RestClient restClient, URI baseUri) {
        this.baseUri = baseUri;
        this.restClient = restClient;
    }


    @Override
    public List<Entity> getAllEntities() {
        return get(new TypeToken<List<Entity>>() {
        }.getType(), Paths.ENTITIES);
    }

    @Override
    public List<Service> getAllServices() {
        return get(new TypeToken<List<Service>>() {
        }.getType(), Paths.SERVICES);
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

    @Override
    public List<Entity> getEntities(String namespace) {
        List<Entity> allEntities = getAllEntities();
        List<Entity> namespaceEntities = new ArrayList<Entity>();
        for (Entity entity : allEntities)
            if (entity.getNamespace().equals(namespace))
                namespaceEntities.add(entity);
        return namespaceEntities;
    }

    @Override
    public Entity getEntity(String namespace, String name) {
        return getEntity(new TypeRef(namespace, name, TypeKind.Entity));
    }

    @Override
    public Entity getEntity(TypeRef typeRef) {
        return get(Entity.class, Paths.ENTITIES, typeRef.getFullName());
    }

    @Override
    public Collection<TypeRef> getEntityNames() {
        Collection<TypeRef> entityNames = new LinkedHashSet<TypeRef>();
        for (Entity entity : getAllEntities())
            entityNames.add(entity.getTypeRef());
        return entityNames;
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
    public Namespace getNamespace(String namespaceName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getNamespaces() {
        List<Entity> allEntities = getAllEntities();
        Set<String> namespaces = new LinkedHashSet<String>();
        for (Entity entity : allEntities)
            namespaces.add(entity.getEntityNamespace());
        return new ArrayList<String>(namespaces);
    }

    @Override
    public Relationship getOpposite(Relationship relationship) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Schema getSchema() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Service getService(String namespace, String name) {
        return getService(new TypeRef(namespace, name, TypeKind.Service));
    }

    @Override
    public Service getService(TypeRef typeRef) {
        return get(Service.class, Paths.SERVICES, typeRef.getFullName());
    }

    @Override
    public List<Service> getServices(String namespace) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Entity> getTopLevelEntities(String namespace) {
        List<Entity> topLevel = new ArrayList<Entity>();
        for (Entity entity : getAllEntities())
            topLevel.add(entity);
        return topLevel;

    }

    @Override
    public TupleType getTupleType(String namespace, String name) {
        return getTupleType(new TypeRef(namespace, name, TypeKind.Tuple));
    }

    @Override
    public TupleType getTupleType(TypeRef typeRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TupleType> getTupleTypes(String namespace) {
        throw new UnsupportedOperationException();
    }

    private <T> T get(Type type, String... segments) {
        return restClient.get(baseUri, type, segments);
    }

    private Map<String, Object> getIndex() {
        return (Map<String, Object>) get(new TypeToken<Map<String, Object>>() {
        }.getType());
    }

}