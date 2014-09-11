package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
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
    
    @Override
    public Instance deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        // ensure links don't have sublinks/values and have an objectId
        if (asJsonObject.has("links")) {
            for (Entry<String, JsonElement> entry : asJsonObject.get("links").getAsJsonObject().entrySet()) {
                JsonArray linkArray = entry.getValue().getAsJsonArray();
                for (JsonElement link : linkArray) {
                    JsonObject linkAsObject = link.getAsJsonObject();
                    if (linkAsObject.has("uri")) {
                        JsonElement uri = linkAsObject.get("uri");
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
            Map<String, Object> values = instance.getValues();
            if (values != null && !values.isEmpty())
                shorthand = values.values().iterator().next().toString();
        }
        return shorthand;
    }

}