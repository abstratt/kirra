package com.abstratt.kirra.populator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.abstratt.kirra.Blob;
import com.abstratt.kirra.DataElement;
import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement.DataProfile;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.Repository;
import com.abstratt.kirra.TypeRef.TypeKind;

/**
 * Produces a JSON representation from a repository's data.
 */
public class DataRenderer {
    public static class LazyReference {
        private final InstanceRef reference;
        private Long sequenceNumber;
        public InstanceRef getReference() {
			return reference;
		}

		public Long getSequenceNumber() {
			return sequenceNumber;
		}


        public LazyReference(InstanceRef reference) {
            super();
            this.reference = reference;
        }

        public void setSequenceNumber(Long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
        
    }

    public static String ID = DataRenderer.class.getPackage().getName();
    private Repository repository;

    private Map<InstanceRef, LazyReference> referenceMap;

    public DataRenderer(Repository repository) {
        this.repository = repository;
    }

    public Map<String, Map<String, ?>> render() {
        referenceMap = new HashMap<InstanceRef, LazyReference>();
        Map<String, Map<String, ?>> namespaceMap = new LinkedHashMap<String, Map<String, ?>>();
        for (String namespace : this.repository.getNamespaces()) {
            Map<String, List<?>> renderedNamespace = renderNamespace(namespace);
            namespaceMap.put(namespace, renderedNamespace);
        }
        referenceMap = null;
        return namespaceMap;
    }

    private LazyReference registerReference(Instance instance, Long id) {
        LazyReference reference = referenceMap.get(instance.getReference());
        if (reference == null) {
            reference = new LazyReference(instance.getReference());
            referenceMap.put(instance.getReference(), reference);
        }
        if (id != null)
            reference.setSequenceNumber(id);
        return reference;
    }

    /**
     * Renders an instance.
     * 
     * @param instance
     * @param output
     */
    private Map<String, Object> renderInstance(Entity entity, Instance instance) {
        Map<String, Object> instanceMap = new LinkedHashMap<String, Object>();
        for (DataElement property : entity.getProperties())
            if (!property.isDerived() && !property.isMultiple())
                instanceMap.put(property.getName(), getPropertyValue(instance, property));
        for (Relationship relationship : entity.getRelationships())
            if (!relationship.isDerived() && relationship.isPrimary()) {
                if (!relationship.isMultiple()) {
                    Instance related = instance.getSingleRelated(relationship.getName());
                    if (related != null || relationship.isRequired())
                        instanceMap.put(relationship.getName(), related == null ? null : registerReference(related, null));
                } else {
                    List<Instance> relateds = repository.getRelatedInstances(entity.getNamespace(), entity.getName(),
                            instance.getObjectId(), relationship.getName(), false);
                    if (relateds != null) {
                        List<LazyReference> references = new ArrayList<DataRenderer.LazyReference>();
                        for (Instance related : relateds)
                            references.add(registerReference(related, null));
                        instanceMap.put(relationship.getName(), references);
                    }
                }
            }
        return instanceMap;
    }

    private Object getPropertyValue(Instance instance, DataElement property) {
        if (property.getTypeRef().getKind() == TypeKind.Blob) {
            return getBlobValue(instance, property);
        }
        return instance.getValue(property.getName());
    }

    private Map<String, Object> getBlobValue(Instance instance, DataElement property) {
        Blob blobValue = (Blob) instance.getValue(property.getName());
        if (blobValue == null)
            return null;
        Map<String, Object> asMap = blobValue.toMap();
        byte[] asBytes;
        try (InputStream blobContents = repository.readBlob(instance.getTypeRef(), instance.getObjectId(), property.getName(), blobValue.getToken())) {
            asBytes = IOUtils.toByteArray(blobContents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String asString = Base64.getEncoder().encodeToString(asBytes);
        asMap.put("contents", asString);
        return asMap;
    }

    /**
     * Renders an entity's instances.
     * 
     * @param entity
     * @param output
     */
    private List<Map<String, ?>> renderInstances(Entity entity) {
        List<Map<String, ?>> renderedInstances = new ArrayList<Map<String, ?>>();
        List<Instance> entityInstances = repository.getInstances(entity.getEntityNamespace(), entity.getName(), DataProfile.Full, false);
        long id = 0;
        for (Instance instance : entityInstances) {
            registerReference(instance, ++id);
            renderedInstances.add(renderInstance(entity, instance));
        }
        return renderedInstances;
    }

    /**
     * Renders the namespace.
     * 
     * @param output
     * @param namespace
     */
    private Map<String, List<?>> renderNamespace(String namespace) {
        List<Entity> entities = repository.getEntities(namespace);
        EntitySorter.sort(entities);
        Map<String, List<?>> entityMap = new LinkedHashMap<String, List<?>>();
        for (Entity entity : entities)
            if (entity.isConcrete()) {
                List<Map<String, ?>> renderInstances = renderInstances(entity);
                entityMap.put(entity.getName(), renderInstances);
            }
        return entityMap;
    }
}
