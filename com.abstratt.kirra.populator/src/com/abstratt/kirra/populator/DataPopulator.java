package com.abstratt.kirra.populator; 

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.abstratt.kirra.Blob;
import com.abstratt.kirra.DataElement;
import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.Repository;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.pluginutils.LogUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

public class DataPopulator {
    class Loader {
        private Map<String, Map<String, List<String>>> instances;
        private List<BlobAction> blobActions;
        private Map<Instance, List<LinkingAction>> simpleLinkingActions;
        private Map<Instance, List<LinkingAction>> multipleLinkingActions;
        
        private Instance createOrResolveRelated(String currentNamespace, JsonNode referenceOrObject, TypeRef type) {
            if (referenceOrObject.isObject()) {
                Entity relatedEntity = repository.getEntity(type.getEntityNamespace(), type.getTypeName());
                return processInstance(relatedEntity, referenceOrObject);
            } else if (referenceOrObject.isTextual()) {
                Instance resolved = resolveReference(currentNamespace, referenceOrObject.textValue());
                return resolved != null ? resolved : null;
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
            List<LinkingAction> simpleInstanceLinkingActions = new LinkedList<>();
            List<LinkingAction> multipleInstanceLinkingActions = new LinkedList<>();
            blobActions = new LinkedList<>();
            for (Iterator<String> propertyNames = instanceNode.fieldNames(); propertyNames.hasNext();) {
                String slotName = propertyNames.next();
                if (validProperties.containsKey(slotName))
                    setProperty(newInstance, instanceNode.get(slotName), validProperties.get(slotName));
                else if (validRelationships.containsKey(slotName)) {
                	Relationship relationship = validRelationships.get(slotName);
                	if (relationship.isMultiple())
                		multipleInstanceLinkingActions.add(new LinkingAction(relationship, instanceNode, slotName));
                	else
                		simpleInstanceLinkingActions.add(new LinkingAction(relationship, instanceNode, slotName));
                }
            }
        
            Instance created = repository.createInstance(newInstance);
            getEntityInstances(entity.getEntityNamespace(), entity.getName()).add(created.getObjectId());
            if (!blobActions.isEmpty()) {
                blobActions.forEach(action -> action.accept(created));
                blobActions.clear();
            }
            multipleLinkingActions.put(created, multipleInstanceLinkingActions);
            simpleLinkingActions.put(created, simpleInstanceLinkingActions);
            return created;
        }

        private int processTree(JsonNode tree) {
        	instances = new LinkedHashMap<String, Map<String, List<String>>>();
            simpleLinkingActions = new LinkedHashMap<>();
            multipleLinkingActions = new LinkedHashMap<>();
            
            for (String namespace : repository.getNamespaces())
                instances.put(namespace, new LinkedHashMap<String, List<String>>());
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
            
            simpleLinkingActions.forEach((instance, actions) -> {
            	actions.forEach(it -> it.accept(instance));
            	repository.updateInstance(instance);
            });
            
            multipleLinkingActions.forEach((instance, actions) -> {
            	actions.forEach(it -> it.accept(instance));
            });
            
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
            String referencedNamespace = ref.getNamespace();
			Map<String, List<String>> namespace = instances.get(referencedNamespace);
            if (namespace == null)
                return null;
            String referencedEntity = ref.getEntity();
			List<String> entity = namespace.get(referencedEntity);
            if (entity == null || entity.size() <= ref.getIndex())
                return null;
            // phew!
            String externalId = entity.get(ref.getIndex());
			return repository.getInstance(referencedNamespace, referencedEntity, externalId, false);
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
                	String asText = propertyValue.asText();
                	value = Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd").stream()
            			.map(pattern -> {
            				try {
								LocalDate parsed = LocalDate.parse(asText, DateTimeFormatter.ofPattern(pattern));
            					return parsed.atStartOfDay();
                            } catch (DateTimeParseException e) {
                                return null;
                            }
            			})
            			.filter(it -> it != null)
            			.findAny()
            			.orElse(null);
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
            case START_OBJECT:
                if (property.getTypeRef().getKind() == TypeKind.Blob) {
            		String contentType = propertyValue.get("contentType").asText();
            		String originalName = Optional.ofNullable(propertyValue.get("originalName")).map(it -> it.asText()).orElse(null);
            		blobActions.add(new BlobAction(property.getName(), propertyValue.get("contents").asText(), contentType, originalName));
            		value = null; 
                }
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
        
        abstract class DelayedAction implements Consumer<Instance> {
            
        }
        
    	class LinkingAction extends DelayedAction { 
			private Relationship relationship;
			private JsonNode instanceNode;
			private String slotName;
			public LinkingAction(Relationship relationship, JsonNode instanceNode, String slotName) {
    			this.relationship = relationship;
    			this.instanceNode = instanceNode;
    			this.slotName = slotName;
    		}
			@Override
			public void accept(Instance instance) {
    			setRelationship(instance, instanceNode.get(slotName), relationship);
    		}
    	}
    	
    	class BlobAction extends DelayedAction {
    	    private String slotName;
            private String contents;
            private String originalName;
            private String contentType;
            BlobAction(String slotName, String contents, String contentType, String originalName) {
    	        this.slotName = slotName;
    	        this.contents = contents;
    	        this.contentType = contentType;
    	        this.originalName = originalName;
    	    }
            @Override
            public void accept(Instance instance) {
                Blob blob = repository.createBlob(instance.getTypeRef(), instance.getObjectId(), slotName, contentType, originalName);
                byte[] asBytes = Base64.getDecoder().decode(contents);
                repository.writeBlob(instance.getTypeRef(), instance.getObjectId(), slotName, blob.getToken(), new ByteArrayInputStream(asBytes));
                LogUtils.debug(ID, () -> instance.getReference().toString() + blob.toMap().toString());
            }
    	}

    }

    private static final String DEFAULT_SNAPSHOT_FILENAME = "data.json";
    public static final Path DEFAULT_SNAPSHOT_PATH = Paths.get(DEFAULT_SNAPSHOT_FILENAME);

    public static String ID = DataPopulator.class.getPackage().getName();

    private Repository repository;

    private Path dataFileName;

    public DataPopulator(Repository repository) { 
        this(repository, DEFAULT_SNAPSHOT_PATH);
    }
    public DataPopulator(Repository repository, Path dataFileName) {
        this.repository = repository;
        this.dataFileName = dataFileName;
    }

    public File getDataFile() {
        // load the sample data
        File repositoryPath;
        try {
            repositoryPath = FileUtils.toFile(this.repository.getRepositoryURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        File dataFile = new File(repositoryPath, this.dataFileName.toString());
        return dataFile;
    }

    public int populate() {
        File dataFile = getDataFile();
        InputStream in = null;
        try {
            // populate with empty data set if data.json not found
        	if (dataFile.isFile()) {
        		in = new BufferedInputStream(new FileInputStream(dataFile), 8192);
        	} else {
        		if (dataFile.getName().equals(DEFAULT_SNAPSHOT_FILENAME)) {
        			in = new ByteArrayInputStream("{}".getBytes());
        		} else {
        			LogUtils.logWarning(DataPopulator.ID, "Snapshot file found " + dataFile, null);
                    return -1;		
        		}
        	}
            return populate(in);
        } catch (IOException e) {
            LogUtils.logWarning(DataPopulator.ID, "Error loading " + dataFile, e);
            return -1;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public int populate(InputStream contents) {
    	boolean wasPopulating = repository.isPopulating();
        try {
            repository.zap();
        	repository.setPopulating(true);
            JsonNode tree = DataParser.parse(new InputStreamReader(contents));
            if (tree == null || !tree.isObject())
                return 0;
            return new Loader().processTree(tree);
        } catch (JsonParseException e) {
            throw new KirraException("Error parsing JSON contents", e, KirraException.Kind.VALIDATION);
        } catch (IOException e) {
            throw new KirraException("Error reading contents", e, KirraException.Kind.VALIDATION);
        } finally {
			repository.setPopulating(wasPopulating);
		}
    }
}
