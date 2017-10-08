package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.Blob;
import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement.DataProfile;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class InstanceSerializer implements JsonSerializer<Instance>, JsonDeserializer<Instance> {

    public InstanceSerializer() {
    }
    
    private static final List<String> EXCLUDED_FIELDS = Arrays.<String>asList(new String[] {
            "links", "objectId", "typeRef"});

    @Override
    public JsonElement serialize(Instance instance, Type type, JsonSerializationContext context) {
        Gson gson = CommonHelper.buildBasicGson().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipClass(Class<?> arg0) {
                return false;
            }
            @Override
            public boolean shouldSkipField(FieldAttributes arg0) {
                return EXCLUDED_FIELDS.contains(arg0.getName());
            }
            
        }).create();
        
        // hide any non-user visible properties and relationships
        Entity entity = KirraContext.getSchemaManagement().getEntity(instance.getTypeRef());

        JsonElement valuesAsJson = getJsonValues(instance, gson, entity);
		
        // flatten links to avoid infinite recursion due to cyclic refs
        Map<String, Instance> links = instance.getLinks();
        JsonObject linksAsJson = new JsonObject();
        for (Map.Entry<String, Instance> link : links.entrySet()) {
            String relationshipName = link.getKey();
            JsonElement element;
            // only include user visible properties
            if (entity.getRelationship(relationshipName).isUserVisible()) {
	            Instance linkValue = link.getValue();
				if (linkValue == null)
	            	element = JsonNull.INSTANCE;
	            else {
	            	Entity relatedEntity = KirraContext.getSchemaManagement().getEntity(linkValue.getTypeRef());
	            	JsonObject relatedAsJson = new JsonObject();
	            	element = addBasicProperties(gson, relatedEntity, linkValue, relatedAsJson);
	            	if (linkValue.getProfile() != DataProfile.Empty) {
	            	    relatedAsJson.add("values", getJsonValues(linkValue, gson, relatedEntity));
	            	}
	            }
	            linksAsJson.add(relationshipName, element);
            }
        }
        JsonObject asJson = new JsonObject();
        asJson.add("links", linksAsJson);
        asJson.add("values", valuesAsJson);
        asJson.add("disabledActions", context.serialize(instance.getDisabledActions()));
    	addBasicProperties(gson, entity, instance, asJson);
        return asJson;
    }

    private JsonElement getJsonValues(Instance instance, Gson gson, Entity entity) {
        Map<String, Object> values = instance.getValues();
        // remove non-user visible properties
		entity.getProperties()
			.stream()
			.filter(it -> !it.isUserVisible())
			.forEach(it -> values.remove(it.getName()));
		JsonElement valuesAsJson = gson.toJsonTree(values);
        return valuesAsJson;
    }
    
    @Override
    public Instance deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();

        String scopeNamespace = asJsonObject.get("scopeNamespace").getAsString();
        String scopeName = asJsonObject.get("scopeName").getAsString();
        String objectId = asJsonObject.has("objectId") ? asJsonObject.get("objectId").getAsString() : null;
        Instance instance = new Instance(scopeNamespace, scopeName);
        instance.setObjectId(objectId);
        Entity entity = KirraContext.getSchemaManagement().getEntity(instance.getTypeRef());
        
        if (asJsonObject.has("values")) {
            for (Entry<String, JsonElement> entry : asJsonObject.get("values").getAsJsonObject().entrySet()) {
                Property property = entity.getProperty(entry.getKey());
                if (property.isUserVisible()) {
                    Object value;
                    if (property.getTypeRef().getKind() == TypeKind.Blob) {
                        value = Blob.fromMap(context.deserialize(entry.getValue(), Map.class));
                    } else {
                        value = context.deserialize(entry.getValue(), Object.class);
                    }
                    instance.setValue(property.getName(), value);
                }
            }
        }

        // ensure links don't have sublinks/values and have an objectId
        if (asJsonObject.has("links")) {
            for (Entry<String, JsonElement> entry : asJsonObject.get("links").getAsJsonObject().entrySet()) {
                Relationship relationship = entity.getRelationship(entry.getKey());
            	if (relationship.isUserVisible() && !relationship.isMultiple()) {
            	    if (entry.getValue().isJsonObject()) {
            	        JsonObject linkAsObject = entry.getValue().getAsJsonObject();
            	        if (linkAsObject.has("uri")) {
            	            JsonElement uri = linkAsObject.get("uri");
            	            if (!uri.isJsonNull()) {
            	                String uriString = uri.getAsString();
            	                String[] segments = StringUtils.split(uriString, "/");
            	                // uri is '...<entity>/instances/<objectId>'
            	                if (segments.length > 2) {
            	                    String relatedObjectId = segments[segments.length - 1];
            	                    String linkedTypeName = segments[segments.length - 3];
            	                    TypeRef linkType = new TypeRef(linkedTypeName, TypeKind.Entity);
            	                    Instance relatedInstance = new Instance(linkType, relatedObjectId);
            	                    relatedInstance.setProfile(DataProfile.Empty);
            	                    instance.setSingleRelated(relationship.getName(), relatedInstance);
            	                }
            	            }
            	        }
            	    } else {
            	        instance.setSingleRelated(relationship.getName(), null);
            	    }
            	}
            }
        }
        
        return instance;
    }

    private JsonObject addBasicProperties(Gson gson, Entity entity, Instance instance, JsonObject instanceAsJson) {
        URI entityUri = CommonHelper.resolve(KirraContext.getBaseURI(), Paths.ENTITIES, instance.getTypeRef().toString());
        URI instanceUri = CommonHelper.resolve(entityUri, Paths.INSTANCES, instance.getObjectId());
        instanceAsJson.addProperty("objectId", instance.getObjectId());
        instanceAsJson.addProperty("uri", instanceUri.toString());
        instanceAsJson.addProperty("shorthand", getShorthand(entity, instance));
        instanceAsJson.addProperty("profile", instance.getProfile().name());
        instanceAsJson.addProperty("entityUri", entityUri.toString());
        instanceAsJson.add("typeRef", gson.toJsonTree(instance.getTypeRef()));
        instanceAsJson.addProperty("scopeName", instance.getTypeRef().getTypeName());
        instanceAsJson.addProperty("scopeNamespace", instance.getTypeRef().getNamespace());
        return instanceAsJson;
    }

    private String getShorthand(Entity entity, Instance instance) {
        String shorthand = instance.getShorthand();
        if (shorthand == null) {
            Map<String, Object> valueMap = instance.getValues();
            if (valueMap != null && !valueMap.isEmpty()) {
				Collection<Object> values = valueMap.values();
				shorthand = Optional.ofNullable(values.iterator().next()).map(it -> it.toString()).orElse(null);
			}
        }
        return shorthand;
    }

}