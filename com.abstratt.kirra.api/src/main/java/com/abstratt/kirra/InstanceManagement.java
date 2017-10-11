package com.abstratt.kirra;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Basic protocol for managing entity instances.
 */
public interface InstanceManagement {

    enum DataProfile {
        Full("Slim"), Slim ("Empty"), Empty ("Empty");
        
        private String lighter;
        DataProfile(String lighter) {
            this.lighter = lighter;
        }

        public static DataProfile from(boolean full) {
            return full ? Full : Slim;
        }
        public static DataProfile from(String name) {
            return Arrays.stream(DataProfile.values()).filter(it -> it.name().equalsIgnoreCase(name)).findAny().orElse(DataProfile.Full);
        }
        public DataProfile lighter() {
            return valueOf(lighter);
        }
    }
    
    public class PageRequest {


        public PageRequest(Long first, Integer maximum, DataProfile profile, boolean includeSubtypes) {
            this.first = first;
            this.maximum = maximum;
            this.dataProfile = profile;
            this.includeSubtypes = includeSubtypes;
        }
        public PageRequest() {
            this(0L, Integer.MAX_VALUE, DataProfile.Full, true);
        }
        Long first;
        Integer maximum;
        private Boolean includeSubtypes;
        private DataProfile dataProfile;
        
        public int getMaximum() {
            return (maximum == null || maximum <= 0) ? Integer.MAX_VALUE : maximum; 
        }
        public long getFirst() {
            return (first == null || first < 0) ? 0 : first;
        }
        public DataProfile getDataProfile() {
            return dataProfile;
        }
        public boolean getIncludeSubtypes() {
            return includeSubtypes == null || includeSubtypes;
        }
    }
    
    public class Page<T> {
        public final long offset;
        public final long length;
        public final List<T> contents;
        
        public Page(List<T> contents) {
            this.contents = contents;
            this.offset = 0;
            this.length = contents.size();
        }

        public Page(List<T> contents, long offset, long length) {
            this.contents = contents;
            this.offset = offset;
            this.length = length;
        }
    }

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
    public default List<?> executeOperation(Operation operation, String externalId, List<?> arguments, String parameterSet) {
        return executeOperation(operation, externalId, arguments);
    }
    
    public List<?> executeOperation(Operation operation, String externalId, List<?> arguments);

    public default List<?> executeQuery(Operation operation, String externalId, List<?> arguments, boolean full) {
    	return executeOperation(operation, externalId, arguments);
    }
    
    public default List<?> executeQuery(Operation operation, String externalId, List<?> arguments) {
    	return executeQuery(operation, externalId, arguments, true);
    }
    
    public default List<?> executeQuery(Operation operation, String externalId, List<?> arguments, PageRequest pageRequest) {
        return executeQuery(operation, externalId, arguments, pageRequest.getDataProfile() == DataProfile.Full);
    }
    
    public default long countQueryResults(Operation operation, String externalId, List<?> arguments) {
    	return executeQuery(operation, externalId, arguments, false).size();
    }

    
    /**
     * Returns an instance describing the currently logged-in user/profile.
     */
    public Instance getCurrentUser();
    
    public default List<Instance> getCurrentUserRoles() {
    	throw new UnsupportedOperationException();
    }
    
    public default Instance createUser(Instance userInfo) {
    	return createInstance(userInfo);
    }

    public List<Operation> getEnabledEntityActions(Entity entity);
    
    public default EntityCapabilities getEntityCapabilities(TypeRef entity) {
    	return new EntityCapabilities();
    }
    
    public default Map<TypeRef, EntityCapabilities> getEntityCapabilities(List<TypeRef> entities) {
    	return entities.stream().collect(Collectors.toMap(e -> e, e -> getEntityCapabilities(e)));
    }
    
    public default InstanceCapabilities getInstanceCapabilities(TypeRef entity, String objectId) {
    	return new InstanceCapabilities();
    }

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
    public Instance getInstance(String namespace, String name, String externalId, DataProfile dataProfile);
    
    public default Instance getInstance(String namespace, String name, String externalId, boolean full) {
        return getInstance(namespace, name, externalId, full ? DataProfile.Full : DataProfile.Slim);
    }

    /**
     * Loads all instances of the given entity type.
     * 
     * @param namespace
     * @param name
     * @param full
     *            whether instances should be fully loaded
     * @param includeSubclasses            
     * @param pageRequest optional page request
     * @return
     * @see Instance#isFull()
     */
    public default List<Instance> getInstances(String namespace, String name, PageRequest pageRequest) {
        return getInstances(namespace, name, pageRequest.getDataProfile(), pageRequest.getIncludeSubtypes()); 
    }
    public default List<Instance> getInstances(String namespace, String name, boolean full, boolean includeSubclasses) {
        return getInstances(namespace, name, full ? DataProfile.Full : DataProfile.Slim, includeSubclasses);
    }

