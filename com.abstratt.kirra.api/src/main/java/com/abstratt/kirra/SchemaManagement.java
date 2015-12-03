package com.abstratt.kirra;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface SchemaManagement {
    public String getApplicationName();

    public String getBuild();

    /**
     * Returns all entities in the namespace, or across all namespaces.
     * 
     * @param namespace
     *            the namespace, or <code>null</code> for all
     * @return the list of entities found
     */
    public List<Entity> getEntities(String namespace);
    
    /**
     * Returns all entities that have the give base entity as their super entity.
     * @param baseEntity
     * @return
     */
    public default List<Entity> getSubEntities(TypeRef baseEntity) {
    	return getEntities(null).stream().filter(e -> e.getSuperTypes().contains(baseEntity)).collect(Collectors.toList());
    }

    /**
     * Returns the entity with the given name. Returns null a corresponding
     * class does not exist, or if it is not an entity.
     * 
     * @param namespace
     * @param name
     * @return the corresponding entity, or <code>null</code>
     */
    public Entity getEntity(String namespace, String name);

    public Entity getEntity(TypeRef typeRef);

    public Collection<TypeRef> getEntityNames();

    /**
     * Returns the operations available for the given entity type.
     */
    public List<Operation> getEntityOperations(String namespace, String name);

    /**
     * Returns the properties available for the given entity type.
     */
    public List<Property> getEntityProperties(String namespace, String name);

    /**
     * Returns the relationships available for the given entity type.
     */
    public List<Relationship> getEntityRelationships(String namespace, String name);

    public Namespace getNamespace(String namespaceName);

    public List<String> getNamespaces();

    // ENTITY DATA API

    /**
     * Returns the entire schema at once.
     */
    public Schema getSchema();

    public Service getService(String namespace, String name);

    public Service getService(TypeRef typeRef);

    public List<Service> getServices(String namespace);

    public List<Entity> getTopLevelEntities(String namespace);

    public TupleType getTupleType(String namespace, String name);

    public TupleType getTupleType(TypeRef typeRef);

    public List<TupleType> getTupleTypes(String namespace);

    List<Entity> getAllEntities();

    List<Service> getAllServices();

    List<TupleType> getAllTupleTypes();

    Relationship getOpposite(Relationship relationship);
}
