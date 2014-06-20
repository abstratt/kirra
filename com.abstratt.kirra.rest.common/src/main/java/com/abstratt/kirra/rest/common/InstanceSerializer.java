package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

import com.abstratt.kirra.Instance;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class InstanceSerializer implements JsonSerializer<Instance> {

    private URI instancesUri;

    public InstanceSerializer(URI uri) {
        this.instancesUri = uri;
    }

    @Override
    public JsonElement serialize(Instance instance, Type type, JsonSerializationContext context) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mmZ");
        JsonObject asJson = (JsonObject) gsonBuilder.create().toJsonTree(instance);
        if (instancesUri != null) {
	        asJson.addProperty("uri", CommonHelper.resolve(instancesUri, instance.getObjectId()).toString());
	        asJson.addProperty("entityUri", CommonHelper.resolve(instancesUri, "..").toString());
        }
        if (instance.getShorthand() == null) {
            Map<String, Object> values = instance.getValues();
            if (values != null && !values.isEmpty())
                asJson.addProperty("shorthand", values.values().iterator().next().toString());
        }
        return asJson;
    }

}