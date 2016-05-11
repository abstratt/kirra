package com.abstratt.kirra.populator; 

import java.io.BufferedInputStream; 
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.abstratt.kirra.DataElement;
import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.Repository;
import com.abstratt.kirra.TypeRef;
import com.abstratt.pluginutils.LogUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

public class DataPopulator {
    class Loader {
        private Map<String, Map<String, List<String>>> instances = new HashMap<String, Map<String, List<String>>>();

        private Instance createOrResolveRelated(String currentNamespace, JsonNode referenceOrObject, TypeRef type) {
            if (referenceOrObject.isObject()) {
                Entity relatedEntity = repository.getEntity(type.getEntityNamespace(), type.getTypeName());
                return processInstance(relatedEntity, referenceOrObject);
            } else if (referenceOrObject.isTextual()) {
                Instance resolved = resolveReference(currentNamespace, referenceOrObject.textValue());
                return resolved != null && resolved.isInstanceOf(type) ? resolved : null;
            }
            return null;
        }

        private List<String> getEntityInstances(String namespaceName, String entity) {
            Map<String, List<String>> namespace = instances.get(namespaceName);
            if (!namespace.containsKey(entity))
                namespace.put(entity, new ArrayList<String>());
            return namespace.get(entity);
        }

        private int processEntity(String namespace, final Entry<String, JsonNode> entityNode) {
            final Entity entity;
            entity = repository.getEntity(namespace, entityNode.getKey());
            if (!entity.isStandalone())
                // only standalone entities can be populated this way
                return 0;
            int count = 0;
            if (entity != null && entityNode.getValue().isArray()) {
                for (Iterator<JsonNode> instanceNodes = entityNode.getValue().elements(); instanceNodes.hasNext();) {
                    JsonNode instanceNode = instanceNodes.next();
                    if (instanceNode.isObject() && processInstance(entity, instanceNode) != null)
                        count++;
                }
            }
            return count;
        }

        private Instance processInstance(Entity entity, JsonNode instanceNode) {
            // create a new instance
            Instance newInstance = repository.newInstance(entity.getEntityNamespace(), entity.getName());
            Map<String, Property> validProperties = new HashMap<String, Property>();
            for (Property property : entity.getProperties())
                validProperties.put(property.getName(), property);
            Map<String, Relationship> validRelationships = new HashMap<String, Relationship>();
            for (Relationship relationship : entity.getRelationships())
                validRelationships.put(relationship.getName(), relationship);
            Map<Relationship, JsonNode> multivaluedRelationships = new LinkedHashMap<>();
            for (Iterator<String> propertyNames = instanceNode.fieldNames(); propertyNames.hasNext();) {
                String slotName = propertyNames.next();
                if (validProperties.containsKey(slotName))
                    setProperty(newInstance, instanceNode.get(slotName), validProperties.get(slotName));
                else if (validRelationships.containsKey(slotName)) {
                	Relationship relationship = validRelationships.get(slotName);
					if (relationship.isMultiple())
                		multivaluedRelationships.put(relationship, instanceNode.get(slotName));
                	else 
                		setRelationship(newInstance, instanceNode.get(slotName), relationship);
                }
            }
            Instance created = repository.createInstance(newInstance);
            multivaluedRelationships.forEach((relationship, referencesAsJsonArray) -> {
            	setMultiRelationship(created, referencesAsJsonArray, relationship);
            });
            getEntityInstances(entity.getEntityNamespace(), entity.getName()).add(created.getObjectId());
            return created;
        }

        private int processTree(JsonNode tree) {
            for (String namespace : repository.getNamespaces())
                instances.put(namespace, new HashMap<String, List<String>>());
            int count = 0;
            for (Iterator<Map.Entry<String, JsonNode>> namespaceNodes = tree.fields(); namespaceNodes.hasNext();) {
                Entry<String, JsonNode> namespaceNode = namespaceNodes.next();
                if (instances.containsKey(namespaceNode.getKey()) && namespaceNode.getValue().isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> entityNodes = namespaceNode.getValue().fields();
                    for (; entityNodes.hasNext();) {
                        Entry<String, JsonNode> entityNode = entityNodes.next();
                        count += processEntity(namespaceNode.getKey(), entityNode);
                    }
                }
            }
            return count;
        }

