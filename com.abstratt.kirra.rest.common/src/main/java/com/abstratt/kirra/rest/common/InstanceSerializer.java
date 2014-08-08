package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.abstratt.kirra.Instance;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class InstanceSerializer implements JsonSerializer<Instance> {

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
        JsonObject asJson = (JsonObject) gson.toJsonTree(instance);

        // flatten links to avoid infinite recursion due to cyclic refs
        Map<String, List<Instance>> links = instance.getLinks();
        JsonObject linksAsJson = new JsonObject();
        for (Map.Entry<String, List<Instance>> link : links.entrySet()) {
            JsonArray linkArray = new JsonArray();
            String relationshipName = link.getKey();
            linksAsJson.add(relationshipName, linkArray);
            for (Instance linkedInstance : link.getValue())
                linkArray.add(addBasicProperties(gson, linkedInstance, new JsonObject()));
        }
        asJson.add("links", linksAsJson);
        addBasicProperties(gson, instance, asJson);
        return asJson;
    }

    private JsonObject addBasicProperties(Gson gson, Instance instance, JsonObject instanceAsJson) {
        URI entityUri = CommonHelper.resolve(KirraContext.getBaseURI(), Paths.ENTITIES, instance.getTypeRef().toString());
        instanceAsJson.addProperty("objectId", instance.getObjectId());
        instanceAsJson.addProperty("uri", CommonHelper.resolve(entityUri, Paths.INSTANCES, instance.getObjectId()).toString());
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
            Map<String, Object> values = instance.getValues();
            if (values != null && !values.isEmpty())
                shorthand = values.values().iterator().next().toString();
        }
        return shorthand;
    }

}