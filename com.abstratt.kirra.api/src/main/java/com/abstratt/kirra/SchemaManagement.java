package com.abstratt.kirra;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides access to various pieces of information about a schema.
 */
public interface SchemaManagement {
    /**
     * The name of the application.
     */
    public String getApplicationName();
    /**
     * The human-friendly title of the application. 
     */
    public default String getApplicationLabel() {
    	return getApplicationName();
    }
    /**
     * The logo of the application, as a base64-encoded string.
     */
    public default String getApplicationLogo() {
    	return null;
    }

    /**
     * The current build of the application.
     */
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
     * Returns the entities in the application that represent roles.
     */
    public default List<Entity> getRoleEntities(String namespace) {
    	return getEntities(namespace).stream().filter(e -> e.isRole()).collect(Collectors.toList());
    }
    
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
    
    default <S extends DataScope> S getDataScope(TypeRef typeRef) {
        switch (typeRef.kind) {
        case Entity:
            return (S) getEntity(typeRef);
        case Tuple:
            return (S) getTupleType(typeRef);
        default:
            throw new IllegalArgumentException("" + typeRef);
        }
    }
    
    /**
     * Returns all entities that represent roles.
     */
    public default Collection<Entity> getRoleEntities() {
    	return getEntityNames().stream().map(t -> getEntity(t)).filter(it -> it.isRole()).collect(Collectors.toList());
    }

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

    /**
     * Returns the namespace with the given name.
     */
    public Namespace getNamespace(String namespaceName);

    public List<String> getNamespaces();

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
	public default List<TypeRef> getAllEntityRefs() {
		return getAllEntities().stream().map(e -> e.getTypeRef()).collect(Collectors.toList());
	}
}