        /**
         * Reference format: [<namespace>:]entity@<index> where index is
         * 1-based.
         */
        private Instance resolveReference(String currentNamespace, String referenceString) {
            Reference ref = Reference.parse(currentNamespace, referenceString);
            if (ref == null)
                return null;
            Map<String, List<String>> namespace = instances.get(ref.getNamespace());
            if (namespace == null)
                return null;
            List<String> entity = namespace.get(ref.getEntity());
            if (entity == null || entity.size() <= ref.getIndex())
                return null;
            // phew!
            return repository.getInstance(ref.getNamespace(), ref.getEntity(), entity.get(ref.getIndex()), false);
        }

        // kirra #31
        private void setMultiRelationship(Instance newInstance, JsonNode jsonNode, Relationship relationship) {
            if (!jsonNode.isArray())
                // no good, multiple relationships expect arrays
                return;
            for (int i = 0; i < jsonNode.size(); i++) {
                Instance related = createOrResolveRelated(newInstance.getEntityNamespace(), jsonNode.get(i), relationship.getTypeRef());
                if (related != null)
                	repository.linkInstances(relationship, newInstance.getObjectId(), related.getReference());
            }
        }

        private void setProperty(Instance newInstance, JsonNode propertyValue, DataElement property) {
            String propertyTypeName = property.getTypeRef().getTypeName();
            Object value = null;
            switch (propertyValue.asToken()) {
            case VALUE_TRUE:
                if (!propertyTypeName.equals("Boolean"))
                    return;
                value = true;
                break;
            case VALUE_FALSE:
                if (!propertyTypeName.equals("Boolean"))
                    return;
                value = false;
                break;
            case VALUE_STRING:
                value = propertyValue.asText();
                if (propertyTypeName.equals("Date")) {
                    try {
                        value = new SimpleDateFormat("yyyy/MM/dd").parse(propertyValue.asText());
                    } catch (ParseException e) {
                        // no good, don't set
                        return;
                    }
                }
                break;
            case VALUE_NUMBER_INT:
                if (!(propertyTypeName.equals("Integer") || propertyTypeName.equals("Double")))
                    return;
                value = propertyValue.asLong();
                break;
            case VALUE_NUMBER_FLOAT:
                if (!propertyTypeName.equals("Double"))
                    return;
                value = propertyValue.asDouble();
                break;
            }
            newInstance.setValue(property.getName(), value);
        }

        private void setRelationship(Instance newInstance, JsonNode jsonNode, Relationship relationship) {
            if (relationship.isMultiple())
                setMultiRelationship(newInstance, jsonNode, relationship);
            else
                setSingleRelationship(newInstance, jsonNode, relationship);
        }

        private void setSingleRelationship(Instance newInstance, JsonNode jsonNode, Relationship relationship) {
            Instance related = createOrResolveRelated(newInstance.getEntityNamespace(), jsonNode, relationship.getTypeRef());
            if (related != null)
                newInstance.setSingleRelated(relationship.getName(), related);
        }
    }

    public static String ID = DataPopulator.class.getPackage().getName();

    private Repository repository;

    public DataPopulator(Repository repository) {
        this.repository = repository;
    }

    public File getDataFile() {
        // load the sample data
        File repositoryPath;
        try {
            repositoryPath = FileUtils.toFile(this.repository.getRepositoryURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        File dataFile = new File(repositoryPath, "data.json");
        return dataFile;
    }

    public int populate() {
        File dataFile = getDataFile();
        InputStream in = null;
        try {
            // populate with empty data set if data.json not found
            in = new BufferedInputStream(dataFile.isFile() ? new FileInputStream(dataFile) : new ByteArrayInputStream("{}".getBytes()),
                    8192);
            return populate(in);
        } catch (IOException e) {
            LogUtils.logWarning(DataPopulator.ID, "Error loading " + dataFile, e);
            return -1;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public int populate(InputStream contents) {
        try {
            JsonNode tree = DataParser.parse(new InputStreamReader(contents));
            if (tree == null || !tree.isObject())
                return 0;
            return new Loader().processTree(tree);
        } catch (JsonParseException e) {
            throw new KirraException("Error parsing JSON contents", e, KirraException.Kind.VALIDATION);
        } catch (IOException e) {
            throw new KirraException("Error reading contents", e, KirraException.Kind.VALIDATION);
        }
    }
}