    public default List<Instance> getInstances(String namespace, String name, DataProfile dataProfile, boolean includeSubclasses) {
    	if (includeSubclasses) 
    		throw new UnsupportedOperationException("Support for subclasses not implemented");
    	return getInstances(namespace, name, dataProfile);
    }
    
    public default List<Instance> getInstances(String namespace, String name, boolean full) {
        return getInstances(namespace, name, full ? DataProfile.Full : DataProfile.Slim);
    }
    public default List<Instance> getInstances(String namespace, String name, DataProfile dataProfile) {
        return getInstances(namespace, name, dataProfile, true);
    }

    public default Long countInstances(String namespace, String name) {
    	return (long) getInstances(namespace, name, false).size();
    }
    
    public default Long countInstances(Map<String, List<Object>> criteria, String namespace, String name, boolean includeSubclasses) {
    	return (long) filterInstances(criteria, namespace, name, DataProfile.Slim, includeSubclasses).size();
    }
    
    public default List<Instance> filterInstances(Map<String, List<Object>> criteria, String namespace, String name, DataProfile dataProfile) {
        return filterInstances(criteria, namespace, name, dataProfile, false);
    }
    
    public default List<Instance> filterInstances(Map<String, List<Object>> criteria, String namespace, String name, boolean full) {
        return filterInstances(criteria, namespace, name, DataProfile.from(full), true);
    }

    public default List<Instance> filterInstances(Map<String, List<Object>> criteria, String namespace, String name, DataProfile dataProfile, boolean includeSubclasses) {
        return filterInstances(new PageRequest(null, null, dataProfile, includeSubclasses), criteria, namespace, name).contents;
    }
    public default Page<Instance> filterInstances(PageRequest pageRequest, Map<String, List<Object>> criteria, String namespace, String name) {
    	if (pageRequest.getIncludeSubtypes()) 
    		throw new UnsupportedOperationException("Support for subclasses not implemented");
    	List<Instance> instances = filterInstances(criteria, namespace, name, pageRequest.getDataProfile());
        List<Instance> limitedList = instances.stream().skip(pageRequest.getFirst()).limit(pageRequest.getMaximum()).collect(Collectors.toList());
        return new Page<>(limitedList, pageRequest.first, limitedList.size());
    }

    public List<Instance> getParameterDomain(Entity entity, String externalId, Operation action, Parameter parameter);

    public default List<Instance> getRelatedInstances(String namespace, String name, String externalId, String relationship, boolean full) {
        return getRelatedInstances(namespace, name, externalId, relationship, full ? DataProfile.Full : DataProfile.Slim);
    }

    public List<Instance> getRelatedInstances(String namespace, String name, String externalId, String relationship, DataProfile dataProfile);

    
    public default List<Instance> getRelatedInstances(Instance anchorInstance, String relationship, boolean full) {
    	List<Instance> relatedInstances = getRelatedInstances(anchorInstance.getScopeNamespace(), anchorInstance.getScopeName(), anchorInstance.getObjectId(), relationship, full);
		return relatedInstances;
    }

    public List<Instance> getRelationshipDomain(Entity entity, String objectId, Relationship relationship);

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
     * Establishes a link between the two instances through the given
     * relationship.
     * 
     * @param relationship
     * @param sourceId
     * @param destinationId
     */
    public void linkInstances(Relationship relationship, String sourceId, InstanceRef destinationId);
    
    /**
     * Breaks an existing link between two instances through the given
     * relationship.
     * 
     * @param relationship
     * @param sourceId
     * @param destinationId
     */
    public void unlinkInstances(Relationship relationship, String sourceId, InstanceRef destinationId);

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

	default public void deleteBlob(TypeRef entityRef, String objectId, String blobPropertyName, String blob) {
		throw new UnsupportedOperationException();
	}
	default public Blob getBlob(TypeRef entityRef, String objectId, String blobPropertyName, String token) {
        throw new UnsupportedOperationException();
    }
	default public InputStream readBlob(TypeRef entityRef, String objectId, String blobPropertyName, String token) {
		throw new UnsupportedOperationException();
	}
	default public Blob writeBlob(TypeRef entityRef, String objectId, String blobPropertyName, String token, InputStream contents) {
        throw new UnsupportedOperationException();
    }
    default public Blob createBlob(TypeRef entityRef, String objectId, String blobPropertyName, String contentType, String originalName) {
        throw new UnsupportedOperationException();
    }

    default public boolean isRestricted() {
        return true;
    }
}