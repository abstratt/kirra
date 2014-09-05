package com.abstratt.kirra;

import java.util.List;
import java.util.Map;

/**
 * Basic protocol for managing entity instances.
 */
public interface InstanceManagement {

    /**
     * Persists a transient instance. It is an error to try to invoke create on
     * an already persisted instance.
     * 
     * @param namespace
     * @param entityName
     */
    public Instance createInstance(Instance instance);

    /**
     * Destroys an instance, persisted or not.
     * 
     * @param instance
     */
    public void deleteInstance(Instance instance);

    /**
     * Destroys an instance, persisted or not.
     * 
     * @param namespace
     *            entity namespace
     * @param name
     *            entity name
     * @param id
     *            instance identifier
     */
    public void deleteInstance(String namespace, String name, String id);

    /**
     * Returns a collection of instances or values, never null.
     */
    public List<?> executeOperation(Operation operation, String externalId, List<?> arguments);

    public Instance getCurrentUser();

    public List<Operation> getEnabledEntityActions(Entity entity);

    /**
     * Loads an instance with the given identifier.
     * 
     * @param namespace
     * @param name
     * @param externalId
     *            the object identifier
     * @param full
     *            whether the instance should be fully loaded
     * @return an instance, or <code>null</code> if no object exists
     * @see Instance#isFull()
     */
    public Instance getInstance(String namespace, String name, String externalId, boolean full);

    /**
     * Loads all instances of the given entity type.
     * 
     * @param namespace
     * @param name
     * @param full
     *            whether instances should be fully loaded
     * @return
     * @see Instance#isFull()
     */
    public List<Instance> getInstances(String namespace, String name, boolean full);

    public List<Instance> filterInstances(Map<String, List<Object>> criteria, String namespace, String name, boolean full);

    public List<Instance> getParameterDomain(Entity entity, String externalId, Operation action, Parameter parameter);

    public List<Instance> getRelatedInstances(String namespace, String name, String externalId, String relationship, boolean full);

    public List<Instance> getRelationshipDomain(Entity entity, String objectId, Relationship relationship);

    /**
     * Establishes a link between the two instances through the given
     * relationship.
     * 
     * @param relationship
     * @param sourceId
     * @param destinationId
     */
    public void linkInstances(Relationship relationship, String sourceId, String destinationId);

    /**
     * Creates a transient instance of the given type. Default values will have
     * been properly assigned. Transient instances have identities just like
     * persisted instances. However, they are discarded at the end of a session
     * if not saved. Also, transient instances are not visible as part of the
     * entity extent (see {@link #getInstances(String, String, boolean)}).
     * 
     * @param namespace
     *            entity namespace
     * @param name
     *            entity name
     * @return the created instance, with default values assigned
     */
    public Instance newInstance(String namespace, String name);

    public void saveContext();

    /**
     * Breaks an existing link between two instances through the given
     * relationship.
     * 
     * @param relationship
     * @param sourceId
     * @param destinationId
     */
    public void unlinkInstances(Relationship relationship, String sourceId, String destinationId);

    /**
     * Updates a persistent instance.
     * 
     * @param instance
     * @return
     */
    public Instance updateInstance(Instance instance);

    /**
     * Validates the given instance without saving it.
     * 
     * @param toValidate
     *            instance to be validated
     * @throws KirraException
     *             if validation fails
     */
    public void validateInstance(Instance toValidate);

    public void zap();
}