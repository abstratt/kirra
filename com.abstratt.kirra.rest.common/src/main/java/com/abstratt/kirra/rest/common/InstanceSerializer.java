package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
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

        Map<String, Object> values = instance.getValues();
        // remove non-user visible properties
		entity.getProperties()
			.stream()
			.filter(it -> !it.isUserVisible())
			.forEach(it -> values.remove(it.getName()));
		JsonElement valuesAsJson = gson.toJsonTree(values);
		
        // flatten links to avoid infinite recursion due to cyclic refs
        Map<String, Instance> links = instance.getLinks();
        Set<String> filledInSlotNames = links.keySet();
        Set<String> allSlotNames = entity.getRelationships().stream().map(it -> it.getName()).collect(Collectors.toSet());
        JsonObject linksAsJson = new JsonObject();
        for (Map.Entry<String, Instance> link : links.entrySet()) {
            String relationshipName = link.getKey();
            JsonElement element;
            // only include user visible properties
            if (entity.getRelationship(relationshipName).isUserVisible()) {
	            Instance linkValue = link.getValue();
				if (linkValue == null)
	            	element = JsonNull.INSTANCE;
	            else
	            	element = addBasicProperties(gson, link.getValue(), new JsonObject()); 
	            linksAsJson.add(relationshipName, element);
            }
        }
        JsonObject asJson = new JsonObject();
        asJson.add("links", linksAsJson);
        asJson.add("values", valuesAsJson);
        asJson.add("disabledActions", context.serialize(instance.getDisabledActions()));
    	addBasicProperties(gson, instance, asJson);
        return asJson;
    }
    
    @Override
    public Instance deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        // ensure links don't have sublinks/values and have an objectId
        if (asJsonObject.has("links")) {
            for (Entry<String, JsonElement> entry : asJsonObject.get("links").getAsJsonObject().entrySet()) {
            	if (entry.getValue().isJsonObject()) {
	                JsonObject linkAsObject = entry.getValue().getAsJsonObject();
	                if (linkAsObject.has("uri")) {
	                    JsonElement uri = linkAsObject.get("uri");
	                    if (!uri.isJsonNull()) {
		                    String uriString = uri.getAsString();
		                    String[] segments = StringUtils.split(uriString, "/");
		                    // uri is '...<entity>/instances/<objectId>'
		                    if (segments.length > 0)
		                        linkAsObject.addProperty("objectId", segments[segments.length - 1]);
		                    if (segments.length > 2) {
		                        String linkedTypeName = segments[segments.length - 3];
		                        TypeRef linkType = new TypeRef(linkedTypeName, TypeKind.Entity);
		                        linkAsObject.addProperty("scopeName", linkType.getTypeName());
		                        linkAsObject.addProperty("scopeNamespace", linkType.getNamespace());
		                    }
	                    }
	                }
	                linkAsObject.remove("values");
	                linkAsObject.remove("links");
	                linkAsObject.addProperty("full", false);
            	}
            }
        }
        Instance instance = new Gson().fromJson(jsonElement, Instance.class);
        return instance;
    }

    private JsonObject addBasicProperties(Gson gson, Instance instance, JsonObject instanceAsJson) {
        URI entityUri = CommonHelper.resolve(KirraContext.getBaseURI(), Paths.ENTITIES, instance.getTypeRef().toString());
        URI instanceUri = CommonHelper.resolve(entityUri, Paths.INSTANCES, instance.getObjectId());
        instanceAsJson.addProperty("objectId", instance.getObjectId());
        instanceAsJson.addProperty("uri", instanceUri.toString());
        instanceAsJson.addProperty("shorthand", getShorthand(instance));
        instanceAsJson.addProperty("entityUri", entityUri.toString());
        instanceAsJson.add("typeRef", gson.toJsonTree(instance.getTypeRef()));
        instanceAsJson.addProperty("scopeName", instance.getTypeRef().getTypeName());
        instanceAsJson.addProperty("scopeNamespace", instance.getTypeRef().getNamespace());
        return instanceAsJson;
    }

    private String getShorthand(Instance instance) {
        String shorthand = instance.getShorthand();
        if (shorthand == null) {
            Map<String, Object> valueMap = instance.getValues();
            if (valueMap != null && !valueMap.isEmpty()) {
				Collection<Object> values = valueMap.values();
				shorthand = values.iterator().next().toString();
			}
        }
        return shorthand;
    }

}